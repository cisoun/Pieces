package pieces.utils;

import java.util.Stack;

public class IA {
	private static final int BAS = 1;
	private static final int DROITE = 1;
	private static final int GAUCHE = -1;
	private static final int HAUT = -1;

	private final static int RANGEE = 8;
	private final static int PIECES = RANGEE * RANGEE;
	private static int[] pieces;

	private static int[] region1 = new int[] { 0, 0, 0 };
	private static int[] region2 = new int[] { 0, -1, -2 };
	private static int[] region3 = new int[] { 0, 1, 3 };
	private static int[] region4 = new int[] { 0, -3, -5 };
	private static int[] region5 = new int[] { 0, 5, 10 };

	public static int meilleurCoup(Matrix matrice, Stack<Integer> coupsPossibles, int difficulte) {
		int[] valeurCoup = new int[coupsPossibles.size()];

		for (int i = 0; i < coupsPossibles.size(); i++) {
			int x = matrice.getX(coupsPossibles.get(i));
			int y = matrice.getY(coupsPossibles.get(i));

			if (x == 0 || x == 7) {
				if (y == 0 || y == 7) {
					valeurCoup[i] = region5[difficulte];
				} else if (y == 1 || y == 6) {
					valeurCoup[i] = region4[difficulte];
				} else {
					valeurCoup[i] = region3[difficulte];
				}
			} else if (x == 1 || x == 6) {
				if (y == 0 || y == 7) {
					valeurCoup[i] = region4[difficulte];
				} else if (y == 1 || y == 6) {
					valeurCoup[i] = region4[difficulte];
				} else {
					valeurCoup[i] = region2[difficulte];
				}
			} else {
				if (y == 0 || y == 7) {
					valeurCoup[i] = region3[difficulte];
				} else if (y == 1 || y == 6) {
					valeurCoup[i] = region2[difficulte];
				} else {
					valeurCoup[i] = region1[difficulte];
				}
			}
			int piecePossible = matrice.getIndex(x, y); // getPiece(x,y);
			int valeur = 0;
			for (int dH = HAUT; dH <= BAS; dH++)
				for (int dV = GAUCHE; dV <= DROITE; dV++)
					valeur += nombrePiece(matrice, piecePossible, dH, dV);

			valeurCoup[i] = valeurCoup[i] + valeur;

		}
		int max = region4[difficulte] - 1;
		int z = 0;
		for (int j = 0; j < valeurCoup.length; j++) {
			if (valeurCoup[j] > max) {
				max = valeurCoup[j];
				z = j;
			}
		}
		int fx = matrice.getX(coupsPossibles.get(z)); // .getX();
		int fy = matrice.getY(coupsPossibles.get(z)); // .getY();
		return matrice.getIndex(fx, fy);
	}

	private static int nombrePiece(Matrix matrice, int piece, int directionHorizontale, int directionVerticale) {
		// Si aucun voisin n'est trouvé dans la direction, abandonner.
		int x = matrice.getX(piece); // piece.getCoordonnees().getX();
		int y = matrice.getY(piece); // piece.getCoordonnees().getY();
		int voisin = matrice.chercherVoisine(piece, directionHorizontale, directionVerticale);// chercherVoisine(piece,
																								// directionHorizontale,
																								// directionVerticale);
		if (voisin == -1)
			return 0;

		// Calculs du nombre de pièces à analyser.
		int cx = matrice.getX(voisin); // voisin.getCoordonnees().getX();
		int cy = matrice.getY(voisin); // voisin.getCoordonnees().getY();
		int deltaX = Math.abs(x - cx);
		int deltaY = Math.abs(y - cy);

		int piecesARetourner = Matrix.nombrePieces(directionHorizontale, directionVerticale, deltaX, deltaY);

		return piecesARetourner;
	}
}
