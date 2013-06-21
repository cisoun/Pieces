package pieces.utils;

/**
 * AI class.
 * 
 * ============================================================================
 * 
 * THIS AI ISN'T VERY GOOD YET. CONSIDER AN ALPHA-BETA PRUNING IMPLEMENTATION
 * FOR A BETTER AND OPTIMIZED AI.
 * 
 * ============================================================================
 * 
 * @author Simon Guillaume-Gentil
 * @author Cyriaque Skrapits (corrections)
 * 
 */
public class AI {
	private static final int DOWN = 1;
	private static final int RIGHT = 1;
	private static final int LEFT = -1;
	private static final int UP = -1;

	// private final static int RANGE = 8;
	// private final static int PIECES = RANGE * RANGE;
	// private static int[] pieces;

	private static int[] region1 = new int[] { 0, 0, 0 };
	private static int[] region2 = new int[] { 0, -1, -2 };
	private static int[] region3 = new int[] { 0, 1, 3 };
	private static int[] region4 = new int[] { 0, -3, -5 };
	private static int[] region5 = new int[] { 0, 5, 10 };

	public static int play(Matrix matrix, int difficulty) {
		int availableMoves = matrix.getAvailableMoves().size();
		int[] moveValue = new int[availableMoves];

		for (int i = 0; i < availableMoves; i++) {
			int x = matrix.getX(matrix.getAvailableMoves().get(i));
			int y = matrix.getY(matrix.getAvailableMoves().get(i));

			if (x == 0 || x == 7) {
				if (y == 0 || y == 7) {
					moveValue[i] = region5[difficulty];
				} else if (y == 1 || y == 6) {
					moveValue[i] = region4[difficulty];
				} else {
					moveValue[i] = region3[difficulty];
				}
			} else if (x == 1 || x == 6) {
				if (y == 0 || y == 7) {
					moveValue[i] = region4[difficulty];
				} else if (y == 1 || y == 6) {
					moveValue[i] = region4[difficulty];
				} else {
					moveValue[i] = region2[difficulty];
				}
			} else {
				if (y == 0 || y == 7) {
					moveValue[i] = region3[difficulty];
				} else if (y == 1 || y == 6) {
					moveValue[i] = region2[difficulty];
				} else {
					moveValue[i] = region1[difficulty];
				}
			}
			int piece = matrix.getIndex(x, y);
			int value = 0;
			for (int dH = UP; dH <= DOWN; dH++)
				for (int dV = LEFT; dV <= RIGHT; dV++) {
					int neighbor = matrix.searchNeighbor(piece, dH, dV);
					if (neighbor == -1)
						continue;
					value += matrix.distance(piece, neighbor) + 1;
				}
			moveValue[i] = moveValue[i] + value;
		}

		int max = region4[difficulty] - 1;
		int z = 0;
		for (int j = 0; j < moveValue.length; j++) {
			if (moveValue[j] > max) {
				max = moveValue[j];
				z = j;
			}
		}
		int fx = matrix.getX(matrix.getAvailableMoves().get(z)); // .getX();
		int fy = matrix.getY(matrix.getAvailableMoves().get(z)); // .getY();
		return matrix.getIndex(fx, fy);
	}
}
