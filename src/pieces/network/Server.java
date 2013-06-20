package pieces.network;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import pieces.gui.Message;

public class Server extends UnicastRemoteObject implements IServer {
	private static final long serialVersionUID = 1L;

	private IClient host;
	private IClient client;
	private boolean round;
	private boolean playable;

	/**
	 * Game's server.
	 * @param host
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	public Server(IClient host) throws AccessException, RemoteException, NotBoundException, MalformedURLException {
		this.host = host;
		this.round = false;
		this.playable = false;
	}

	@Override
	public boolean canPlay() throws RemoteException {
		return playable;
	}

	@Override
	public void login(IClient client) throws RemoteException, ServerFullException {
		// Check if server has already two players.
		// Otherwise throw an exception.
		if (playable) {
			throw new ServerFullException();
		}
	
		// Join the client to the server and set the server as ready to play.
		this.client = client;
		this.playable = true;
		
		// Notify the host that the client is logged in.
		this.host.handshake();
	}

	/**
	 * Disconnect the players.
	 */
	@Override
	public void logout(boolean player) throws RemoteException {
		if (player) {
			// Le client se déconnecte.
			host.message("L'adversaire s'est déconnecté.", Message.WARNING, false, true);
			client = null;
		} else {
			// Le serveur se déconnecte.
			if (client != null)
				client.logout();
		}
		playable = false;
	}


	@Override
	public void play(int piece) throws RemoteException {
		if (!round)
			client.play(piece);
		else
			host.play(piece);
		round = !round;
	}
}
