package pieces.gui.utils;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import pieces.Game;

/**
 * Visual themes manager.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class Themes {
	public static final String PATH = "themes";
	public static final Color BACKGROUND = SystemColor.window;
	public static final Color GRID_BACKGROUND = SystemColor.window;
	public static final Color GRID_LINES = SystemColor.window.brighter();
	public static final Color GRID_HINT = ColorTools.darken(GRID_BACKGROUND, 20);

	private static TreeMap<String, Theme> themes = new TreeMap<String, Theme>();
	private static String[] files = { "theme.properties", "sequence.png", "background.png" };
	private static String currentTheme = "Pieces";

	public static class Theme {
		private String name;
		private Color background;
		private Color gridBackground;
		private Color gridHint;
		private Color gridLines;
		private VolatileImage backgroundImage;

		Properties properties = new Properties();

		public Theme(String name) {
			this.name = name;
			if (hasBackgroundImage())
				this.backgroundImage = Graphics.loadFromFile(PATH + "/" + this.name + "/background.png");

			try {
				properties.load(new FileInputStream(PATH + "/" + name + "/theme.properties"));
				this.background = getPropriete("background", BACKGROUND);
				this.gridBackground = getPropriete("grid_background", GRID_BACKGROUND);
				this.gridHint = getPropriete("grid_hint", GRID_HINT);
				this.gridLines = getPropriete("grid_lines", GRID_LINES);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "No theme found...\rAborted.", Game.APP_NAME, JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}

		private Color getPropriete(String cle, Color defaut) {
			return ColorTools.toColor(properties.getProperty(cle, ColorTools.toHex(defaut)));
		}

		public Color getBackground() {
			return this.background;
		}

		public Color getGridBackground() {
			return this.gridBackground;
		}

		public VolatileImage getGridBackgroundImage() {
			return this.backgroundImage;
		}

		public Color getGridHint() {
			return this.gridHint;
		}

		public Color getGridLines() {
			return this.gridLines;
		}

		public String getName() {
			return this.name;
		}

		public String getSequencePath() {
			return "themes/" + this.name + "/sequence.png";
		}

		public boolean hasBackgroundImage() {
			File dossier = new File(PATH + "/" + this.name);
			List<String> liste = Arrays.asList(dossier.list());
			return liste.contains("background.png");
		}
	};

	public static boolean existsTheme(String theme) {
		if (themes == null)
			load();

		for (int i = 0; i < themes.size(); i++) {
			if (themes.get(i).getName() == theme)
				return true;
		}

		return false;
	}

	public static Theme getCurrentTheme() {
		return getTheme(currentTheme);
	}

	public static int getThemeIndex(String theme) {
		List<String> liste = new ArrayList<String>(themes.keySet());
		if (themes.containsKey(theme))
			return liste.indexOf(theme);
		else
			return -1;
	}

	public int getNumberOfThemes() {
		return Themes.themes.size();
	}

	public static Theme getTheme(String theme) {
		// If theme already exists, get it.
		if (themes.containsKey(theme) && themes.get(theme) != null)
			return themes.get(theme);
	
		// Otherwise, create it.
		Theme t = new Theme(theme);
		themes.put(theme, t);
		return t;
	}

	public static String[] getThemesName() {
		if (themes == null)
			load();
	
		return themes.keySet().toArray(new String[themes.size()]);
	}

	public static boolean isTheme(String theme) {
		File dossier = new File(PATH + "/" + theme);
		List<String> liste = Arrays.asList(dossier.list());
		if (liste.contains(files[0]) && liste.contains(files[1]))
			return true;
		return false;
	}

	public static void load() {
		themes.clear();
	
		File[] folders;
		File foldersTheme = new File(PATH);
		folders = foldersTheme.listFiles();
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File fichier) {
				return fichier.isDirectory();
			}
		};
		folders = foldersTheme.listFiles(fileFilter);
	
		for (File f : folders) {
			if (isTheme(f.getName()))
				themes.put(f.getName(), null);
		}
	}

	public static void setCurrentTheme(String theme) {
		currentTheme = theme;
	}
}
