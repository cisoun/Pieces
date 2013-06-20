package pieces.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pieces.Game;
import pieces.gui.utils.ColorTools;
import pieces.gui.utils.Ease;
import pieces.gui.utils.Themes;
import pieces.gui.utils.Bank.SequenceNotFoundException;
import pieces.utils.Config;


public class Options extends JPanel {

	private final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
	private final Font FONT_TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	private final String TITLE = "OPTIONS";
	private final int MARGIN_TITLE = 1;

	private Game game;
	private GUI gui;
	private JLabel lblTitleAppearance;
	private JLabel lblTitleGame;
	private JLabel lblAppearanceTheme;
	private JLabel lblGameBlackPlayer;
	private JLabel lblGameWhitePlayer;
	private JCheckBox cbGameFinalAnimation;
	private JCheckBox cbAppearanceShowGrid;
	private JCheckBox cbAppearanceShowMoves;
	private JComboBox<String> cboAppearanceTheme;
	private JComboBox<String> cboGameBlackPlayer;
	private JComboBox<String> cboGameWhitePlayer;

	private int titleWidth;
	private int titleHeight;

	private String[] difficulties = new String[] { "Human", "Easy", "Medium", "Hard" };

	private boolean cache = true;

	public Options(Game game, GUI gui) {

		FontMetrics fm = this.getFontMetrics(FONT_TITLE);
		titleHeight = fm.stringWidth(TITLE);
		titleWidth = fm.getHeight();

		this.game = game;
		this.gui = gui;

		// Titles
		lblTitleAppearance = new JLabel("Appearance");
		lblTitleAppearance.setFont(FONT_TITLE);

		lblTitleGame = new JLabel("Game");
		lblTitleGame.setFont(FONT_TITLE);

		// Options
		cbAppearanceShowGrid = new JCheckBox("Show grid");
		//cbApparenceAfficherGrille.setFont(POLICE);
		cbAppearanceShowGrid.setOpaque(false);
		cbAppearanceShowGrid.setSelected(Config.get(Config.SHOW_GRID, true));
		cbAppearanceShowGrid.addActionListener(alAfficherGrille);

		cbAppearanceShowMoves = new JCheckBox("Show moves");
		//cbApparenceCasesPossibles.setFont(POLICE);
		cbAppearanceShowMoves.setOpaque(false);
		cbAppearanceShowMoves.setSelected(Config.get(Config.SHOW_MOVES, false));
		cbAppearanceShowMoves.addActionListener(alCoupsPossibles);

		lblAppearanceTheme = new JLabel("Visual theme : ");
		//lblApparenceTheme.setFont(POLICE);

		cboAppearanceTheme = new JComboBox<String>(Themes.getNomThemes());
		cboAppearanceTheme.setOpaque(false);
		cboAppearanceTheme.setSelectedIndex(Themes.getThemeIndex(Config.get(Config.THEME, "Default")));
		cboAppearanceTheme.addActionListener(alThemes);

		cbGameFinalAnimation = new JCheckBox("Reverse pieces at the end");
		cbGameFinalAnimation.setOpaque(false);
		cbGameFinalAnimation.setSelected(Config.get(Config.END_ANIMATION, false));
		cbGameFinalAnimation.addActionListener(alRetournementFin);

		lblGameBlackPlayer = new JLabel("Black player : ");

		cboGameBlackPlayer = new JComboBox<String>(difficulties);
		cboGameBlackPlayer.setOpaque(false);
		cboGameBlackPlayer.setSelectedIndex(Integer.valueOf(Config.get(Config.DIFFICULTY_BLACK, "0")));
		cboGameBlackPlayer.addActionListener(alDifficulteNoir);

		lblGameWhitePlayer = new JLabel("White player : ");

		cboGameWhitePlayer = new JComboBox<String>(difficulties);
		cboGameWhitePlayer.setOpaque(false);
		cboGameWhitePlayer.setSelectedIndex(Integer.valueOf(Config.get(Config.DIFFICULTY_WHITE, "0")));
		cboGameWhitePlayer.addActionListener(alDifficulteBlanc);

		// Layout
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		int y = 0;

		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Appearance
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(lblTitleAppearance, gbc);

		gbc.gridy = y++;
		add(Box.createVerticalStrut(30));

		gbc.gridy = y++;
		add(cbAppearanceShowGrid, gbc);

		gbc.gridy = y++;
		add(cbAppearanceShowMoves, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = y;
		add(lblAppearanceTheme, gbc);

		gbc.gridx = 1;
		gbc.gridy = y++;
		add(cboAppearanceTheme, gbc);

		// Game
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(Box.createVerticalStrut(30), gbc);

		gbc.gridy = y++;
		add(lblTitleGame, gbc);

		gbc.gridy = y++;
		add(Box.createVerticalStrut(10), gbc);

		gbc.gridy = y++;
		add(cbGameFinalAnimation, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(lblGameBlackPlayer, gbc);

		gbc.gridx = 1;
		add(cboGameBlackPlayer, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(lblGameWhitePlayer, gbc);

		gbc.gridx = 1;
		add(cboGameWhitePlayer, gbc);

		setPreferredSize(new Dimension(0, getHeight()));
		setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 10));

		redessiner();
	}

	public void afficher() {
		Thread animation = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean c = cache;
				for (int i = 0; i <= 10; i++) {
					int x = (int) (Math.abs((c ? 1.0 : 0.0) - (Ease.InOutSine(i, 0, 1, 10))) * 300);
					setPreferredSize(new Dimension(x, getHeight()));
					setSize(x, getHeight());
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					revalidate();
				}
			}
		});
		animation.start();

		cache = !cache;
	}

	public boolean isCache() {
		return cache;
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		// Anti-aliasing.
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Ombre.
		// GradientPaint gp = new GradientPaint(0, 0, reversi.getBackground(),
		// 10, 0,Couleur.assombrir(reversi.getBackground(), 20));
		// g2d.setPaint(gp);
		// g2d.fillRect(0, 0, 10, getHeight());

		// Titre.
		g2d.setColor(ColorTools.darken(gui.getBackground(), 20));
		g2d.fillRect(MARGIN_TITLE, 0, titleWidth + 20, getHeight());

		g2d.setColor(new Color(50, 50, 50));
		// g2d.fillRect(0, 20, titreLargeur + 20, titreHauteur + 20);
		g2d.fillRect(MARGIN_TITLE, 0, titleWidth + 20, getHeight());

		g2d.translate(MARGIN_TITLE + titleWidth + 7, titleHeight + 25);
		g2d.rotate(Math.toRadians(-90));

		g2d.setFont(FONT_TITLE);
		g2d.setColor(Color.WHITE);
		g2d.drawString(TITLE, 0, 0);

		g2d.rotate(Math.toRadians(90));
		g2d.translate(-MARGIN_TITLE - titleWidth - 7, -titleHeight - 25);

		// Bordure.
		// g2d.setColor(Themes.getThemeCourant().getBackground());
		// g2d.drawLine(MARGE_TITRE, 0, MARGE_TITRE, getHeight());
	}

	ActionListener alAfficherGrille = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selectionne = cbAppearanceShowGrid.isSelected();
			Config.set(Config.SHOW_GRID, selectionne);
			cbAppearanceShowGrid.setSelected(selectionne);
			gui.getGrille().repaint();
		}
	};

	ActionListener alCoupsPossibles = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selectionne = cbAppearanceShowMoves.isSelected();
			Config.set(Config.SHOW_MOVES, selectionne);
			cbAppearanceShowMoves.setSelected(selectionne);
			cbAppearanceShowMoves.repaint();
			gui.getGrille().repaint();
		}
	};

	ActionListener alThemes = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String nom = cboAppearanceTheme.getItemAt(cboAppearanceTheme.getSelectedIndex()).toString();
			// boolean valide =
			// Themes.estValide(cboThemes.getItemAt(cboThemes.getSelectedIndex()).toString());
			Config.set(Config.THEME, nom);

			Themes.setThemeCourant(nom);
			try {
				gui.getBanque().chargerBanque(Themes.getThemeCourant().getSequencePath());
			} catch (SequenceNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Theme theme = Themes.getTheme(nom);
			gui.redessiner();
			redessiner();
		}
	};

	ActionListener alRetournementFin = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selectionne = cbGameFinalAnimation.isSelected();
			Config.set(Config.END_ANIMATION, selectionne);
		}
	};

	ActionListener alDifficulteNoir = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = cboGameBlackPlayer.getSelectedIndex();
			Config.set(Config.DIFFICULTY_BLACK, String.valueOf(index));
			game.updatePlayers();
		}
	};

	ActionListener alDifficulteBlanc = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = cboGameWhitePlayer.getSelectedIndex();
			Config.set(Config.DIFFICULTY_WHITE, String.valueOf(index));
			game.updatePlayers();
		}
	};

	private void redessiner() {
		Color couleurFond = Themes.getThemeCourant().getBackground();
		Color couleurTexte = ColorTools.getLuminosity(Themes.getThemeCourant().getBackground()) < 128 ? Color.WHITE : Color.BLACK;

		setBackground(couleurFond);
		setForeground(couleurTexte);

		for (Component j : getComponents()) {
			j.setForeground(couleurTexte);
			j.setBackground(couleurFond);
			if (j instanceof JCheckBox)
				((JCheckBox) j).setRolloverEnabled(false);
		}
	}
}
