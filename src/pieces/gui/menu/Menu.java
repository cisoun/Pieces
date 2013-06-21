package pieces.gui.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

import pieces.gui.utils.ColorTools;
import pieces.gui.utils.Themes;

public class Menu extends JPanel {
	private static final long serialVersionUID = 1L;

	private final static int MARGIN = 8;
	private final static int SIZE = 10;

	private final static int NORMAL = 0;
	private final static int HOVER = 1;
	private final static int PRESSED = 2;

	private String text;
	private int state;

	public Menu(String texte) {
		setText(texte);

		setOpaque(false);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (state != PRESSED)
					return;
				state = HOVER;
				repaint();
				action();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				state = PRESSED;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				state = NORMAL;
				repaint();

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				state = HOVER;
				repaint();
			}
		});
	}

	public void action() {
		// TODO Auto-generated method stub

	}

	public static int getHauteur() {
		return SIZE + 2 * MARGIN;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color background = Themes.getCurrentTheme().getBackground();
		Color couleur = ColorTools.getTextColor(background);

		// Antialiasing
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		if (state == HOVER) {
			g2d.setColor(Color.black);
			GradientPaint gp = new GradientPaint(0, 0, ColorTools.brighten(background, 30), 0, getHeight(), background);

			g2d.setPaint(gp);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.setColor(ColorTools.darken(background, 20));
			g2d.drawLine(0, 0, 0, getHeight());
			g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		} else if (state == PRESSED) {
			g2d.setColor(Color.black);
			GradientPaint gp = new GradientPaint(0, 0, ColorTools.darken(background, 150), 0, getHeight(), ColorTools.darken(background, 50));

			g2d.setPaint(gp);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.setColor(ColorTools.darken(background, 100));
			g2d.drawLine(0, 0, 0, getHeight());
			g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		}

		// Text
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, SIZE));

		g2d.setColor(ColorTools.brighten(background, 20));
		g2d.drawString(text, MARGIN * 2, SIZE + MARGIN + 1);

		if (state != PRESSED && ColorTools.getLuminosity(background) < 128) {
			g2d.setColor(ColorTools.darken(background, 150));
			g2d.drawString(text, MARGIN * 2, SIZE + MARGIN - 1);
		}

		g2d.setColor(couleur);
		g2d.drawString(text, MARGIN * 2, SIZE + MARGIN);
	}

	public void setText(String text) {
		this.text = text;

		FontMetrics fm = this.getFontMetrics(new Font(Font.SANS_SERIF, Font.BOLD, SIZE));

		setPreferredSize(new Dimension(fm.stringWidth(text) + MARGIN * 4, MARGIN * 2 + SIZE));

		revalidate();
		repaint();
	}
}
