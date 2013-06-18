package reversi.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MenuBar;
import java.awt.image.VolatileImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import reversi.gui.menu.BarreMenu;
import reversi.gui.utils.Themes;

public class Plateau extends JPanel {
	private Grille grille;
	private Message message;
	private Options options;
	private Multijoueur multijoueur;

	public Plateau(Grille grille, Message message, Options options, Multijoueur multijoueur) {
		this.grille = grille;
		this.message = message;
		this.options = options;
		this.multijoueur = multijoueur;

		setLayout(new BorderLayout());
		add(Box.createVerticalStrut(message.getHauteur()), BorderLayout.NORTH);
		add(grille, BorderLayout.CENTER);
		add(message, BorderLayout.SOUTH);

		setBackground(Themes.getThemeCourant().getGridBackground());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		if (Themes.getThemeCourant().hasBackgroundImage()) {
			VolatileImage image = Themes.getThemeCourant().getGridBackgroundImage();
			int largeur = image.getWidth();
			int hauteur = image.getHeight();
			for (int x = 0; x < getWidth() / largeur + 1; x++)
				for (int y = 0; y < getHeight() / hauteur + 1; y++) {
					g2d.drawImage(image, x * largeur, y * hauteur, largeur, hauteur, null);
				}
		}
		if (!multijoueur.isCache())
		{
			g2d.setColor(Color.BLACK);
			for (int i = 0; i < 20; i++) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (20 - i) / 200.0f));
				g2d.drawLine(0, i, getWidth(), i);
			}
			g2d.setComposite(AlphaComposite.SrcOver);
		}

		if (!options.isCache()) {
			g2d.setColor(Color.BLACK);
			for (int i = 0; i < 20; i++) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, i / 100.0f));
				g2d.drawLine(getWidth() - 20 + i, 0, getWidth() - 20 + i, getHeight());
			}
			g2d.setComposite(AlphaComposite.SrcOver);
		}
	}
}