/*==============================================================================
 * 
 * REVERSI
 * 
 * Classe : Piece
 * 
 * Description:
 * 	Classe des pièces retournables.
 * 
 =============================================================================*/

package pieces.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import pieces.gui.utils.Bank;
import pieces.utils.Coords;



public class Piece extends JPanel {
	private static final long serialVersionUID = 6120781714260705968L;
	private int tailleSprite;
	private int images;
	private int imagecourante;
	private boolean retourne;
	private Coords coordonnees;
	private Bank banque;
	private Thread animation;

	private final int DELTA = 25; // ms d'attente entre chaque image.

	public Piece(Bank banque, boolean retourne, int x, int y) {
		this.retourne = retourne;
		this.images = banque.getNombreImages();
		this.coordonnees = new Coords(x, y);
		setBanque(banque);
		setOpaque(false);
		setVisible(false);

		// Image à utiliser à l'initialisation en fonction de la disposition
		// de base de la pièce.
		if (retourne)
			this.imagecourante = images - 1;
		else
			this.imagecourante = 0;
	}

	public Coords getCoordonnees() {
		return this.coordonnees;
	}

	public boolean isReversed() {
		return retourne;
	}

	/*
	 * retourner()
	 * 
	 * Retourne directement la pièce.
	 */
	public void retourner() {
		retourner(0);
	}

	/*
	 * retourner(attente)
	 * 
	 * Retourne la pièce après un certain temps d'attente. Utile pour une série
	 * de pièces qui se retournent une après les autres.
	 */
	public void retourner(final int attente) {
		if (animation != null && animation.isAlive())
			animation.interrupt();

		if (!isVisible()) {
			setVisible(true);
			return;
		}
		retourne = !retourne;

		animation = new Thread(new Runnable() {

			// Animation du retournement de la pièce.
			@Override
			public void run() {
				try {
					Thread.sleep(attente);
					if (!retourne) {
						while (imagecourante > 0) {
							imagecourante--;
							repaint();
							Thread.sleep(DELTA);
						}
					} else {
						while (imagecourante < images - 1) {
							imagecourante++;
							repaint();
							Thread.sleep(DELTA);
						}
					}
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		});
		animation.start();
	}

	/*
	 * setBanque(banque)
	 * 
	 * Définition de la banque d'images à utiliser.
	 */
	public void setBanque(Bank banque) {
		this.banque = banque;
		this.tailleSprite = banque.getTaillePiece();
		repaint();
	}

	public void setRetourne(boolean retourne) {
		this.imagecourante = retourne ? images - 1 : 0;
		this.retourne = retourne;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (!isVisible())
			return;

		// https://today.java.net/pub/a/today/2004/11/12/graphics2d.html
		// OpenGL ne supporte pas la méthode de rendu BICUBIC.
		// On utilise donc ici la méthode BILINEAR.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Antialiasing.
		g2d.drawImage(banque.getImage(imagecourante), 0, 0, getWidth(), getHeight(), 0, 0, tailleSprite, tailleSprite, this);
		g2d.dispose();
	}
}
