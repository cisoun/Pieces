package reversi.gui.utils;

import java.awt.Color;

public class Couleur {

	private static int verifierCouleur(int valeur) {
		if (valeur < 0)
			return 0;
		if (valeur > 255)
			return 255;
		return valeur;
	}

	public static Color eclaircir(Color couleur, int valeur) {
		int r = verifierCouleur(couleur.getRed() + valeur);
		int g = verifierCouleur(couleur.getGreen() + valeur);
		int b = verifierCouleur(couleur.getBlue() + valeur);
		return new Color(r, g, b);
	}

	public static Color assombrir(Color couleur, int valeur) {
		return eclaircir(couleur, -valeur);
	}

	public static int luminosite(Color c) {
		return (int) Math.sqrt(c.getRed() * c.getRed() * .241 + c.getGreen() * c.getGreen() * .691 + c.getBlue() * c.getBlue() * .068);
	}

	public static String toHex(Color couleur) {
		return "#" + Integer.toHexString(couleur.getRGB()).substring(2, 8);
	}

	public static Color toColor(String hex) {
		return new Color(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16));
	}

	public static Color couleurTexte(Color fond) {
		if (luminosite(fond) < 128)
			return Couleur.eclaircir(fond, 100);
		else
			return Couleur.assombrir(fond, 100);
	}
}
