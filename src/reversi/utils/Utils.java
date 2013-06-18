package reversi.utils;

import javax.swing.JOptionPane;

public class Utils {
	public static void debug(String context, String message)
	{
		System.out.println("[" + context + "] " + message);
	}
	
	public static void message(String titre, String message, int type)
	{
		JOptionPane.showMessageDialog(null, message, titre, type);
	}
}
