package reversi;

import java.awt.Color;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;

import javax.swing.JOptionPane;

import reversi.gui.GUI;
import reversi.gui.Grille;
import reversi.gui.Message;
import reversi.reseau.Client_I;
import reversi.reseau.Reseau;
import reversi.reseau.Serveur;
import reversi.utils.Config;
import reversi.utils.Coordonnees;
import reversi.utils.IA;
import reversi.utils.Joueur;
import reversi.utils.Matrice;
import reversi.utils.Utils;
import reversi.utils.Matrice.Piece;

public class Reversi extends UnicastRemoteObject implements Client_I {
	public static final String NOM = "Pieces";
	private final int RANGEE = 8;
	private final int PIECES = RANGEE * RANGEE;
	private final int DELTA = 1000;

	public Joueur joueurNoir;
	public Joueur joueurBlanc;
	private GUI gui;
	public Matrice matrice;
	private boolean tour;
	private boolean multijoueur;
	private boolean joueur;
	public static final boolean NOIR = false;
	public static final boolean BLANC = true;

	public Reversi() throws RemoteException {
		// Matrice.
		matrice = new Matrice(RANGEE);

		// Joueurs.
		joueurNoir = new Joueur(Joueur.NOIR, Joueur.TYPE_HUMAIN);
		joueurBlanc = new Joueur(Joueur.NOIR, Joueur.TYPE_IA1);

		// Interface.
		gui = new GUI(this);

		// Lancement d'une nouvelle partie.
		nouvellePartie();
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

		if (!multijoueur)
			joueurIA(tour);
	}

	public void chercherMouvements() {
		matrice.chercherMouvements(tour ? Piece.BLANC : Piece.NOIR);
		gui.repaint();
	}

	public void deconnexion() {
		multijoueur = false;
		gui.multijoueur();
		try {
			Reseau.deconnexion(joueur);
			nouvellePartie();
			message("La partie a été interrompue.", Message.ATTENTION, false, true);
		} catch (RemoteException e) {
			message("Une erreur est survenue lors de l'arrêt de la partie.", Message.ERREUR, false, true);
			e.printStackTrace();
		}
	}

	public void handshake() {
		message("Adversaire connecté !", Message.OK, false, true);
		gui.revalidate();
		gui.repaint();
	}

	public boolean isHote() {
		return joueur;
	}

	public boolean isJouable() {
		if (multijoueur) {
			try {
				return tour == joueur && Reseau.isJouable();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			if (joueurBlanc.isIA() && tour != NOIR)
				return false;
			if (joueurNoir.isIA() && tour != BLANC)
				return false;
			return true;
		}
	}

	public boolean isMultijoueur() {
		return multijoueur;
	}

	public void message(String texte, Color couleur, boolean persistant, boolean urgent) {
		if (gui != null)
			gui.message(texte, couleur, persistant, urgent);
	}

	public void multijoueur() {
		updateJoueurs();
		try {
			Reseau.creerServeur(this);
			nouvellePartie();
			multijoueur = true;
			tour = false;
			joueur = NOIR;
			gui.multijoueur();
			message("Une partie multijoueur a été lancée !", Message.OK, false, true);
			message("Attente d'un autre joueur...", Message.NEUTRE, true, false);
		} catch (Exception e) {
			message("Impossible de créer une partie en ligne. Voir la console pour plus d'informations", Message.ERREUR, false, false);
			e.printStackTrace();
		}
	}

	public void nouvellePartie() {
		if (multijoueur)
			deconnexion();

		matrice.reinitialiser();
		matrice.set(3, 3, Piece.BLANC);
		matrice.set(4, 3, Piece.NOIR);
		matrice.set(3, 4, Piece.NOIR);
		matrice.set(4, 4, Piece.BLANC);

		tour = false;
		joueurNoir.reinitialiser();
		joueurBlanc.reinitialiser();

		gui.nouvellePartie();

		matrice.chercherMouvements(Piece.NOIR);

		if (multijoueur)
			return;
		updateJoueurs();
	}
	
	private void joueurIA(final boolean joueur)
	{
		final Joueur joueurIA = joueur ? joueurBlanc : joueurNoir;
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

	public void poserPiece(int piece) {
		int type = tour() ? Piece.BLANC : Piece.NOIR;
		matrice.poser(piece, type);

		if (multijoueur) {
			try {
				// Si c'est notre tour, avertir le serveur du coup.
				if (isJouable())
					Reseau.poserPiece(piece);
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
			Reseau.rejoindre(this);
			nouvellePartie();
			tour = false;
			multijoueur = true;
			joueur = BLANC;
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
		joueurNoir.setType(Config.get(Config.DIFFICULTE_NOIR, Joueur.TYPE_HUMAIN));
		joueurBlanc.setType(Config.get(Config.DIFFICULTE_BLANC, Joueur.TYPE_IA1));
		gui.repaint();
		joueurIA(tour);
	}
}
