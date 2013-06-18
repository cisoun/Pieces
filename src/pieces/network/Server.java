package pieces.network;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import pieces.gui.Message;

public class Server extends UnicastRemoteObject implements IServer {
	private static final long serialVersionUID = 1L;

	private String url;
	private IClient hote;
	private IClient client;

	private boolean tour;
	private boolean jouable;

	/**
	 * Serveur du Reversi.
	 * 
	 * @param port
	 * @param hote
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	public Server(IClient hote) throws AccessException, RemoteException, NotBoundException, MalformedURLException {
		this.hote = hote;
		this.tour = false;
		this.jouable = false;
	}

	/**
	 * 
	 */
	@Override
	public void logout(boolean joueur) throws RemoteException {
		if (joueur) {
			// Le client se déconnecte.
			hote.message("L'adversaire s'est déconnecté.", Message.ATTENTION, false, true);
			client = null;
		} else {
			// Le serveur se déconnecte.
			if (client != null)
				client.logout();
			// client.message("L'adversaire s'est déconnecté.",
			// Message.ATTENTION, false, true);
		}
		jouable = false;
	}

	@Override
	public boolean canPlay() throws RemoteException {
		return jouable;
	}

	@Override
	public void play(int piece) throws RemoteException {
		if (!tour)
			client.poserPiece(piece);
		else
			hote.poserPiece(piece);
		tour = !tour;
	}

	@Override
	public void rejoindre(IClient client) throws RemoteException {
		this.client = client;
		this.hote.handshake();
		this.jouable = true;
	}
}
