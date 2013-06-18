package reversi.gui.utils;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

import reversi.gui.utils.Themes.Theme;

public class Themes {
	public static final String CHEMIN = "themes";
	public static final Color BACKGROUND = SystemColor.window;
	public static final Color GRID_BACKGROUND = SystemColor.window;
	public static final Color GRID_LINES = SystemColor.window.brighter();
	public static final Color GRID_HINT = Couleur.assombrir(GRID_BACKGROUND, 20);
	
	private static HashMap<String, Theme> _themes = new HashMap<String, Theme>();
	private static ArrayList<String> themesDisponibles = new ArrayList<String>();
	private static String[] fichiers = {"theme.properties", "sequence.png", "background.png"};
	private static String themeCourant = "Pieces";
	
	public static class Theme {
		private String name;
		private Color background;
		private Color gridBackground;
		private Color gridHint;
		private Color gridLines;
		private VolatileImage backgroundImage;
		
		Properties properties = new Properties();
		
		public Theme(String name)
		{
			this.name = name;
			if (hasBackgroundImage())
				this.backgroundImage = Graphics.loadFromFile(CHEMIN + "/" + this.name + "/background.png");

			try {
				properties.load(new FileInputStream(CHEMIN + "/" + name + "/theme.properties"));
				this.background = getPropriete("background", BACKGROUND);
				this.gridBackground = getPropriete("grid_background", GRID_BACKGROUND);
				this.gridHint = getPropriete("grid_hint", GRID_HINT);
				this.gridLines = getPropriete("grid_lines", GRID_LINES);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Pas de thème utilisable...\rAbandon.", "Pieces", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
		
		private Color getPropriete(String cle, Color defaut)
		{
			return Couleur.toColor(properties.getProperty(cle, Couleur.toHex(defaut)));
		}
		
		public Color getBackground()
		{
			return this.background;
		}
		
		public Color getGridBackground()
		{
			return this.gridBackground;
		}
		
		public VolatileImage getGridBackgroundImage()
		{
			return this.backgroundImage;
		}
		
		public Color getGridHint()
		{
			return this.gridHint;
		}
		
		public Color getGridLines()
		{
			return this.gridLines;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getSequencePath()
		{
			return "themes/" + this.name + "/sequence.png";
		}
		
		public boolean hasBackgroundImage()
		{
			File dossier = new File(CHEMIN + "/" + this.name);
			List<String> liste = Arrays.asList(dossier.list());
			return liste.contains("background.png");
		}
	};
	
	public static void chargerThemes()
	{
		themesDisponibles.clear();
	
		File[] dossiers;
		File dossierThemes = new File(CHEMIN);
		dossiers = dossierThemes.listFiles();
		FileFilter fileFilter = new FileFilter() {
		    @Override
			public boolean accept(File fichier) {
		        return fichier.isDirectory();
		    }
		};
		dossiers = dossierThemes.listFiles(fileFilter);
		
		for(File f:dossiers)
		{
			if (estValide(f.getName()))
				themesDisponibles.add(f.getName());
		}
		
		Collections.sort(themesDisponibles);
	}

	public static boolean estValide(String theme)
	{
		File dossier = new File(CHEMIN + "/" + theme);
		List<String> liste = Arrays.asList(dossier.list());
		if (liste.contains(fichiers[0]) && liste.contains(fichiers[1]))
			return true;
		return false;
	}
	
	public static boolean existeTheme(String theme)
	{
		if (themesDisponibles == null)
			chargerThemes();

		for (int i = 0; i < themesDisponibles.size(); i++)
		{
			if (themesDisponibles.get(i) == theme)
				return true;
		}

		return false;
	}
	
	public static String[] getNomThemes()
	{
		if (themesDisponibles == null)
			chargerThemes();

		String[] liste = new String[themesDisponibles.size()];
		liste = themesDisponibles.toArray(liste);
		return liste;
	}

	public static Theme getTheme(String theme)
	{
		// Si le thème existe déjà, le récupérer.
		if (_themes.containsKey(theme))
			return _themes.get(theme);
	
		// Autrement le créer.
		Theme t = new Theme(theme);
		_themes.put(theme, t);
		return t;
	}

	public static Theme getThemeCourant()
	{
		if (!_themes.containsKey(themeCourant))
			return getTheme(themeCourant);
		return _themes.get(themeCourant);
	}
	
	public static int getThemeIndex(String theme)
	{
		if (themesDisponibles.contains(theme))
			return themesDisponibles.indexOf(theme);
		else
			return -1;
	}

	public int nombreThemes()
	{
		return Themes.themesDisponibles.size();
	}
	
	public static void setThemeCourant(String theme)
	{
		themeCourant = theme;
	}
}
