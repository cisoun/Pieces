package pieces.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Server's interface.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public interface IServer extends Remote {
	/**
	 * Check if the game is playable online.
	 * 
	 * @return State of the game.
	 * @throws RemoteException
	 */
	public boolean canPlay() throws RemoteException;

	/**
	 * Connect a client (or host) to the server.
	 * 
	 * @param client
	 * @throws RemoteException
	 * @throws ServerFullException
	 */
	public void login(IClient client) throws RemoteException, ServerFullException;

	/**
	 * Disconnect the players.
	 * 
	 * @param player
	 * @throws RemoteException
	 */
	public void logout(boolean player) throws RemoteException;

	/**
	 * Play a piece.
	 * 
	 * @param piece
	 * @throws RemoteException
	 */
	public void play(int piece) throws RemoteException;

	/*
	 * Exception thrown when the game has already two players and a client try
	 * to join it.
	 */
	@SuppressWarnings("serial")
	public class ServerFullException extends Exception {
		public ServerFullException() {
			System.err.println("Two players are already playing on this server.");
		}
	}
}
