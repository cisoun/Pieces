package pieces.gui.utils;

import java.awt.Color;

/**
 * Color manager.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class ColorTools {

	/**
	 * Check if color value is between 0 and 255.
	 * 
	 * @param valeur
	 * @return
	 */
	private static int checkColor(int valeur) {
		if (valeur < 0)
			return 0;
		if (valeur > 255)
			return 255;
		return valeur;
	}

	public static Color brighten(Color couleur, int valeur) {
		int r = checkColor(couleur.getRed() + valeur);
		int g = checkColor(couleur.getGreen() + valeur);
		int b = checkColor(couleur.getBlue() + valeur);
		return new Color(r, g, b);
	}

	public static Color darken(Color couleur, int valeur) {
		return brighten(couleur, -valeur);
	}

	/**
	 * Get the luminosity level between 0 and 255 of a color.
	 * 
	 * @param c Color.
	 * @return Luminosity level.
	 */
	public static int getLuminosity(Color c) {
		return (int) Math.sqrt(c.getRed() * c.getRed() * .241 + c.getGreen() * c.getGreen() * .691 + c.getBlue() * c.getBlue() * .068);
	}

	/**
	 * Get a text color depending in accordance with a background color. - Dark
	 * background = bright text. - Bright background = dark text.
	 * 
	 * @param background Background color.
	 * @return Text color.
	 */
	public static Color getTextColor(Color background) {
		if (getLuminosity(background) < 128)
			return ColorTools.brighten(background, 100);
		else
			return ColorTools.darken(background, 100);
	}

	public static String toHex(Color couleur) {
		return "#" + Integer.toHexString(couleur.getRGB()).substring(2, 8);
	}

	public static Color toColor(String hex) {
		return new Color(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16));
	}
}
