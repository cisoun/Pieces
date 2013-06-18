package pieces.utils;

import java.util.Stack;

/**
 * Matrice numérique contenant les pièces du jeu.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class Matrix {

	public static class MatrixPiece {
		public static final int VIDE = 0;
		public static final int NOIR = 1;
		public static final int BLANC = 2;

		public static String toChar(int piece) {
			return piece == VIDE ? "_" : (piece == NOIR ? "#" : "O");
		}
	}

	private static final int BAS = 1;
	private static final int DROITE = 1;
	private static final int GAUCHE = -1;
	private static final int HAUT = -1;

	private int[] matrice;
	private int rangee;
	private int pieces;
	private int piecesNoires;
	private int piecesBlanches;
	public Stack<Integer> coupsPossibles;

	public Matrix(int dimension) {
		rangee = dimension;
		pieces = rangee * rangee;
		matrice = new int[pieces];
		coupsPossibles = new Stack<Integer>();
		piecesNoires = 0;
		piecesBlanches = 0;
	}

	public void chercherMouvements(int typePiece) {
		coupsPossibles.clear();
	
		// Recherche de coups possibles pour chaque case.
		for (int i = 0; i < pieces; i++) {
			// Ignore les pièces déjà posées ou cases trouvées.
			if (get(i) != MatrixPiece.VIDE || coupsPossibles.contains(i))
				continue;
			// Pour toutes les directions...
			for (int dH = HAUT; dH <= BAS; dH++) {
				for (int dV = GAUCHE; dV <= DROITE; dV++) {
					// Position de la pièce analysée.
					int x = getX(i);
					int y = getY(i);
	
					// Calcul du nombre de pièces dans la trajectoire jusqu'au
					// bord.
					int piecesHorizontale = dH == GAUCHE ? x + 1 : rangee - x;
					int piecesVerticales = dV == HAUT ? y + 1 : rangee - y;
					int pieces = nombrePieces(dH, dV, piecesHorizontale, piecesVerticales);
	
					// Vérifie dans la rangée où se trouve la pièce voisine.
					boolean ok = false;
					for (int j = 1; j < pieces; j++) {
						int mx = x + (dH * j); // Position X courante.
						int my = y + (dV * j); // Position Y courante.
	
						// Pièce courante.
						int voisine = getIndex(mx, my);
						int typeVoisine = get(voisine);
	
						// Si la première case rencontrée de la rangée est vide
						// ou est la même alors pas de mouvement possible.
						if (j == 1 && (typeVoisine == MatrixPiece.VIDE || typeVoisine == typePiece))
							break;
						
						if (ok && typeVoisine == MatrixPiece.VIDE)
							break;
	
						// Si la première pièce a été passée et qu'une similaire
						// a été trouvé, la stocker.
						if (typeVoisine == typePiece) {
							if (ok)
							{
								System.out.println("Valide en " + Coords.toString(x, y));
								if (!coupsPossibles.contains(i))
								coupsPossibles.push(i);
								ok = false;
							} else {
								continue;
							}
						}
	
						
						if (typeVoisine != MatrixPiece.VIDE && typeVoisine != typePiece)
							ok = true;
	
					}
				}
			}
		}
		System.out.println("---");
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
	public int chercherVoisine(int piece, int directionHorizontale, int directionVerticale) {
		// Calcul du nombre de pièces dans la diagonale jusqu'au bord.
		int x = getX(piece);
		int y = getY(piece);
		int piecesHorizontale = directionHorizontale == GAUCHE ? x : rangee - x - 1;
		int piecesVerticales = directionVerticale == HAUT ? y : rangee - y - 1;
		int pieces = nombrePieces(directionHorizontale, directionVerticale, piecesHorizontale, piecesVerticales);
	
		int cote = get(piece);
	
		// Vérifie dans la rangée où se trouve la pièce voisine.
		for (int i = 1; i <= pieces; i++) {
			int voisine = getIndex(x + (directionHorizontale * i), y + (directionVerticale * i));
			// Case vide... Pas de voisine.
			if (get(voisine) == MatrixPiece.VIDE)
				return -1;
			// Voisine trouvée !
			if (get(voisine) == cote) {
				return voisine;
			}
		}
		return -1;
	}

	private int distance(int a, int b) {
		int dX = Math.abs(getX(a) - getX(b));
		int dY = Math.abs(getY(a) - getY(b));
	
		if (dX == dY || dY == 0)
			return dX - 1;
		else
			return dY - 1;
	}

	public void initialiser() {
		for (int i = 0; i < pieces; i++)
			matrice[i] = MatrixPiece.VIDE;
	}

	public int get(int piece) {
		return matrice[piece];
	}

	public int get(int x, int y) {
		return get(getIndex(x, y));
	}

	public int getIndex(int x, int y) {
		return getIndex(x, y, rangee);
	}

	public static int getIndex(int x, int y, int rangee) {
		return y * rangee + x;
	}

	public int getX(int piece) {
		return piece % rangee;
	}

	public int getY(int piece) {
		return piece / rangee;
	}

	/**
	 * Retourne le nombre de pièce se trouvant entre une pièce d'origine et une
	 * pièce voisine.
	 * 
	 * @param directionHorizontale Traiter à gauche ou à droite.
	 * @param directionVerticale Traiter en haut ou en bas.
	 * @param piecesHorizontales Nombre de pièces sur l'axe horizontal.
	 * @param piecesVerticales Nombre de pièces sur l'axe vertical.
	 * @return Nombre de pièces entre deux pièces.
	 */
	public static int nombrePieces(int directionHorizontale, int directionVerticale, int piecesHorizontales, int piecesVerticales) {
		int pieces;
		if (directionHorizontale == 0) {
			pieces = piecesVerticales;
		} else if (directionVerticale == 0) {
			pieces = piecesHorizontales;
		} else {
			pieces = piecesHorizontales >= piecesVerticales ? piecesVerticales : piecesHorizontales;
		}
		return pieces;
	}

	public void set(int piece, int type) {
		matrice[piece] = type;
	}

	public void set(int x, int y, int type) {
		set(getIndex(x, y), type);
	}

	public void poser(int piece, int type) {
		set(piece, type);
		traiterToutesRangees(piece);
		System.out.println(toString());
	}

	public void poser(int x, int y, int type) {
		poser(getIndex(x, y), type);
	}

	public void reinitialiser() {
		initialiser();
	}

	public void retourner(int piece) {
		if (get(piece) == MatrixPiece.VIDE)
			return;

		int type = 2 - (get(piece) - 1); // Passage de 1 à 2 et de 2 à 1.
		set(piece, type);
	}

	public void retourner(int x, int y) {
		retourner(getIndex(x, y));
	}
	
	public int score(int type)
	{
		int x = 0;
		for (int i = 0; i < matrice.length; i++)
			if (get(i) == type)
				x++;
		return x;
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
		// Si aucune voisine n'est trouvée dans la direction, abandonner.
		int x = getX(piece);
		int y = getY(piece);
		int voisine = chercherVoisine(piece, directionHorizontale, directionVerticale);
		if (voisine == -1)
			return;
	
		// Calculs du nombre de pièces à analyser.
		int cx = getX(voisine);
		int cy = getY(voisine);
		int deltaX = Math.abs(x - cx);
		int deltaY = Math.abs(y - cy);
	
		int pieces = nombrePieces(directionHorizontale, directionVerticale, deltaX, deltaY);
	
		for (int j = 1; j < pieces; j++)
			retourner(x + (directionHorizontale * j), y + (directionVerticale * j));
	}

	private void traiterToutesRangees(int piece) {
		for (int dH = HAUT; dH <= BAS; dH++)
			for (int dV = GAUCHE; dV <= DROITE; dV++)
				traiterRangee(piece, dH, dV);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String m = "";
		for (int y = 0; y < rangee; y++) {
			for (int x = 0; x < rangee; x++)
				m += MatrixPiece.toChar(get(x, y));
			m += "\n";
		}
		return m;
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
	public boolean possedeVoisine(int piece, boolean avecAdversaire) {
		boolean condition = false;
		for (int dh = HAUT; dh <= BAS; dh++) {
			for (int dv = GAUCHE; dv <= DROITE; dv++) {
				if (dh == 0 && dv == 0)
					continue;
				int voisine = chercherVoisine(piece, dh, dv);
				if (voisine != -1) {
					condition = condition || (!avecAdversaire || (avecAdversaire && distance(piece, voisine) >= 1));
				}
			}
		}
		return condition;
	}
}
