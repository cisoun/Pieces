/*==============================================================================
 * 
 * REVERSI
 * 
 * Classe : BanquePieces
 * 
 * Description:
 * 	Banque d'images disponibles.
 * 	Charge l'image comportant la séquence du retournement de la pièce puis
 * 	la divise en plusieurs BufferedImages.
 * 	
 * 	La classe Piece peut ensuite l'utiliser pour dessiner la pièce et s'animer.
 * 
 =============================================================================*/

package reversi.gui.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import reversi.gui.GUI;

public class BanquePieces {
	private static final int IMAGES = 10;
	
	private VolatileImage[] banque;
	private int images;
	private int taillePiece;

	public BanquePieces(String fichierSequence, int images) throws SequenceIntrouvableException, IOException {
		chargerBanque(fichierSequence);
	}

	public void chargerBanque(String fichierSequence) throws SequenceIntrouvableException, IOException {
		File fichier = new File(fichierSequence);

		// Vérifie l'existence du fichier et renvoie une exception si ce n'est
		// pas le cas.
		if (!fichier.exists()) {
			throw new SequenceIntrouvableException();
		}

		// Récupération de la séquence.
		BufferedImage sequence = ImageIO.read(fichier);
		banque = new VolatileImage[IMAGES];

		// La hauteur de l'image de la séquence défini la taille des pièces.
		taillePiece = sequence.getHeight(null);

		// Découpage de la séquence.
		for (int i = 0; i < IMAGES; i++) {
			BufferedImage image = sequence.getSubimage(i * taillePiece, 0, taillePiece, taillePiece);
			// Passage du BufferedImage au VolatileImage afin d'accélérer le
			// rendu.
			// Transparency.BITMASK peut être utilisé pour accélérer encore plus
			// le processus
			// mais l'anti-aliasing sera ignoré.
			banque[i] = Graphics.createVolatileImage(taillePiece, taillePiece, Transparency.TRANSLUCENT);
			Graphics2D g = null;
			g = banque[i].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setComposite(AlphaComposite.Src);
			g.clearRect(0, 0, taillePiece, taillePiece);
			g.drawImage(image, null, 0, 0);
			g.dispose();
		}
	}

	public VolatileImage[] getBanque() {
		return banque;
	}

	public VolatileImage getImage(int n) {
		return banque[n];
	}

	public int getNombreImages() {
		return IMAGES;
	}

	public int getTaillePiece() {
		return taillePiece;
	}

	@SuppressWarnings("serial")
	public class SequenceIntrouvableException extends Exception {
		public SequenceIntrouvableException() {
			
		}
	}
}
