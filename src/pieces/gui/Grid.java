package pieces.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

import pieces.Game;
import pieces.gui.utils.Bank;
import pieces.gui.utils.Themes;
import pieces.utils.Config;
import pieces.utils.Coords;
import pieces.utils.Matrix.MatrixPiece;


public class Grid extends JPanel {
	private static final long serialVersionUID = 1L;

	private Game game;
	private Piece[] pieces;
	private Bank banque;

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

	public Grid(Game jeu, Bank banque) {
		this.game = jeu;
		this.banque = banque;

		setLayout(null);

		// Instanciation des pièces.
		pieces = new Piece[PIECES];
		for (int i = 0; i < PIECES; i++) {
			// Définition de leurs coordonnées.
			int x = i % RANGEE;
			int y = (i - x) / RANGEE;
			// ...
			pieces[i] = new Piece(banque, false, x, y);
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
			Piece p = getPiece(x, y);
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
		poserPiece(Coords.toIndex(x, y, RANGEE), joueur);
	}
	
	public void poserPiece(int piece, boolean joueur)
	{
		Piece p = pieces[piece];
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
	private Piece chercherVoisine(Piece piece, int directionHorizontale, int directionVerticale) {
		// Calcul du nombre de pièces dans la diagonale jusqu'au bord.
		int x = piece.getCoordonnees().getX();
		int y = piece.getCoordonnees().getY();
		int piecesHorizontale = directionHorizontale == GAUCHE ? x : RANGEE - x - 1;
		int piecesVerticales = directionVerticale == HAUT ? y : RANGEE - y - 1;
		int pieces = nombrePieces(directionHorizontale, directionVerticale, piecesHorizontale, piecesVerticales);

		boolean retourne = getPiece(x, y).isReversed();

		// Vérifie dans la rangée où se trouve la pièce voisine.
		for (int i = 1; i <= pieces; i++) {
			Piece voisine = getPiece(x + (directionHorizontale * i), y + (directionVerticale * i));
			// Case vide... Pas de voisine.
			if (!voisine.isVisible())
				return null;
			// Voisine trouvée !
			if (voisine.isReversed() == retourne) {
				return voisine;
			}
		}
		return null;
	}

	private void dessinerCoupsPossibles(Graphics2D g2d) {
		// Dessine si option activée ou tour = faux (noirs).
		if (!Config.get(Config.SHOW_MOVES, false) || !game.isJouable())
			return;
	
		Color couleurIndice = Themes.getThemeCourant().getGridHint();
	
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		if (game.getMatrix().movesAvailable.size() > 0) {
			for (int i = 0; i < game.getMatrix().movesAvailable.size(); i++) {
				int c = game.getMatrix().movesAvailable.elementAt(i);
				// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				// 0.5f));
				g2d.setColor(couleurIndice);
				g2d.fillRect(margeX + tailleCase * game.getMatrix().getX(c), margeY + tailleCase * game.getMatrix().getY(c), tailleCase, tailleCase);
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
			if (!Config.get(Config.SHOW_GRID, true)) {
				if (i == RANGEE)
					break;
				i = RANGEE - 1;
			}
		}
	}

	private int distance(Piece a, Piece b) {
		int dX = Math.abs(a.getCoordonnees().getX() - b.getCoordonnees().getX());
		int dY = Math.abs(a.getCoordonnees().getY() - b.getCoordonnees().getY());
	
		if (dX == dY || dY == 0)
			return dX - 1;
		else
			return dY - 1;
	}

	private void gestionSouris() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (!game.isJouable())
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
				int piece = game.getMatrix().getIndex(x, y);

				// Si case déjà prise, annuler.
				if (game.getMatrix().get(piece) != MatrixPiece.EMPTY)
					return;

				// Vérifie c'est une case valide.
				game.getMatrix().set(piece, game.tour() ? MatrixPiece.WHITE : MatrixPiece.BLACK);
				if (!game.getMatrix().possedeVoisine(piece, true)) {
					game.getMatrix().set(piece, MatrixPiece.EMPTY);
					game.message("Vous ne pouvez pas poser de pièce ici !", Message.ERROR, false, true);
					return;
				}
	
				// Poser les pièces.
				poserPiece(piece, game.tour());
				game.play(piece);
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
	public Piece getPiece(int x, int y) {
		int piece = (y) * RANGEE + x;
		if (piece > PIECES)
			return null;
		return pieces[piece];
	}
	
	public Piece getPiece(int piece)
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
	private boolean possedeVoisine(Piece piece, boolean avecAdversaire) {
		int x = piece.getCoordonnees().getX();
		int y = piece.getCoordonnees().getY();
		boolean condition = false;
		for (int dh = HAUT; dh <= BAS; dh++) {
			for (int dv = GAUCHE; dv <= DROITE; dv++) {
				Piece voisine = chercherVoisine(piece, dh, dv);
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
		Piece p = pieces[piece];
		int x = p.getCoordonnees().getX();
		int y = p.getCoordonnees().getY();
		Piece voisin = chercherVoisine(p, directionHorizontale, directionVerticale);
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
