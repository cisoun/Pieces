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
		public static final int EMPTY = 0;
		public static final int BLACK = 1;
		public static final int WHITE = 2;
		
		public static String toChar(int piece) {
			return piece == EMPTY ? "_" : (piece == BLACK ? "#" : "O");
		}
	}

	private static final int DOWN = 1;
	private static final int RIGHT = 1;
	private static final int LEFT = -1;
	private static final int UP = -1;

	private int[] matrix;
	private int range;
	private int maxPieces;
	private int pieces;
	private Stack<Integer> availableMoves;
	
	public Matrix(int dimension) {
		range = dimension;
		maxPieces = range * range;
		matrix = new int[maxPieces];
		availableMoves = new Stack<Integer>();
	}
	
	public Matrix(Matrix m)
	{
		this(m.range);
		for (int i = 0; i < maxPieces; i++)
			set(i, m.get(i));
	}

	public void clearMoves()
	{
		availableMoves.clear();
	}
	
	/**
	 * Count the number of pieces between two other pieces.
	 * @param a
	 * @param b
	 * @return
	 */
	public int distance(int a, int b) {
		int dX = Math.abs(getX(a) - getX(b));
		int dY = Math.abs(getY(a) - getY(b));

		if (dX == dY || dY == 0)
			return dX - 1;
		else
			return dY - 1;
	}

	public int get(int piece) {
		return matrix[piece];
	}

	public int get(int x, int y) {
		return get(getIndex(x, y));
	}

	public Stack<Integer> getAvailableMoves()
	{
		return availableMoves;
	}
	
	public int getIndex(int x, int y) {
		return getIndex(x, y, range);
	}

	public static int getIndex(int x, int y, int rangee) {
		return y * rangee + x;
	}
	
	public int getRange()
	{
		return range;
	}

	public int getX(int piece) {
		return piece % range;
	}

	public int getY(int piece) {
		return piece / range;
	}
	
	/**
	 * Check in every directions if a piece has a neighbor and if so, opposite
	 * pieces between it and its neighbor.
	 * 
	 * @param piece.
	 * @param withOpponents Must have opposite pieces between them.
	 * @return True if has neighbor or and opposite pieces in the same range.
	 */
	public boolean hasNeighbor(int piece, boolean withOpponents) {
		boolean condition = false;
		for (int dh = UP; dh <= DOWN; dh++) {
			for (int dv = LEFT; dv <= RIGHT; dv++) {
				if (dh == 0 && dv == 0)
					continue;
				int neighbor = searchNeighbor(piece, dh, dv);
				if (neighbor != -1) {
					condition = condition || (!withOpponents || (withOpponents && distance(piece, neighbor) >= 1));
				}
			}
		}
		return condition;
	}

	public void initialize() {
		pieces = 0;
		for (int i = 0; i < maxPieces; i++)
			matrix[i] = MatrixPiece.EMPTY;
	}

	public boolean isFull()
	{
		return pieces == maxPieces;
	}

	/**
	 * Return the number of pieces between one and another.
	 * 
	 * @param horizontalDirection
	 * @param verticalDirection
	 * @param horizontalPieces Number of pieces on the X axis.
	 * @param verticalPieces Number of pieces on the Y axis.
	 * @return Number of pieces.
	 */
	public static int numberOfPieces(int horizontalDirection, int verticalDirection, int horizontalPieces, int verticalPieces) {
		int pieces;
		if (horizontalDirection == 0) {
			pieces = verticalPieces;
		} else if (verticalDirection == 0) {
			pieces = horizontalPieces;
		} else {
			pieces = horizontalPieces >= verticalPieces ? verticalPieces : horizontalPieces;
		}
		return pieces;
	}

	public int pieces()
	{
		return pieces; //maxPieces - score(MatrixPiece.EMPTY);
	}
	
	public void play(int piece, int type) {
		set(piece, type);
		processAllRanges(piece);
		System.out.println(toString());
	}

	public void play(int x, int y, int type) {
		play(getIndex(x, y), type);
	}

	/**
	 * Check if a piece has a neighbor in the same range and if so, reverse
	 * every pieces between them.
	 * 
	 * @param x Original piece's X pos.
	 * @param y Original piece's Y pos.
	 * @param horizontalDirection
	 * @param verticalDirection
	 */
	private void processRange(int piece, int horizontalDirection, int verticalDirection) {
		// If no neighbor, exit.
		int x = getX(piece);
		int y = getY(piece);
		int neighbor = searchNeighbor(piece, horizontalDirection, verticalDirection);
		if (neighbor == -1)
			return;
	
		// Count number of pieces to analyze.
		int pieces = distance(piece, neighbor);

		for (int j = 1; j <= pieces; j++)
			reverse(x + (horizontalDirection * j), y + (verticalDirection * j));
	}

	private void processAllRanges(int piece) {
		for (int dH = UP; dH <= DOWN; dH++)
			for (int dV = LEFT; dV <= RIGHT; dV++)
				processRange(piece, dH, dV);
	}

	public void reset() {
		initialize();
	}

	public void reverse(int piece) {
		if (get(piece) == MatrixPiece.EMPTY)
			return;

		int type = 2 - (get(piece) - 1); // Passage de 1 à 2 et de 2 à 1.
		set(piece, type);
	}

	public void reverse(int x, int y) {
		reverse(getIndex(x, y));
	}

	public int score(int type) {
		int x = 0;
		for (int i = 0; i < matrix.length; i++)
			if (get(i) == type)
				x++;
		return x;
	}

	public void searchMoves(int pieceType) {
		clearMoves();
	
		// Analyze each case.
		for (int i = 0; i < maxPieces; i++) {
			// Ignore used or already found cases.
			if (get(i) != MatrixPiece.EMPTY || availableMoves.contains(i))
				continue;
			// For all directions...
			for (int dH = UP; dH <= DOWN; dH++) {
				for (int dV = LEFT; dV <= RIGHT; dV++) {
					// Current piece's position.
					int x = getX(i);
					int y = getY(i);
	
					// Compute numbers of cases between current case and border.
					int horizontalPieces = dH == LEFT ? x + 1 : range - x;
					int vertialPieces = dV == UP ? y + 1 : range - y;
					int pieces = numberOfPieces(dH, dV, horizontalPieces, vertialPieces);
	
					// Check in the range where the neighboring piece is at.
					boolean ok = false;
					for (int j = 1; j < pieces; j++) {
						int mx = x + (dH * j); // Current X pos.
						int my = y + (dV * j); // Current Y pos.
	
						// Current piece.
						int neighbor = getIndex(mx, my);
						int neighborType = get(neighbor);
	
						// If first found case is empty or its piece is the same
						// , no moves possible.
						if (j == 1 && (neighborType == MatrixPiece.EMPTY || neighborType == pieceType))
							break;
	
						if (ok && neighborType == MatrixPiece.EMPTY)
							break;
	
						// If first piece was already seen and a same one was
						// found, save the last one.
						if (neighborType == pieceType) {
							if (ok) {
								System.out.println("Available at " + Coords.toString(x, y));
								if (!availableMoves.contains(i))
									availableMoves.push(i);
								ok = false;
							} else {
								continue;
							}
						}
	
						if (neighborType != MatrixPiece.EMPTY && neighborType != pieceType)
							ok = true;
	
					}
				}
			}
		}
	}

	/**
	 * Analyze a row from a piece.
	 * 
	 * If we found the same piece in this row without any empty case between the
	 * original and the last found, we suppose it's its neighbor.
	 * 
	 * @param x Original piece's X position.
	 * @param y Original piece's Y position.
	 * @param horizontalDirection
	 * @param verticalDirection
	 * @return Neighboring piece.
	 */
	public int searchNeighbor(int piece, int horizontalDirection, int verticalDirection) {
		// Compute number of pieces between the current piece to the border.
		int x = getX(piece);
		int y = getY(piece);
		int horizontalPieces = horizontalDirection == LEFT ? x : range - x - 1;
		int verticalPieces = verticalDirection == UP ? y : range - y - 1;
		int pieces = numberOfPieces(horizontalDirection, verticalDirection, horizontalPieces, verticalPieces);
	
		int type = get(piece);
	
		// Check in the row where is its neighbor.
		for (int i = 1; i <= pieces; i++) {
			int neighbor = getIndex(x + (horizontalDirection * i), y + (verticalDirection * i));
			// Empty case, no neighbor.
			if (get(neighbor) == MatrixPiece.EMPTY)
				return -1;
			// Neighbor found !
			if (get(neighbor) == type) {
				return neighbor;
			}
		}
		return -1;
	}

	public void set(int piece, int type) {
		if (get(piece) == MatrixPiece.EMPTY && type != MatrixPiece.EMPTY)
			pieces++;
		else if(get(piece) != MatrixPiece.EMPTY && type == MatrixPiece.EMPTY)
			pieces--;
		matrix[piece] = type;
	}

	public void set(int x, int y, int type) {
		set(getIndex(x, y), type);
	}

	@Override
	public String toString() {
		String m = "";
		for (int y = 0; y < range; y++) {
			for (int x = 0; x < range; x++)
				m += MatrixPiece.toChar(get(x, y));
			m += "\n";
		}
		return m;
	}
}
