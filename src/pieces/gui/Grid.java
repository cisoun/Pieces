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
import pieces.utils.Player;

public class Grid extends JPanel {
	private static final long serialVersionUID = 1L;

	private Game game;
	private Piece[] pieces;

	private int marginX;
	private int marginY;
	private int caseSize;
	private int gridSize;

	private final int DELTA = 50; // MS between each piece reversing.
	private final int RANGE = 8;
	private final int PIECES = RANGE * RANGE;
	private final int PIECE_MARGIN = 5;

	private static final int DOWN = 1;
	private static final int RIGHT = 1;
	private static final int LEFT = -1;
	private static final int UP = -1;

	public Grid(Game game, Bank bank) {
		this.game = game;

		setLayout(null);
		setOpaque(false);
		
		// Initialize all the pieces.
		pieces = new Piece[PIECES];
		for (int i = 0; i < PIECES; i++) {
			// Coords definition.
			int x = i % RANGE;
			int y = (i - x) / RANGE;
			// ...
			pieces[i] = new Piece(bank, false, x, y);
			this.add(pieces[i]);
		}

		// Handlers
		mouseHandler();
		resizeHandler();
		
		initialize();
	}

	/**
	 * Grid initialization. Used when a new game has been created.
	 */
	public void initialize() {
		for (int i = 0; i < PIECES; i++) {
			// Coords definition.
			int x = i % RANGE;
			int y = (i - x) / RANGE;
			// ...
			Piece p = getPiece(x, y);
			p.setSide(false);
			p.setVisible(false);
		}

		// Inserts the 4 defaults pieces of Reversi.
		put(3, 3, Player.WHITE);
		put(4, 3, Player.BLACK);
		put(3, 4, Player.BLACK);
		put(4, 4, Player.WHITE);
	}

	private void drawAvailableMoves(Graphics2D g2d) {
		// Dessine si option activée ou tour = faux (noirs).
		if (!Config.get(Config.SHOW_MOVES, false) || !game.canPlay())
			return;

		Color couleurIndice = Themes.getCurrentTheme().getGridHint();

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		if (game.getMatrix().getAvailableMoves().size() > 0) {
			for (int i = 0; i < game.getMatrix().getAvailableMoves().size(); i++) {
				int c = game.getMatrix().getAvailableMoves().elementAt(i);
				// g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				// 0.5f));
				g2d.setColor(couleurIndice);
				g2d.fillRect(marginX + caseSize * game.getMatrix().getX(c), marginY + caseSize * game.getMatrix().getY(c), caseSize, caseSize);
			}
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	}

	private void drawGrid(Graphics2D g2d, Color couleurGrille) {
		g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[] { 2f }, 0f));
		g2d.setColor(couleurGrille.darker());
		for (int i = 0; i <= RANGE; i++) {
			// Vertical line
			g2d.drawLine(marginX + caseSize * i, marginY, marginX + caseSize * i, marginY + gridSize);
			// Horizontal line
			g2d.drawLine(marginX, marginY + caseSize * i, marginX + gridSize, marginY + caseSize * i);
			if (!Config.get(Config.SHOW_GRID, true)) {
				if (i == RANGE)
					break;
				i = RANGE - 1;
			}
		}
	}

	/**
	 * Return a specified piece.
	 * 
	 * @param x
	 * @param y
	 * @return Piece at [x, y].
	 */
	public Piece getPiece(int x, int y) {
		int piece = (y) * RANGE + x;
		if (piece > PIECES)
			return null;
		return pieces[piece];
	}

	public Piece getPiece(int piece) {
		return pieces[piece];
	}

	private void mouseHandler() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				// If it's not our round, exit.
				if (!game.canPlay())
					return;

				// Get piece below the cursor.
				if (event.getX() <= marginX || event.getX() >= marginX + gridSize)
					return;
				if (event.getY() <= marginY || event.getY() >= marginY + gridSize)
					return;
				int x = (int) ((float) (event.getX() - marginX) / (float) gridSize * RANGE);
				int y = (int) ((float) (event.getY() - marginY) / (float) gridSize * RANGE);

				System.out.println("[" + (x + 1) + "," + (y + 1) + "]");
				int piece = game.getMatrix().getIndex(x, y);

				// If case is already player, exit.
				if (game.getMatrix().get(piece) != MatrixPiece.EMPTY)
					return;

				// Check if the case is valid.
				// We need to put a virtual piece in order to have a functional
				// neighbor research.
				game.getMatrix().set(piece, game.round() ? MatrixPiece.WHITE : MatrixPiece.BLACK);
				if (!game.getMatrix().hasNeighbor(piece, true)) {
					game.getMatrix().set(piece, MatrixPiece.EMPTY);
					game.message("You can't play here !", Message.ERROR, false, true);
					return;
				}

				// Play the piece.
				put(piece, game.round()); // MUST BE PLACED BEFORE GAME.PLAY()
				game.play(piece);
			}
		});
	}

	public void put(int piece, boolean player) {
		Piece p = pieces[piece];
		p.setSide(player);
		p.setVisible(true);

		// Process neighboring pieces.
		processAllRanges(piece);
	}

	public void put(int x, int y, boolean player) {
		put(Coords.toIndex(x, y, RANGE), player);
	}

	private synchronized void processAllRanges(int piece) {
		for (int dH = UP; dH <= DOWN; dH++)
			for (int dV = LEFT; dV <= RIGHT; dV++)
				processRange(piece, dH, dV);
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
		int x = game.getMatrix().getX(piece);
		int y = game.getMatrix().getY(piece);
		int neighbor = game.getMatrix().searchNeighbor(piece, horizontalDirection, verticalDirection);
		if (neighbor == -1)
			return;

		// Count number of pieces to analyze.
		int pieces = game.getMatrix().distance(piece, neighbor);

		// Reverse pieces.
		for (int j = 1; j <= pieces; j++)
			reverse(x + (horizontalDirection * j), y + (verticalDirection * j), DELTA * j);
	}

	private void resizeHandler() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				for (int x = 0; x < RANGE; x++) {
					for (int y = 0; y < RANGE; y++) {
						getPiece(x, y).setBounds(
								marginX + x * caseSize + PIECE_MARGIN, 
								marginY + y * caseSize + PIECE_MARGIN, 
								caseSize - PIECE_MARGIN * 2, 
								caseSize - PIECE_MARGIN * 2
								);
					}
				}
			}
	
		});
	}

	private void reverse(int x, int y, int delta) {
		getPiece(x, y).reverse(delta);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color gridColor = Themes.getCurrentTheme().getGridLines();

		int width = getWidth();
		int height = getHeight() - 1; // - 1 avoid the last line to not be
										// drawn.
		gridSize = width >= height ? height : width;
		caseSize = gridSize / RANGE;
		marginX = width / 2 - gridSize / 2;
		marginY = height / 2 - gridSize / 2;

		drawAvailableMoves(g2d);
		drawGrid(g2d, gridColor);
	}
}
