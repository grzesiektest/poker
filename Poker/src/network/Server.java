package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;

public class Server {
	private int port;

	public ServerSocket serverSocket;

	private LinkedList<Connection> connections;

	public ConnectionListener connectionListener;

	private WaitForClients waitForClients;

	private boolean isRunning;

	public Server(int port) {
		this.port = port;
		isRunning = false;
	}

	public boolean start() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception ex) {
			return false;
		}

		isRunning = true;

		connections = new LinkedList<Connection>();
		connectionListener = new ConnectionListener(this);
		connectionListener.start();
		waitForClients = new WaitForClients(this);
		waitForClients.start();

		return true;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void stop() {
		isRunning = false;

		connectionListener.interrupt();
		waitForClients.interrupt();

		try {
			for (int i = 0; i < connections.size(); i++) {
				Connection connection = (Connection) connections.get(i);
				connection.close();
			}
			connections.clear();
			serverSocket.close();
		} catch (IOException ex) {
		}
	}

	public LinkedList<Connection> getConnections() {
		return connections;
	}
	
	public int getConnectionsCount() {
		return connections.size();
	}
}
