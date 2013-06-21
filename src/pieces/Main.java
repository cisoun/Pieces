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
		 * Graphics settings.
		 */
		// Performances :
		// http://www.oracle.com/technetwork/java/javase/tsg-desktop-150005.pdf
		// System.setProperty("sun.java2d.opengl","true");
		System.setProperty("sun.java2d.opengl.fbobject", "false");

		// Shows a log about graphic acceleration after the program has closed.
		// System.setProperty("sun.java2d.trace", "count");

		// Windows/DirectX
		// System.setProperty("sun.java2d.translaccel", "true"); // DirectX

		// Disable pixmap use.
		// This option may be useless but can improve a little the animations.
		//System.setProperty("sun.java2d.pmoffscreen", "false");

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
		System.out.println(Game.APP_NAME);
		System.out.println(Game.APP_VERSION);
		try {
			new Game();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
