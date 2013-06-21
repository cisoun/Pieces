package pieces.gui.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Load a sequence image and cut it in 10 parts which are loaded into an array
 * of VolatileImage.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class Bank {
	private static final int IMAGES = 10;

	private VolatileImage[] bank;
	private int pieceSize;

	public Bank(String sequence, int images) throws SequenceNotFoundException, IOException {
		load(sequence);
	}

	public void load(String fichierSequence) throws SequenceNotFoundException, IOException {
		File file = new File(fichierSequence);

		// Check if sequence exists otherwise, throws an exception.
		if (!file.exists()) {
			throw new SequenceNotFoundException();
		}

		// Load the sequence.
		BufferedImage sequence = ImageIO.read(file);
		bank = new VolatileImage[IMAGES];

		// Sequence's height define the size of the pieces.
		pieceSize = sequence.getHeight(null);

		// Cut the sequence into sub-images.
		for (int i = 0; i < IMAGES; i++) {
			BufferedImage image = sequence.getSubimage(i * pieceSize, 0, pieceSize, pieceSize);
			// Convert a BufferedImage to VolatileImage in order to make the
			// rendering faster.
			// Transparency.BITMASK can be used to fasten the process but it
			// will disable the antialising.
			bank[i] = Graphics.createVolatileImage(pieceSize, pieceSize, Transparency.TRANSLUCENT);
			Graphics2D g = null;
			g = bank[i].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setComposite(AlphaComposite.Src);
			g.clearRect(0, 0, pieceSize, pieceSize);
			g.drawImage(image, null, 0, 0);
			g.dispose();
		}
	}

	public VolatileImage[] getBanque() {
		return bank;
	}

	public VolatileImage getImage(int n) {
		return bank[n];
	}

	public int getNumberOfImages() {
		return IMAGES;
	}

	public int getPieceSize() {
		return pieceSize;
	}

	@SuppressWarnings("serial")
	public class SequenceNotFoundException extends Exception {
		public SequenceNotFoundException() {

		}
	}
}
