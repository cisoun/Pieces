package pieces.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServer extends Remote {
	public void logout(boolean joueur) throws RemoteException;
	public boolean canPlay() throws RemoteException;
	public void play(int piece) throws RemoteException;
	public void rejoindre(IClient client) throws RemoteException;
}
