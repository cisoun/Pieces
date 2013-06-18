package reversi.gui.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.jws.Oneway;
import javax.swing.JLabel;
import javax.swing.JPanel;

import reversi.gui.GUI;
import reversi.gui.utils.Couleur;
import reversi.gui.utils.Themes;

public class Menu extends JPanel {

	private static final long serialVersionUID = 1L;
	private final static int MARGE = 8;
	private final static int TAILLE = 10;

	private final static int NORMAL = 0;
	private final static int HOVER = 1;
	private final static int PRESSED = 2;

	private String texte;

	private int etat;

	private int texteLargeur;
	private int texteHauteur;

	public Menu(String texte) {
		setTexte(texte);

		setOpaque(false);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (etat != PRESSED)
					return;
				etat = HOVER;
				repaint();
				action();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				etat = PRESSED;
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				etat = NORMAL;
				repaint();

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				etat = HOVER;
				repaint();
			}
		});
	}

	public void action() {
		// TODO Auto-generated method stub

	}

	public static int getHauteur() {
		return TAILLE + 2 * MARGE;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color background = Themes.getThemeCourant().getBackground();
		Color couleur = Couleur.couleurTexte(background);

		// Antialiasing.
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		if (etat == HOVER) {
			g2d.setColor(Color.black);
			GradientPaint gp = new GradientPaint(0, 0, Couleur.eclaircir(background, 30), 0, getHeight(), background);

			g2d.setPaint(gp);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.setColor(Couleur.assombrir(background, 20));
			g2d.drawLine(0, 0, 0, getHeight());
			g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		} else if (etat == PRESSED) {
			g2d.setColor(Color.black);
			GradientPaint gp = new GradientPaint(0, 0, Couleur.assombrir(background, 150), 0, getHeight(), Couleur.assombrir(background, 50));

			g2d.setPaint(gp);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.setColor(Couleur.assombrir(background, 100));
			g2d.drawLine(0, 0, 0, getHeight());
			g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
		}

		// Texte.
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, TAILLE));

		g2d.setColor(Couleur.eclaircir(background, 20));
		g2d.drawString(texte, MARGE * 2, TAILLE + MARGE + 1);

		if (etat != PRESSED && Couleur.luminosite(background) < 128) {
			g2d.setColor(Couleur.assombrir(background, 150));
			g2d.drawString(texte, MARGE * 2, TAILLE + MARGE - 1);
		}

		g2d.setColor(couleur);
		g2d.drawString(texte, MARGE * 2, TAILLE + MARGE);
	}

	public void setTexte(String texte) {
		this.texte = texte;

		FontMetrics fm = this.getFontMetrics(new Font(Font.SANS_SERIF, Font.BOLD, TAILLE));

		texteLargeur = fm.stringWidth(texte);
		texteHauteur = fm.getHeight();

		setPreferredSize(new Dimension(fm.stringWidth(texte) + MARGE * 4, MARGE * 2 + TAILLE));

		revalidate();
		repaint();
	}
}
