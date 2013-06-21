package pieces.gui.utils;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Class for graphics utility.
 * 
 * VolatileImage methods taken from : <a
 * href="http://content.gpwiki.org/index.php/Java:Tutorials:VolatileImage"
 * >http://content.gpwiki.org/index.php/Java:Tutorials:VolatileImage</a>
 */
public class Graphics {
	/**
	 * Create a simple VolatileImage.
	 * 
	 * @return VolatileImage.
	 */
	public static VolatileImage createVolatileImage(int width, int height, int transparency) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		VolatileImage image = null;

		image = gc.createCompatibleVolatileImage(width, height, transparency);

		int valid = image.validate(gc);

		if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
			image = createVolatileImage(width, height, transparency);
			return image;
		}

		return image;
	}

	/**
	 * Create a VolatileImage from a file. Skip the BufferedImage to
	 * VolatileImage process when used.
	 * 
	 * @return VolatileImage.
	 */
	public static VolatileImage loadFromFile(String filename) {
		// GraphicsEnvironment ge =
		// GraphicsEnvironment.getLocalGraphicsEnvironment();
		// GraphicsConfiguration gc =
		// ge.getDefaultScreenDevice().getDefaultConfiguration();

		// Loads the image from a file using ImageIO.
		BufferedImage bimage;
		try {
			bimage = ImageIO.read(new File(filename));
		} catch (IOException e) {
			// Create an empty BufferedImage.
			bimage = new BufferedImage(0, 0, 0);
			e.printStackTrace();
		}

		// From Code Example 2.
		VolatileImage vimage = createVolatileImage(bimage.getWidth(), bimage.getHeight(), Transparency.OPAQUE);

		Graphics2D g = null;

		try {
			g = vimage.createGraphics();

			g.drawImage(bimage, null, 0, 0);
		} finally {
			// It's always best to dispose of your Graphics objects.
			g.dispose();
		}

		return vimage;
	}
}
