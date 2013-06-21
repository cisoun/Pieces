package pieces.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import pieces.gui.utils.Bank;
import pieces.utils.Coords;

public class Piece extends JPanel {
	private static final long serialVersionUID = 1L;

	private int spriteSize;
	private int images;
	private int currentImage;
	private boolean side;
	private Coords coords;
	private Bank bank;
	private Thread animation;

	private final int DELTA = 25; // MS between each frame.

	public Piece(Bank bank, boolean side, int x, int y) {
		this.side = side;
		this.images = bank.getNumberOfImages();
		this.coords = new Coords(x, y);
		setBank(bank);
		setOpaque(false);
		setVisible(false);

		// Initial image.
		if (side)
			this.currentImage = images - 1;
		else
			this.currentImage = 0;
	}

	public Coords getCoords() {
		return this.coords;
	}

	public boolean isReversed() {
		return side;
	}

	public void reverse() {
		reverse(0);
	}

	/**
	 * Reverse the piece after a defined time lapse.
	 * @param timelapse
	 */
	public void reverse(final int timelapse) {
		if (animation != null && animation.isAlive())
			animation.interrupt();

		if (!isVisible()) {
			setVisible(true);
			return;
		}
		side = !side;

		animation = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(timelapse);
					if (!side) {
						while (currentImage > 0) {
							currentImage--;
							repaint();
							Thread.sleep(DELTA);
						}
					} else {
						while (currentImage < images - 1) {
							currentImage++;
							repaint();
							Thread.sleep(DELTA);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		animation.start();
	}

	public void setBank(Bank bank) {
		this.bank = bank;
		this.spriteSize = bank.getPieceSize();
		repaint();
	}

	public void setSide(boolean side) {
		this.currentImage = side ? images - 1 : 0;
		this.side = side;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (!isVisible())
			return;

		// https://today.java.net/pub/a/today/2004/11/12/graphics2d.html
		// OpenGL doesn't support the BICUBIC render method.
		// So we use here the BILINEAR method.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // Antialiasing.
		g2d.drawImage(bank.getImage(currentImage), 0, 0, getWidth(), getHeight(), 0, 0, spriteSize, spriteSize, this);
		g2d.dispose();
	}
}
