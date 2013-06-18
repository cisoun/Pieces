package reversi.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import reversi.Reversi;
import reversi.gui.utils.BanquePieces;
import reversi.gui.utils.Couleur;
import reversi.gui.utils.Themes;
import reversi.utils.Config;
import reversi.utils.Coordonnees;
import reversi.utils.Matrice;
import reversi.utils.Matrice.Piece;

public class Grille extends JPanel {
	private Reversi jeu;
	private PieceGUI[] pieces;
	private BanquePieces banque;

	private int margeX;
	private int margeY;
	private int tailleCase;
	private int tailleGrille;

	

	private final int DELTA = 50; // Millisecondes entre chaque retournement.
	private final int RANGEE = 8;
	private final int PIECES = RANGEE * RANGEE;
	private final int MARGE_PIECE = 5;

	private static final int BAS = 1;
	private static final int DROITE = 1;
	private static final int GAUCHE = -1;
	private static final int HAUT = -1;

	public static final boolean NOIR = false;
	public static final boolean BLANC = true;

	public Grille(Reversi jeu, BanquePieces banque) {
		this.jeu = jeu;
		this.banque = banque;

		setLayout(null);

		// Instanciation des pièces.
		pieces = new PieceGUI[PIECES];
		for (int i = 0; i < PIECES; i++) {
			// Définition de leurs coordonnées.
			int x = i % RANGEE;
			int y = (i - x) / RANGEE;
			// ...
			pieces[i] = new PieceGUI(banque, false, x, y);
			this.add(pieces[i]);
		}

		initialiser();

		// Activation de la gestion de la souris.
		gestionSouris();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				int taille = getWidth() / RANGEE;
				for (int x = 0; x < RANGEE; x++) {
					for (int y = 0; y < RANGEE; y++) {
						getPiece(x, y).setBounds(margeX + x * tailleCase + MARGE_PIECE, margeY + y * tailleCase + MARGE_PIECE, tailleCase - MARGE_PIECE * 2, tailleCase - MARGE_PIECE * 2);
					}
				}
			}

		});

		setOpaque(false);
	}

	/**
	 * Initialise la grille. Utilisé aussi pour créer une nouvelle partie.
	 */
	public void initialiser() {
		for (int i = 0; i < PIECES; i++) {
			// Définition de leurs coordonnées.
			int x = i % RANGEE;
			int y = (i - x) / RANGEE;
			// ...
			PieceGUI p = getPiece(x, y);
			p.setRetourne(false);
			p.setVisible(false);
		}

		// Insertion des 4 pièces de base du Reversi.
		poserPiece(3, 3, BLANC);
		poserPiece(4, 3, NOIR);
		poserPiece(3, 4, NOIR);
		poserPiece(4, 4, BLANC);

		//chercherMouvements();
		repaint();
	}

	public void poserPiece(int x, int y, boolean joueur) {
		poserPiece(Coordonnees.toIndex(x, y, RANGEE), joueur);
	}
	
	public void poserPiece(int piece, boolean joueur)
	{
		PieceGUI p = pieces[piece];
		p.setRetourne(joueur);
		p.setVisible(true);
		
		// Vérification des pièces voisines et retournement des pièces.
		traiterToutesRangees(piece);
	}

	/**
	 * Analyse une certaine rangée depuis une pièce.
	 * 
	 * Si dans cette rangée on trouve la même pièce sans qu'il y ait de cases
	 * vides entre ces deux, on suppose que c'est la pièce voisine de celle
	 * d'origine.
	 * 
	 * @param x Position X de la pièce d'origine.
	 * @param y Position Y de la pièce d'origine.
	 * @param directionHorizontale Traiter à gauche ou à droite.
	 * @param directionVerticale Traiter en haut ou en bas.
	 * @return Pièce voisine.
	 */
	private PieceGUI chercherVoisine(PieceGUI piece, int directionHorizontale, int directionVerticale) {
		// Calcul du nombre de pièces dans la diagonale jusqu'au bord.
		int x = piece.getCoordonnees().getX();
		int y = piece.getCoordonnees().getY();
		int piecesHorizontale = directionHorizontale == GAUCHE ? x : RANGEE - x - 1;
		int piecesVerticales = directionVerticale == HAUT ? y : RANGEE - y - 1;
		int pieces = nombrePieces(directionHorizontale, directionVerticale, piecesHorizontale, piecesVerticales);

		boolean retourne = getPiece(x, y).isRetourne();

		// Vérifie dans la rangée où se trouve la pièce voisine.
		for (int i = 1; i <= pieces; i++) {
			PieceGUI voisine = getPiece(x + (directionHorizontale * i), y + (directionVerticale * i));
			// Case vide... Pas de voisine.
			if (!voisine.isVisible())
				return null;
			// Voisine trouvée !
			if (voisine.isRetourne() == retourne) {
				return voisine;
			}
		}
		return null;
	}

	private void dessinerCoupsPossibles(Graphics2D g2d) {
		// Dessine si option activée ou tour = faux (noirs).
		if (!Config.get(Config.AFFICHER_COUPS_POSSIBLES, false) || !jeu.isJouable())
			return;
	
		Color couleurIndice = Themes.getThemeCourant().getGridHint();
	
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		if (jeu.matrice.coupsPossibles.size() > 0) {
			for (int i = 0; i < jeu.matrice.coupsPossibles.size(); i++) {
				int c = jeu.matrice.coupsPossibles.elementAt(i);
				// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				// 0.5f));
				g2d.setColor(couleurIndice);
				g2d.fillRect(margeX + tailleCase * jeu.matrice.getX(c), margeY + tailleCase * jeu.matrice.getY(c), tailleCase, tailleCase);
			}
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	}

	private void dessinerGrille(Graphics2D g2d, Color couleurGrille) {
		g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 2f }, 0f));
		g2d.setColor(couleurGrille.darker());
		for (int i = 0; i <= RANGEE; i++) {
			// Ligne verticale
			g2d.drawLine(margeX + tailleCase * i, margeY, margeX + tailleCase * i, margeY + tailleGrille);
			// Ligne horizontale
			g2d.drawLine(margeX, margeY + tailleCase * i, margeX + tailleGrille, margeY + tailleCase * i);
			if (!Config.get(Config.AFFICHER_GRILLE, true)) {
				if (i == RANGEE)
					break;
				i = RANGEE - 1;
			}
		}
	}

	private int distance(PieceGUI a, PieceGUI b) {
		int dX = Math.abs(a.getCoordonnees().getX() - b.getCoordonnees().getX());
		int dY = Math.abs(a.getCoordonnees().getY() - b.getCoordonnees().getY());
	
		if (dX == dY || dY == 0)
			return dX - 1;
		else
			return dY - 1;
	}

	private void gestionSouris() {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (!jeu.isJouable())
					return;
				
				// Récupèration de la pièce se trouvant sous le curseur.
				if (event.getX() <= margeX || event.getX() >= margeX + tailleGrille)
					return;
				if (event.getY() <= margeY || event.getY() >= margeY + tailleGrille)
					return;
				int x = (int) ((float) (event.getX() - margeX) / (float) tailleGrille * RANGEE);
				int y = (int) ((float) (event.getY() - margeY) / (float) tailleGrille * RANGEE);
	
				System.out.println("[" + (x + 1) + "," + (y + 1) + "]");
				//Piece piece = getPiece(x, y);
				int piece = jeu.matrice.getIndex(x, y);

				// Si case déjà prise, annuler.
				if (jeu.matrice.get(piece) != Piece.VIDE)
					return;

				// Vérifie c'est une case valide.
				jeu.matrice.set(piece, jeu.tour() ? Piece.BLANC : Piece.NOIR);
				if (!jeu.matrice.possedeVoisine(piece, true)) {
					jeu.matrice.set(piece, Piece.VIDE);
					jeu.message("Vous ne pouvez pas poser de pièce ici !", Message.ERREUR, false, true);
					return;
				}
	
				// Poser les pièces.
				poserPiece(piece, jeu.tour());
				jeu.poserPiece(piece);
				repaint();
			}
		});
	}

	/**
	 * Retourne la pièce correspondant à une certaine position.
	 * 
	 * @param x Ligne où se trouve la pièce.
	 * @param y Colonne où se trouve la pièce.
	 * @return Retourne la pièce se trouvant sur [x, y].
	 */
	public PieceGUI getPiece(int x, int y) {
		int piece = (y) * RANGEE + x;
		if (piece > PIECES)
			return null;
		return pieces[piece];
	}
	
	public PieceGUI getPiece(int piece)
	{
		return pieces[piece];
	}

	/**
	 * Retourne le nombre de pièce se trouvant entre une pièce d'origine et une
	 * pièce voisine.
	 * 
	 * @param directionHorizontale Traiter à gauche ou à droite.
	 * @param directionVerticale Traiter en haut ou en bas.
	 * @param piecesHorizontale Nombre de pièces sur l'axe horizontal.
	 * @param piecesVerticales Nombre de pièces sur l'axe vertical.
	 * @return Nombre de pièces entre deux pièces.
	 */
	private int nombrePieces(int directionHorizontale, int directionVerticale, int piecesHorizontale, int piecesVerticales) {
		int pieces;
		if (directionHorizontale == 0) {
			pieces = piecesVerticales;
		} else if (directionVerticale == 0) {
			pieces = piecesHorizontale;
		} else {
			pieces = piecesHorizontale >= piecesVerticales ? piecesVerticales : piecesHorizontale;
		}
		return pieces;
	}

	/**
	 * Vérifie dans toutes les directions si un pièce possède une pièce voisine
	 * et si oui, des pièces adverses entre elles.
	 * 
	 * @param piece Pièce à traiter.
	 * @param avecAdversaire Doit avoir des pièces adverses entre elles ?
	 * @return Vrai si possède une voisine ou et des pièces adverses dans la
	 *         rangée.
	 */
	private boolean possedeVoisine(PieceGUI piece, boolean avecAdversaire) {
		int x = piece.getCoordonnees().getX();
		int y = piece.getCoordonnees().getY();
		boolean condition = false;
		for (int dh = HAUT; dh <= BAS; dh++) {
			for (int dv = GAUCHE; dv <= DROITE; dv++) {
				PieceGUI voisine = chercherVoisine(piece, dh, dv);
				if (voisine != null) {
					condition = condition || (!avecAdversaire || (avecAdversaire && distance(piece, voisine) >= 1));
				}
			}
		}
		return condition;
	}

	private void retournerPiece(int x, int y, int delta) {
		/*if (!reversi.tour()) {
			reversi.joueurNoir.gagne();
			reversi.joueurBlanc.perds();
		} else {
			reversi.joueurNoir.perds();
			reversi.joueurBlanc.gagne();
		}
		reversi.afficherScores();*/
		getPiece(x, y).retourner(delta);
	}

	/**
	 * Vérifie si une pièce possède une pièce voisine dans une certaine rangée
	 * 
	 * Si c'est le cas, retourne toutes les pièces contraires se trouvant entre
	 * les deux.
	 * 
	 * @param x Position X de la pièce d'origine.
	 * @param y Position Y de la pièce traitée.
	 * @param directionHorizontale Traiter à gauche ou à droite.
	 * @param directionVerticale Traiter en haut ou en bas.
	 */
	private void traiterRangee(int piece, int directionHorizontale, int directionVerticale) {
		// Si aucun voisin n'est trouvé dans la direction, abandonner.
		PieceGUI p = pieces[piece];
		int x = p.getCoordonnees().getX();
		int y = p.getCoordonnees().getY();
		PieceGUI voisin = chercherVoisine(p, directionHorizontale, directionVerticale);
		if (voisin == null)
			return;
	
		// Calculs du nombre de pièces à analyser.
		int cx = voisin.getCoordonnees().getX();
		int cy = voisin.getCoordonnees().getY();
		int deltaX = Math.abs(x - cx);
		int deltaY = Math.abs(y - cy);
	
		int pieces = nombrePieces(directionHorizontale, directionVerticale, deltaX, deltaY);
	
		// Retournements des pièces.
		for (int j = 1; j < pieces; j++)
			retournerPiece(x + (directionHorizontale * j), y + (directionVerticale * j), DELTA * j);
	}
	/*private void traiterRangee(int piece, int directionHorizontale, int directionVerticale) {
		// Si aucun voisin n'est trouvé dans la direction, abandonner.
		int x = jeu.matrice.getX(piece);
		int y = jeu.matrice.getY(piece);
		int voisine = jeu.matrice.chercherVoisine(piece, directionHorizontale, directionVerticale);
		
		if (voisine == -1)
			return;
		System.out.println("VOISINE : " + voisine);

		
		// Calculs du nombre de pièces à analyser.
		int cx = jeu.matrice.getX(voisine);
		int cy = jeu.matrice.getY(voisine);
		int deltaX = Math.abs(x - cx);
		int deltaY = Math.abs(y - cy);
	
		int pieces = jeu.matrice.nombrePieces(directionHorizontale, directionVerticale, deltaX, deltaY);
		System.out.println("PIECES : " + pieces);
		// Retournements des pièces.
		for (int j = 1; j < pieces; j++)
			getPiece(x + (directionHorizontale * j), y + (directionVerticale * j)).retourner(DELTA * j);
	}*/

	private synchronized void traiterToutesRangees(int piece) {
		for (int dH = HAUT; dH <= BAS; dH++)
			for (int dV = GAUCHE; dV <= DROITE; dV++)
				traiterRangee(piece, dH, dV);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color couleurGrille = Themes.getThemeCourant().getGridLines();

		int width = getWidth();
		int height = getHeight() - 1; 	// - 1 évite une coupure de la dernière
										// ligne.
		tailleGrille = width >= height ? height : width;
		tailleCase = tailleGrille / RANGEE;
		int espaceY = tailleGrille / RANGEE;
		margeX = width / 2 - tailleGrille / 2;
		margeY = height / 2 - tailleGrille / 2;

		// Dessine les coups possibles.
		dessinerCoupsPossibles(g2d);

		// Grille
		dessinerGrille(g2d, couleurGrille);
	}
}
