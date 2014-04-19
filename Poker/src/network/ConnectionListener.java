package network;

import java.util.Iterator;

public class ConnectionListener extends Thread {

	private static final int USER_COUNT = 2;

	private Server server;

	private boolean canJoinToGame = false;

	private int clientsCount = 0;

	private int joinedClientsCount = 0;

	private int readyClientsCount = 0;

	private void canJoinToGame(boolean b) {
		canJoinToGame = b;
		GameEvent geOut;
		if (canJoinToGame) {
			geOut = new GameEvent(GameEvent.SB_CAN_JOIN_GAME);
		} else {
			geOut = new GameEvent(GameEvent.SB_CANNOT_JOIN_GAME);
		}
		sendBroadcastMessage(geOut);
	}

	private void startGame() {
		readyClientsCount = 0;
		GameEvent geOut;
		geOut = new GameEvent(GameEvent.SB_START_GAME);
		int count = 1;

		for (Connection connection : server.getConnections()) {
			if (connection.isAlive() && connection.isJoined()) {
				geOut.setMessage(Integer.toString(count));
				sendMessage(connection, geOut);
				count++;
			}
		}
	}

	public ConnectionListener(Server server) {
		this.server = server;
	}

	public void run() {
		while (server.isRunning()) {
			for (int i = server.getConnections().size() - 1; i >= 0; --i) {
				Connection connection = (Connection) server.getConnections()
						.get(i);

				if (!connection.isAlive()) {
					if (connection.getNick() != "") {
						GameEvent geOut;
						geOut = new GameEvent(GameEvent.SB_PLAYER_QUIT);
						sendBroadcastMessage(geOut);
						clientsCount--;
						joinedClientsCount = 0;
						//canJoinToGame(false);
					}
					connection.close();
					server.getConnections().remove(connection);
				} else {
					GameEvent ge;
					while ((ge = receiveMessage(connection)) != null) {
						switch (ge.getType()) {
						case GameEvent.C_CHAT_MSG:
							if (ge.getPlayerId() != "") {
								GameEvent geOut;
								geOut = new GameEvent(GameEvent.SB_CHAT_MSG, ge
										.getMessage());
								geOut.setPlayerId(ge.getPlayerId());
								sendBroadcastMessage(geOut);
							}
							break;
						case GameEvent.C_LOGIN:
							if (ge.getPlayerId() != "") {
								if (clientsCount == USER_COUNT) {
									GameEvent geOut;
									geOut = new GameEvent(
											GameEvent.S_LOGIN_FAIL,
											"W grze znajduje się już dwóch graczy!");
									sendMessage(connection, geOut);
									geOut = new GameEvent(
											GameEvent.S_TOO_MANY_CONNECTIONS);
									sendMessage(connection, geOut);
								} else if (isPlayerIDUnique(ge.getPlayerId())) {
									connection.setNick(ge.getPlayerId());
									GameEvent geOut;
									geOut = new GameEvent(GameEvent.SB_LOGIN,
											ge.getPlayerId());
									sendBroadcastMessage(geOut);
									clientsCount++;
									if (clientsCount == USER_COUNT) {
										canJoinToGame(true);
									}
								} else {
									GameEvent geOut;
									geOut = new GameEvent(
											GameEvent.S_LOGIN_FAIL,
											"Użytkownik \"" + ge.getPlayerId()
													+ "\" już istnieje");
									sendMessage(connection, geOut);
									geOut = new GameEvent(
											GameEvent.S_USER_EXIST);
									sendMessage(connection, geOut);
								}
							}
							break;
						case GameEvent.C_JOIN_GAME:
							if (connection.getNick() != "") {
								if (clientsCount != USER_COUNT) {
									GameEvent geOut;
									geOut = new GameEvent(
											GameEvent.S_JOIN_GAME_FAIL);
									sendMessage(connection, geOut);
								} else {
									connection.setJoined(true);
									GameEvent geOut;
									geOut = new GameEvent(
											GameEvent.S_JOIN_GAME_OK);
									sendMessage(connection, geOut);
									geOut = new GameEvent(
											GameEvent.SB_PLAYER_JOINED, ge
													.getPlayerId());
									sendBroadcastMessage(geOut);

									joinedClientsCount++;
									if (joinedClientsCount == USER_COUNT) {
										startGame();
									}
								}
							}
							break;
						case GameEvent.C_READY:
							if (connection.getNick() != "") {
								readyClientsCount++;
								if (readyClientsCount == USER_COUNT) {
									GameEvent geOut;
									geOut = new GameEvent(
											GameEvent.SB_ALL_READY);
									sendBroadcastMessage(geOut);
								}
							}
							break;
						case GameEvent.C_SHOT:
							if (ge.getPlayerId() != "") {
								GameEvent geOut;
								geOut = new GameEvent(GameEvent.SB_SHOT, ge
										.getMessage());
								geOut.setPlayerId(ge.getPlayerId());
								sendBroadcastMessage(geOut);
							}
							break;
						case GameEvent.C_SHOT_RESULT:
							if (ge.getPlayerId() != "") {
								GameEvent geOut;
								geOut = new GameEvent(GameEvent.SB_SHOT_RESULT,
										ge.getMessage());
								geOut.setPlayerId(ge.getPlayerId());
								sendBroadcastMessage(geOut);
							}
							break;

						case GameEvent.C_QUIT_GAME:
								joinedClientsCount = 0;
							break;
						}
					}
				}
			}

			try {
				Thread.sleep(50);
			} catch (Exception ex) {
			}
		}
	}

	public void sendMessage(Connection connection, GameEvent ge) {
		connection.sendMessage(ge.toSend());
	}

	public void sendBroadcastMessage(GameEvent ge) {
		Iterator i = server.getConnections().iterator();
		while (i.hasNext()) {
			Connection connection = (Connection) i.next();
			if (connection.isAlive()) {
				sendMessage(connection, ge);
			}
		}
	}

	public GameEvent receiveMessage(Connection connection) {
		if (connection.messagesQueue.isEmpty()) {
			return null;
		} else {
			GameEvent ge = new GameEvent((String) connection.messagesQueue
					.getFirst());
			connection.messagesQueue.removeFirst();
			return ge;
		}
	}

	public boolean isPlayerIDUnique(String nick) {
		Iterator i = server.getConnections().iterator();
		while (i.hasNext()) {
			Connection connection = (Connection) i.next();
			if (connection.getNick().compareTo(nick) == 0)
				return false;
		}
		return true;
	}

}
