package reversi;

import java.rmi.RemoteException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Réglages pour performances graphiques.
		 */
		// Performances :
		// http://www.oracle.com/technetwork/java/javase/tsg-desktop-150005.pdf
		// System.setProperty("sun.java2d.opengl","true");
		System.setProperty("sun.java2d.opengl.fbobject", "false");

		// Log concernant l'accélération graphique effectuée sur le jeu après
		// la fermeture du programme.
		// System.setProperty("sun.java2d.trace", "count");

		// Windows/DirectX
		// System.setProperty("sun.java2d.translaccel", "true"); // DirectX

		// Désactive l'utilisation de pixmaps.
		System.setProperty("sun.java2d.pmoffscreen", "false");

		/*
		 * Look'n'feel du système.
		 */
		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		/*
		 * Lancement du jeu.
		 */
		try {
			Reversi jeu = new Reversi();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
