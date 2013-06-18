package reversi.reseau;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import reversi.utils.Config;

public class Reseau {
	public static final int PORT_DEFAUT = 9001;
	public static final String SERVEUR_DEFAUT = "127.0.0.1";

	private static Serveur_I serveur;

	/**
	 * Créé un serveur de jeu.
	 * @param hote Client reversi faisant hôte.
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws MalformedURLException
	 */
	public static void creerServeur(Client_I hote) throws AccessException, RemoteException, NotBoundException, MalformedURLException
	{
		String adresse = Config.get(Config.SERVEUR, SERVEUR_DEFAUT);
		int port = Config.get(Config.PORT, PORT_DEFAUT);
		
		//System.setProperty("java.rmi.server.hostname", "127.0.0.1");
		
		// Autorisation complète d'utiliser le socket.
		System.setProperty("java.security.policy", "file:./serveur.policy");
		
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		serveur = new Serveur(hote);
		
		// Initialisation du RMI.
		Registry registry = LocateRegistry.createRegistry(port);
		registry.rebind("Pieces", serveur);
	}
	
	/**
	 * Déconnecte les joueurs.
	 * @param Joueur responsable de la déconnexion.
	 * @throws RemoteException
	 */
	public static void deconnexion(boolean joueur) throws RemoteException
	{
		serveur.deconnexion(joueur);
	}
	
	/**
	 * Permet d'obtenir l'état du jeu.
	 * On considère qu'il est jouable du moment qu'il y a un hôte et un client.
	 * @return Est-il jouable ?
	 * @throws RemoteException
	 */
	public static boolean isJouable() throws RemoteException
	{
		return serveur.isJouable();
	}

	/**
	 * Pose une pièce au joueur inactif.
	 * @param piece Pièce retournée par le joueur actif.
	 * @throws RemoteException
	 */
	public static void poserPiece(int piece) throws RemoteException
	{
		serveur.poserPiece(piece);
	}
	
	/**
	 * Permet à un client de se connecter sur le serveur.
	 * @param client Client reversi.
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public static void rejoindre(Client_I client) throws RemoteException, NotBoundException
	{
		String adresse = Config.get(Config.SERVEUR, SERVEUR_DEFAUT);
		int port = Config.get(Config.PORT, PORT_DEFAUT);
		
		// Autorisation complète d'utiliser le socket.
		System.setProperty("java.security.policy", "file:./serveur.policy");
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		
		Registry registry = LocateRegistry.getRegistry(adresse, port);
		serveur = (Serveur_I) registry.lookup("Pieces");
		serveur.rejoindre(client);
	}
}
