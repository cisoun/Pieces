package pieces;

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import pieces.gui.GUI;
import pieces.gui.Message;
import pieces.network.IClient;
import pieces.network.Network;
import pieces.utils.Config;
import pieces.utils.IA;
import pieces.utils.Matrix;
import pieces.utils.Player;
import pieces.utils.Matrix.MatrixPiece;

public class Game extends UnicastRemoteObject implements IClient {
	private static final long serialVersionUID = 1L;
	public static final String APP_NAME = "Pieces";
	private final int RANGEE = 8;
	private final int DELTA = 1000;

	public Player playerBlack;
	public Player playerWhite;
	private GUI gui;
	public Matrix matrice;
	private boolean tour;
	private boolean multiplayer;
	private boolean player;
	public static final boolean BLACK = false;
	public static final boolean WHITE = true;

	public Game() throws RemoteException {
		// Matrice.
		matrice = new Matrix(RANGEE);

		// Joueurs.
		playerBlack = new Player(Player.NOIR, Player.TYPE_HUMAIN);
		playerWhite = new Player(Player.NOIR, Player.TYPE_IA1);

		// Interface.
		gui = new GUI(this);

		// Lancement d'une nouvelle partie.
		newGame();
	}

	public void afficherScores() {
		gui.getScores().afficher();
	}

	public void changerTour() {
		tour = !tour;
		chercherMouvements();
		gui.changerTour();
		/*if (matrice.coupsPossibles.size() == 0)
		{
			tour = !tour;
			chercherMouvements();
			if (matrice.coupsPossibles.size() == 0)
			{
				message("Match nul.", Message.ATTENTION, true, true);
				return;
			}
		}*/

		if (!multiplayer)
			joueurIA(tour);
	}

	public void chercherMouvements() {
		matrice.chercherMouvements(tour ? MatrixPiece.BLANC : MatrixPiece.NOIR);
		gui.repaint();
	}

	@Override
	public void logout() {
		multiplayer = false;
		gui.multijoueur();
		try {
			Network.logout(player);
			newGame();
			message("La partie a été interrompue.", Message.ATTENTION, false, true);
		} catch (RemoteException e) {
			message("Une erreur est survenue lors de l'arrêt de la partie.", Message.ERREUR, false, true);
			e.printStackTrace();
		}
	}

	@Override
	public void handshake() {
		message("Adversaire connecté !", Message.OK, false, true);
		gui.revalidate();
		gui.repaint();
	}

	public boolean isHote() {
		return player;
	}

	public boolean isJouable() {
		if (multiplayer) {
			try {
				return tour == player && Network.canPlay();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			if (playerWhite.isIA() && tour != BLACK)
				return false;
			if (playerBlack.isIA() && tour != WHITE)
				return false;
			return true;
		}
	}

	public boolean isMultijoueur() {
		return multiplayer;
	}

	@Override
	public void message(String texte, Color couleur, boolean persistant, boolean urgent) {
		if (gui != null)
			gui.message(texte, couleur, persistant, urgent);
	}

	public void multiplayer() {
		updateJoueurs();
		try {
			Network.creerServeur(this);
			newGame();
			multiplayer = true;
			tour = false;
			player = BLACK;
			gui.multijoueur();
			message("Une partie multijoueur a été lancée !", Message.OK, false, true);
			message("Attente d'un autre joueur...", Message.NEUTRE, true, false);
		} catch (Exception e) {
			message("Impossible de créer une partie en ligne. Voir la console pour plus d'informations", Message.ERREUR, false, false);
			e.printStackTrace();
		}
	}

	public void newGame() {
		if (multiplayer)
			logout();

		matrice.reinitialiser();
		matrice.set(3, 3, MatrixPiece.BLANC);
		matrice.set(4, 3, MatrixPiece.NOIR);
		matrice.set(3, 4, MatrixPiece.NOIR);
		matrice.set(4, 4, MatrixPiece.BLANC);

		tour = false;
		playerBlack.reinitialiser();
		playerWhite.reinitialiser();

		gui.nouvellePartie();

		matrice.chercherMouvements(MatrixPiece.NOIR);

		if (multiplayer)
			return;
		updateJoueurs();
	}
	
	private void joueurIA(final boolean joueur)
	{
		final Player joueurIA = joueur ? playerWhite : playerBlack;
		if (joueurIA.isIA()) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(DELTA);
						if (joueurIA.isIA()) {
							if (matrice.coupsPossibles.size() > 0) {
								int index = IA.meilleurCoup(matrice, matrice.coupsPossibles, joueurIA.getType() - 1);
								gui.poserPiece(index);
								poserPiece(index);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}

	@Override
	public void poserPiece(int piece) {
		int type = tour() ? MatrixPiece.BLANC : MatrixPiece.NOIR;
		matrice.poser(piece, type);

		if (multiplayer) {
			try {
				// Si c'est notre tour, avertir le serveur du coup.
				if (isJouable())
					Network.poserPiece(piece);
				else
					gui.poserPiece(piece);
			} catch (RemoteException e) {
				message("Erreur lors de la transmission du coup.", Message.ERREUR, false, true);
				e.printStackTrace();
			}
		}
		changerTour();
	}

	public void rejoindre() {
		try {
			message("Connexion en cours...", Message.ATTENTION, true, true);
			Network.login(this);
			newGame();
			tour = false;
			multiplayer = true;
			player = WHITE;
			gui.multijoueur();
			message("Connexion réussie !", Message.OK, false, true);
		} catch (Exception e) {
			message("Impossible de rejoindre la partie.", Message.ERREUR, false, true);
			e.printStackTrace();
		}
	}

	public boolean tour() {
		return tour;
	}

	public void updateJoueurs() {
		playerBlack.setType(Config.get(Config.DIFFICULTY_BLACK, Player.TYPE_HUMAIN));
		playerWhite.setType(Config.get(Config.DIFFICULTY_WHITE, Player.TYPE_IA1));
		gui.repaint();
		joueurIA(tour);
	}
}
