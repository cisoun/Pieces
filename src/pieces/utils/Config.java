package pieces.utils;

import java.util.prefs.Preferences;

/**
 * Gestionnaire de configuration du jeu.
 * On utilise ici l'objet Preferences pour stocker les données.
 * @author Cyriaque Skrapits
 * 
 */
public class Config {
	private static Preferences preferences;
	public static final String SHOW_MOVES = "show_moves";
	public static final String SHOW_GRID = "show_grid";
	public static final String END_ANIMATION = "end_animation";
	public static final String DIFFICULTY_BLACK = "difficulty_black";
	public static final String DIFFICULTY_WHITE = "difficulty_white";
	public static final String PORT = "multiplayer_port";
	public static final String SERVER = "multiplayer_server";
	public static final String THEME = "theme";

	public static void load() {
		preferences = Preferences.userNodeForPackage(Config.class);
	}

	/*
	 * Load
	 */
	public static String get(String cle, String defaut) {
		return preferences.get(cle, defaut);
	}

	public static boolean get(String cle, boolean defaut) {
		return preferences.getBoolean(cle, defaut);
	}
	
	public static int get(String cle, int defaut) {
		return preferences.getInt(cle, defaut);
	}

	/*
	 * Save
	 */
	public static void set(String cle, int valeur) {
		preferences.putInt(cle, valeur);
	}

	public static void set(String cle, boolean valeur) {
		preferences.putBoolean(cle, valeur);
	}
	
	public static void set(String cle, String valeur) {
		preferences.put(cle, valeur);
	}
}
