package pieces.network;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote {
	public void logout() throws RemoteException;
	public void handshake() throws RemoteException;
	public void message(String texte, Color couleur, boolean persistant, boolean urgent) throws RemoteException;
	public void poserPiece(int index) throws RemoteException;
}
