package pieces.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import javax.swing.JPanel;

import pieces.gui.utils.Couleur;
import pieces.gui.utils.Ease;


/**
 * Barre de message affichée en bas de la grille.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class Message extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final Color ATTENTION = new Color(255, 214, 51);
	public static final Color ERREUR = new Color(255, 92, 51);
	public static final Color OK = new Color(153, 204, 51);
	public static final Color NEUTRE = new Color(51, 173, 255);

	private GUI parent;
	private String texte;
	private Color couleur;
	private int hauteur;
	private boolean persistant;
	private int marge = 0;
	private LinkedList<MessageStruct> liste;
	private Thread animation;

	private class MessageStruct {
		public String texte;
		public Color couleur;
		public boolean persistant;
	}

	public Message(GUI parent) {
		this.parent = parent;
		this.texte = "";
		this.couleur = NEUTRE;

		liste = new LinkedList<MessageStruct>();

		FontMetrics fm = getFontMetrics(getFont()); // this.getFontMetrics(new
													// Font(Font.SANS_SERIF,
													// Font.BOLD, TAILLE));
		hauteur = fm.getHeight() + 10;
		marge = fm.getHeight() + 10;
		setPreferredSize(new Dimension(getWidth(), hauteur));
		setDoubleBuffered(true);
		setOpaque(false);
	}

	private void messagesSuivants() {
		stopAnimation();
		liste.poll();
		if (liste.size() > 0) {

			animation();
		}

	}

	private synchronized void animation() {
		final MessageStruct m = liste.peek();
		animation = new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				try {
					for (int i = 1; i <= 10; i++) {
						marge = (int) (hauteur * (1.0 - Ease.InOutSine(i, 0, 1, 10)));
						repaint();
						Thread.sleep(20);
					}
					if (m.persistant)
						return;
					Thread.sleep(2000);
					for (int i = 1; i <= 10; i++) {
						marge = (int) (hauteur * Ease.InOutSine(i, 0, 1, 10));
						repaint();
						Thread.sleep(20);
					}
					messagesSuivants();
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		});
		animation.start();
	}

	public void cacher() {
		stopAnimation();
		marge = hauteur;
		repaint();
	}

	public int getHauteur() {
		return hauteur;
	}

	public void message(String texte, Color couleur, boolean persistant, boolean urgent) {

		MessageStruct m = new MessageStruct();
		m.texte = texte;
		m.couleur = couleur;
		m.persistant = persistant;

		if (urgent) {
			stopAnimation();
			liste.clear();
			liste.addFirst(m);
		} else {
			liste.add(m);

		}

		if (liste.size() == 1)
			animation();
	}

	private void stopAnimation() {
		if (animation != null && animation.isAlive()) {
			animation.interrupt();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		if (liste.size() == 0)
			return;
		MessageStruct m = liste.peek();

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Fond.
		GradientPaint gp = new GradientPaint(0, 0, m.couleur, 0, getHeight(), Couleur.assombrir(m.couleur, 20));
		g2d.setPaint(gp);
		g2d.fillRect(0, marge, getWidth(), getHeight());

		// Bordure.
		g2d.setColor(m.couleur.darker());
		g2d.drawLine(0, marge, getWidth(), marge);

		g2d.setColor(m.couleur.brighter());
		g2d.drawLine(0, marge + 1, getWidth(), marge + 1);

		// Texte.
		FontMetrics fm = getFontMetrics(getFont());
		int x = getWidth() / 2 - fm.stringWidth(m.texte) / 2;

		g2d.setColor(Couleur.eclaircir(m.couleur, 40));
		g2d.drawString(m.texte, x, marge + getHeight() - 6);

		g2d.setColor(Couleur.assombrir(m.couleur, 150));
		g2d.drawString(m.texte, x, marge + getHeight() - 7);

		g2d.dispose();
	}
}
