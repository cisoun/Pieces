package pieces.network;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import pieces.Game;
import pieces.network.IServer.ServerFullException;
import pieces.utils.Config;



public class Network {
	public static final int DEFAULT_PORT = 9001;
	public static final String DEFAULT_SERVER = "127.0.0.1";
	public static final String POLICY = "file:./pieces.policy";

	private static IServer server;

	/**
	 * Create a game server to the host.
	 * @param host Host's client.
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	public static void createServer(IClient host) throws AccessException, RemoteException, NotBoundException, MalformedURLException
	{
		int port = Config.get(Config.PORT, DEFAULT_PORT);
		
		// Full authorization to use the socket.
		System.setProperty("java.security.policy", POLICY);
		
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		
		// RMI
		server = new Server(host);
		Registry registry = LocateRegistry.createRegistry(port);
		registry.rebind(Game.APP_NAME, server);
	}
	
	/**
	 * Log the players out.
	 * @param Player who terminated the game.
	 * @throws RemoteException
	 */
	public static void logout(boolean player) throws RemoteException
	{
		server.logout(player);
	}
	
	/**
	 * Obtain the game's state.
	 * It is considered playable as long as there is a host and a client.
	 * @return Playable ?
	 * @throws RemoteException
	 */
	public static boolean canPlay() throws RemoteException
	{
		return server.canPlay();
	}

	/**
	 * Put a piece to the inactive player.
	 * @param piece Played piece.
	 * @throws RemoteException
	 */
	public static void putPiece(int piece) throws RemoteException
	{
		server.play(piece);
	}
	
	/**
	 * Log a player to the server.
	 * @param client Client reversi.
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws ServerFullException 
	 */
	public static void login(IClient client) throws RemoteException, NotBoundException, ServerFullException
	{
		String address = Config.get(Config.SERVER, DEFAULT_SERVER);
		int port = Config.get(Config.PORT, DEFAULT_PORT);
		
		// Autorisation complète d'utiliser le socket.
		System.setProperty("java.security.policy", POLICY);
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		Registry registry = LocateRegistry.getRegistry(address, port);
		server = (IServer) registry.lookup(Game.APP_NAME);
		server.login(client);
	}
}
