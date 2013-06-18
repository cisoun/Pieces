package reversi.reseau;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface Serveur_I extends Remote {
	public void deconnexion(boolean joueur) throws RemoteException;
	public boolean isJouable() throws RemoteException;
	public void poserPiece(int piece) throws RemoteException;
	public void rejoindre(Client_I client) throws RemoteException;
}
