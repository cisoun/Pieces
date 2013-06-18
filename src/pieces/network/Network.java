package pieces.network;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import pieces.Game;
import pieces.utils.Config;



public class Network {
	public static final int DEFAULT_PORT = 9001;
	public static final String DEFAULT_SERVER = "127.0.0.1";
	public static final String POLICY = "file:./server.policy";

	private static IServer server;

	/**
	 * Créé un serveur de jeu.
	 * @param host Client reversi faisant hôte.
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	public static void creerServeur(IClient host) throws AccessException, RemoteException, NotBoundException, MalformedURLException
	{
		int port = Config.get(Config.PORT, DEFAULT_PORT);
		
		//System.setProperty("java.rmi.server.hostname", "127.0.0.1");
		
		// Autorisation complète d'utiliser le socket.
		System.setProperty("java.security.policy", POLICY);
		
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		server = new Server(host);
		
		// Initialisation du RMI.
		Registry registry = LocateRegistry.createRegistry(port);
		registry.rebind(Game.APP_NAME, server);
	}
	
	/**
	 * Déconnecte les joueurs.
	 * @param Joueur responsable de la déconnexion.
	 * @throws RemoteException
	 */
	public static void logout(boolean player) throws RemoteException
	{
		server.logout(player);
	}
	
	/**
	 * Permet d'obtenir l'état du jeu.
	 * On considère qu'il est jouable du moment qu'il y a un hôte et un client.
	 * @return Est-il jouable ?
	 * @throws RemoteException
	 */
	public static boolean canPlay() throws RemoteException
	{
		return server.canPlay();
	}

	/**
	 * Pose une pièce au joueur inactif.
	 * @param piece Pièce retournée par le joueur actif.
	 * @throws RemoteException
	 */
	public static void poserPiece(int piece) throws RemoteException
	{
		server.play(piece);
	}
	
	/**
	 * Permet à un client de se connecter sur le serveur.
	 * @param client Client reversi.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public static void login(IClient client) throws RemoteException, NotBoundException
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
		server.rejoindre(client);
	}
}
