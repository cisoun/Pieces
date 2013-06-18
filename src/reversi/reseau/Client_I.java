package reversi.reseau;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Client_I extends Remote {
	public void deconnexion() throws RemoteException;
	public void handshake() throws RemoteException;
	public void message(String texte, Color couleur, boolean persistant, boolean urgent) throws RemoteException;
	public void poserPiece(int index) throws RemoteException;
}
