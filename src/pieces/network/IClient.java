package pieces.network;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Client's interface.
 * 
 * @author cyriaque
 * 
 */
public interface IClient extends Remote {
	/**
	 * Signal sent when opponent is known.
	 * 
	 * @throws RemoteException
	 */
	public void handshake() throws RemoteException;

	/**
	 * Disconnects from a game.
	 * 
	 * @throws RemoteException
	 */
	public void logout() throws RemoteException;

	/**
	 * Send a message to the client.
	 * 
	 * @param message
	 * @param color
	 * @param persistant Stays at the front.
	 * @param urgent Shows immediatly.
	 * @throws RemoteException
	 */
	public void message(String message, Color color, boolean persistant, boolean urgent) throws RemoteException;

	/**
	 * Plays a piece and notify the server.
	 * 
	 * @param piece
	 * @throws RemoteException
	 */
	public void play(int piece) throws RemoteException;
}
