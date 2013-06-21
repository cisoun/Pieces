package pieces.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import pieces.Game;
import pieces.gui.utils.Bank;
import pieces.gui.utils.ColorTools;
import pieces.gui.utils.Themes;
import pieces.utils.Matrix.MatrixPiece;

public class Scores extends JPanel {
	private static final long serialVersionUID = 1L;

	private Game game;
	private Bank bank;
	private static int MARGIN = 20;
	private FontMetrics fontMetrics;
	private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 40);

	public Scores(Game game, Bank bank) {
		this.game = game;
		this.bank = bank;
		setLayout(new FlowLayout());

		setPreferredSize(new Dimension(getWidth(), 80));
		setBackground(Themes.getCurrentTheme().getGridBackground());

		update();

		fontMetrics = this.getFontMetrics(font);
	}

	/**
	 * Maybe useless but easier to understand.
	 */
	public void update() {
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color background = Themes.getCurrentTheme().getBackground();

		int pieceSize = bank.getPieceSize();
		int size = getHeight() - MARGIN * 2;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// background
		GradientPaint gp = new GradientPaint(0, 0, background, 0, getHeight(), ColorTools.darken(background, 50));

		g2d.setPaint(gp);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setColor(background.darker());
		g2d.drawLine(0, 0, getWidth(), 0);

		g2d.setColor(background.brighter());
		g2d.drawLine(0, 1, getWidth(), 1);

		// Text
		String text = game.getMatrix().score(MatrixPiece.BLACK) + " - " + game.getMatrix().score(MatrixPiece.WHITE);
		int textWidth = fontMetrics.stringWidth(text);
		int textHeight = fontMetrics.getHeight();
		int textX = getWidth() / 2 - textWidth / 2;
		int textY = getHeight() / 2 - textHeight / 2 + 40;
		GradientPaint gpText = new GradientPaint(0, 0, ColorTools.darken(background, 20), 0, textHeight, ColorTools.darken(background, 70));

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setFont(font);

		g2d.setColor(ColorTools.brighten(background, 30));
		g2d.drawString(text, textX, textY + 1);

		g2d.setColor(ColorTools.darken(background, 70));
		g2d.drawString(text, textX, textY - 1);

		g2d.setColor(background.brighter());
		g2d.setPaint(gpText);
		g2d.drawString(text, textX, textY);

		// Pieces
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Antialiasing.

		if (game.round())
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

		g2d.drawImage(bank.getImage(0), textX - size - MARGIN, MARGIN, textX - MARGIN, size + MARGIN, 0, 0, pieceSize, pieceSize, this);

		if (!game.round())
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		else
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.drawImage(bank.getImage(bank.getNumberOfImages() - 1), textX + textWidth + MARGIN, MARGIN, textX + textWidth + MARGIN + size, size + MARGIN, 0, 0, pieceSize, pieceSize, this);
	}
}
