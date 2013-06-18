package reversi.utils;

import java.util.prefs.Preferences;

/**
 * Gestionnaire de configuration du jeu.
 * On utilise ici l'objet Preferences pour stocker les données.
 * @author Cyriaque Skrapits
 * 
 */
public class Config {
	private static Preferences preferences;
	public static final String AFFICHER_COUPS_POSSIBLES = "afficher_coups_possibles";
	public static final String AFFICHER_GRILLE = "afficher_grille";
	public static final String RETOURNEMENT_FIN = "retournement_fin";
	public static final String DIFFICULTE_NOIR = "difficulte_noir";
	public static final String DIFFICULTE_BLANC = "difficulte_blanc";
	public static final String PORT = "serveur_port";
	public static final String SERVEUR = "serveur";
	public static final String THEME = "theme";

	public static void charger() {
		preferences = Preferences.userNodeForPackage(Config.class);
	}

	/*
	 * Récupération.
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
	 * Enregistrements.
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
