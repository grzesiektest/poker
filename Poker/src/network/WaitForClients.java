package network;

import java.io.IOException;
import java.net.Socket;

public class WaitForClients extends Thread {
	private Server server;

	public WaitForClients(Server server) {
		this.server = server;
	}

	public void run() {
		while (server.isRunning()) {

			
			Socket clientSocket;
			try {
				clientSocket = server.serverSocket.accept();

				Connection connection = new Connection(clientSocket);
				server.getConnections().add(connection);
				connection.start();
			} catch (IOException e) {
			}
		}
	}

}
