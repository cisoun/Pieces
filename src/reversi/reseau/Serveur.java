package reversi.reseau;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import reversi.gui.Message;

public class Serveur extends UnicastRemoteObject implements Serveur_I {
	private String url;
	private Client_I hote;
	private Client_I client;
	
	private boolean tour;
	private boolean jouable;

	/**
	 * Serveur du Reversi.
	 * @param port 
	 * @param hote
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	public Serveur(Client_I hote) throws AccessException, RemoteException, NotBoundException, MalformedURLException {
		this.hote = hote;
		this.tour = false;
		this.jouable = false;
	}
	
	/**
	 * 
	 */
	public void deconnexion(boolean joueur) throws RemoteException {
		if (joueur)
		{
			// Le client se déconnecte.
			hote.message("L'adversaire s'est déconnecté.", Message.ATTENTION, false, true);
			client = null;
		}
		else
		{
			// Le serveur se déconnecte.
			if (client != null)
				client.deconnexion();
				//client.message("L'adversaire s'est déconnecté.", Message.ATTENTION, false, true);
		}
		jouable = false;
	}

	public boolean isJouable() throws RemoteException {
		return jouable;
	}
	
	public void poserPiece(int piece) throws RemoteException {
		if (!tour)
			client.poserPiece(piece);
		else
			hote.poserPiece(piece);
		tour = !tour;
	}
	
	public void rejoindre(Client_I client) throws RemoteException {
		this.client = client;
		this.hote.handshake();
		this.jouable = true;
	}
}
