package pieces;

import java.rmi.RemoteException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main program.
 * @author Cyriaque Skrapits
 * 
 */
public class Main {

	/**
	 * Main entry.
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
		 * Look'n'feel.
		 */
		try {
			// Set system's L&F as the game's.
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
		 * Let's launch the game.
		 */
		try {
			Game game = new Game();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
