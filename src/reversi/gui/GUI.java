package reversi.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import reversi.Reversi;
import reversi.gui.menu.Menu;
import reversi.gui.menu.BarreMenu;
import reversi.gui.utils.BanquePieces;
import reversi.gui.utils.Ease;
import reversi.gui.utils.Themes;
import reversi.gui.utils.BanquePieces.SequenceIntrouvableException;
import reversi.utils.Config;
import reversi.utils.Utils;
import reversi.utils.Matrice.Piece;

public class GUI extends JFrame {

	private Reversi reversi;
	public Themes theme;
	private BanquePieces banque = null;

	private BarreMenu menu;
	private Grille grille;
	private Message message;
	private Scores scores;
	private Options options;
	private Multijoueur multijoueur;
	private Plateau plateau;

	private Menu menuNouvellePartie;
	private Menu menuMultijoueur;
	private Menu menuOptions;
	private Menu menuApropos;

	public GUI(Reversi reversi) {

		this.reversi = reversi;

		Config.charger();
		Themes.chargerThemes();
		Themes.setThemeCourant(Config.get(Config.THEME, "Pieces"));

		// Chargement des thèmes.
		try {
			banque = new BanquePieces("themes/" + Themes.getThemeCourant().getName() + "/sequence.png", 10);
		} catch (SequenceIntrouvableException sie) {
			// sie.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		setBackground(Themes.getThemeCourant().getGridBackground());

		// Initialisation des composants.
		menu = new BarreMenu();
		message = new Message(this);
		grille = new Grille(reversi, banque);
		scores = new Scores(reversi, banque);
		options = new Options(reversi, this);
		multijoueur = new Multijoueur(reversi);
		plateau = new Plateau(grille, message, options, multijoueur);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(multijoueur, BorderLayout.NORTH);
		panel.add(plateau, BorderLayout.CENTER);

		creerMenus();

		// Affichage.
		setLayout(new BorderLayout());
		add(menu, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(scores, BorderLayout.SOUTH);
		add(options, BorderLayout.EAST);

		// NÉCESSAIRE ?
		revalidate();
		repaint();
		
		setTitle("Pieces");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
	}

	private void creerMenus() {
		menuNouvellePartie = new Menu("Nouvelle partie") {
			@Override
			public void action() {
				super.action();
				reversi.nouvellePartie();
			}
		};

		menuMultijoueur = new Menu("Multijoueur") {
			@Override
			public void action() {
				super.action();
				if (reversi.isMultijoueur())
					reversi.deconnexion();
				else
					multijoueur.afficher();
				multijoueur();
			}
		};

		menuOptions = new Menu("Options") {
			@Override
			public void action() {
				super.action();
				options.afficher();
			}
		};

		menuApropos = new Menu("?") {
			@Override
			public void action() {
				// TODO Auto-generated method stub
				super.action();
				afficherApropos();
			}
		};

		menu.addMenu(menuNouvellePartie);
		menu.addMenu(menuMultijoueur);
		menu.addMenu(menuOptions);
		menu.addMenu(menuApropos);
	}

	public void afficherApropos() {
		StringBuilder message = new StringBuilder();
		message.append("PIECES");
		message.append("\n\n");
		message.append("Auteurs : ");
		message.append("\n");
		message.append("Guillaume Simon-Gentil");
		message.append("\n");
		message.append("Cyriaque Skrapits");
		Icon icone = new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawImage(banque.getImage(0), 0, 0, 60, 60, null);
			}

			@Override
			public int getIconWidth() {
				// TODO Auto-generated method stub
				return 60;
			}

			@Override
			public int getIconHeight() {
				// TODO Auto-generated method stub
				return 60;
			}
		};
		JOptionPane.showMessageDialog(this, message.toString(), "À propos de Pieces...", JOptionPane.INFORMATION_MESSAGE, icone);
	}

	private void animationFin() {
		if (!Config.get(Config.RETOURNEMENT_FIN, true))
			return;

		Thread animation = new Thread(new Runnable() {
			int noirs = reversi.matrice.score(Piece.NOIR);

			@Override
			public void run() {
				for (int i = 0; i < 64; i++) {
					PieceGUI p = grille.getPiece(i);
					if (!p.isVisible())
						p.setVisible(true);
					if (p.isRetourne() == noirs > 0)
						p.retourner();
					noirs--;
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					repaint();
					revalidate();
				}
			}
		});
		animation.start();
	}

	public void changerTour() {
		int scoreNoir = reversi.matrice.score(Piece.NOIR);
		int scoreBlanc = reversi.matrice.score(Piece.BLANC);
		
		// Si grille remplie...
		if (scoreNoir + scoreBlanc == 64) {
			if (scoreNoir == scoreBlanc)
				message.message("Match nul !", Message.ATTENTION, true, false);
			else
				message.message("Les " + (scoreNoir > scoreBlanc ? "noirs" : "blancs") + " ont gagné !", Message.OK, true, false);
			animationFin();
			return;
		}
		// ...sinon match nul ou joueur bloqué.
		else
		{
			// Au cas où il ne resterait plus qu'un joueur...
			if (scoreNoir == 0 || scoreBlanc == 0)
			{
				reversi.message("Les " + (scoreNoir == 0 ? "blancs" : "noirs") + " ont gagnée !", Message.OK, true, true);
				return;
			}
			int scores = scoreNoir + scoreBlanc;
			// Évite l'évaluation à l'initialisation de la partie.
			if (scores > 4 && reversi.matrice.coupsPossibles.size() == 0) {
				reversi.changerTour();
				reversi.chercherMouvements();
				if (reversi.matrice.coupsPossibles.size() == 0)
				{
					if (scoreNoir == scoreBlanc)
						reversi.message("Match nul.", Message.ATTENTION, true, true);
					else
						message.message("Les " + (scoreNoir > scoreBlanc ? "noirs" : "blancs") + " ont gagné !", Message.OK, true, false);
				}
				return;
			}
		}

		// Affichage du joueur autorisé à jouer.
		String m;
		m = "C'est aux " + (reversi.tour() ? "blancs" : "noirs") + " de jouer.";
		System.out.println(m);
		message.message(m, Message.NEUTRE, false, true);
	}
	
	public BanquePieces getBanque() {
		return this.banque;
	}

	public Grille getGrille() {
		return this.grille;
	}

	public Scores getScores() {
		return this.scores;
	}

	public void message(String texte, Color couleur, boolean persistant, boolean urgent) {
		message.message(texte, couleur, persistant, urgent);
		revalidate();
		repaint();
	}

	public void nouvellePartie() {
		grille.initialiser();
		message.cacher();
		scores.afficher();
	}
	
	public void poserPiece(int piece) {
		grille.poserPiece(piece, reversi.tour());
	}

	public void redessiner() {
		multijoueur.redessiner();
		setBackground(Themes.getThemeCourant().getGridBackground());
		repaint();
	}

	public void multijoueur() {
		menuMultijoueur.setTexte(reversi.isMultijoueur() ? "Déconnexion" : "Multijoueur");
	}
}
