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
import pieces.gui.utils.Couleur;
import pieces.gui.utils.Themes;
import pieces.utils.Matrix.MatrixPiece;



public class Scores extends JPanel {
	private Game reversi;
	private Bank banque;
	private static int MARGE = 20;
	private FontMetrics fontMetrics;
	private Font police = new Font(Font.SANS_SERIF, Font.BOLD, 40);
	
	public Scores(Game reversi, Bank banque)
	{
		this.reversi = reversi;
		this.banque = banque;
		setLayout(new FlowLayout());
		//add(lblScore);

		setPreferredSize(new Dimension(getWidth(), 80));
		setBackground(Themes.getThemeCourant().getGridBackground());

		afficher();

		fontMetrics = this.getFontMetrics(police);
		
		
	}


	public void afficher()
	{
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		Color background = Themes.getThemeCourant().getBackground();
		
		
		int taillePiece = banque.getTaillePiece();
		int taille = getHeight() - MARGE * 2;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Fond.
		GradientPaint gp = new GradientPaint(0, 0, background, 0, getHeight(),Couleur.assombrir(background, 50));
		

		g2d.setPaint(gp);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setColor(background.darker());
		g2d.drawLine(0, 0, getWidth(), 0);
		
		g2d.setColor(background.brighter());
		g2d.drawLine(0, 1, getWidth(), 1);

		// Texte.
		String texte = reversi.matrice.score(MatrixPiece.NOIR) + " - " + reversi.matrice.score(MatrixPiece.BLANC);
		int texteLargeur = fontMetrics.stringWidth(texte);
		int texteHauteur = fontMetrics.getHeight();
		int texteX = getWidth() / 2 - texteLargeur / 2;
		int texteY = getHeight() / 2 - texteHauteur / 2 + 40;
		GradientPaint gpTexte = new GradientPaint(0, 0, Couleur.assombrir(background, 20), 0, texteHauteur, Couleur.assombrir(background, 70));
		Color couleur = Couleur.luminosite(background) < 128 ? background.brighter() : background.darker();
		
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2d.setFont(police);
		
		g2d.setColor(Couleur.eclaircir(background, 30));
		g2d.drawString(texte, texteX, texteY + 1);
		
		g2d.setColor(Couleur.assombrir(background, 70));
		g2d.drawString(texte, texteX, texteY - 1);
		
		g2d.setColor(background.brighter());
		g2d.setPaint(gpTexte);
		g2d.drawString(texte, texteX, texteY);
		
		// Pièces.
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Antialiasing.
		
		if (reversi.tour())
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));

		g2d.drawImage(banque.getImage(0),
				texteX - taille - MARGE,
				MARGE,
				texteX - MARGE,
				taille + MARGE,
				0, 0, taillePiece, taillePiece,
				this);
		
		if (!reversi.tour())
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		else
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.drawImage(banque.getImage(banque.getNombreImages() - 1),
				texteX + texteLargeur + MARGE,
				MARGE,
				texteX + texteLargeur +  MARGE + taille,
				taille + MARGE,
				0, 0, taillePiece, taillePiece,
				this);
	}
}
