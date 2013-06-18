package reversi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyleConstants.FontConstants;

import reversi.Reversi;
import reversi.gui.utils.BanquePieces.SequenceIntrouvableException;
import reversi.gui.utils.Couleur;
import reversi.gui.utils.Ease;
import reversi.gui.utils.Themes;
import reversi.gui.utils.Themes.Theme;
import reversi.utils.Config;

public class Options extends JPanel {

	private final Font POLICE = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
	private final Font POLICE_TITRE = new Font(Font.SANS_SERIF, Font.BOLD, 12);
	private final String TITRE = "OPTIONS";
	private final int MARGE_TITRE = 1;

	private Reversi reversi;
	private GUI gui;
	private JLabel lblTitreApparence;
	private JLabel lblTitreJeu;
	private JLabel lblApparenceTheme;
	private JLabel lblJeuJoueurNoir;
	private JLabel lblJeuJoueurBlanc;
	private JCheckBox cbJeuRetournementFin;
	private JCheckBox cbApparenceAfficherGrille;
	private JCheckBox cbApparenceCasesPossibles;
	private JComboBox<String> cboApparenceThemes;
	private JComboBox<String> cboJeuJoueurNoir;
	private JComboBox<String> cboJeuJoueurBlanc;

	private int titreLargeur;
	private int titreHauteur;

	private String[] difficultes = new String[] { "Humain", "IA Facile", "IA Moyen", "IA Difficile" };

	private boolean cache = true;

	public Options(Reversi reversi, GUI gui) {

		FontMetrics fm = this.getFontMetrics(POLICE_TITRE);
		titreHauteur = fm.stringWidth(TITRE);
		titreLargeur = fm.getHeight();

		this.reversi = reversi;
		this.gui = gui;

		// Titres
		lblTitreApparence = new JLabel("Apparence");
		lblTitreApparence.setFont(POLICE_TITRE);

		lblTitreJeu = new JLabel("Jeu");
		lblTitreJeu.setFont(POLICE_TITRE);

		// Options
		cbApparenceAfficherGrille = new JCheckBox("Afficher la grille");
		//cbApparenceAfficherGrille.setFont(POLICE);
		cbApparenceAfficherGrille.setOpaque(false);
		cbApparenceAfficherGrille.setSelected(Config.get(Config.AFFICHER_GRILLE, true));
		cbApparenceAfficherGrille.addActionListener(alAfficherGrille);

		cbApparenceCasesPossibles = new JCheckBox("Afficher les cases possibles");
		//cbApparenceCasesPossibles.setFont(POLICE);
		cbApparenceCasesPossibles.setOpaque(false);
		cbApparenceCasesPossibles.setSelected(Config.get(Config.AFFICHER_COUPS_POSSIBLES, false));
		cbApparenceCasesPossibles.addActionListener(alCoupsPossibles);

		lblApparenceTheme = new JLabel("Thème du jeu : ");
		//lblApparenceTheme.setFont(POLICE);

		cboApparenceThemes = new JComboBox<String>(Themes.getNomThemes());
		cboApparenceThemes.setOpaque(false);
		cboApparenceThemes.setSelectedIndex(Themes.getThemeIndex(Config.get(Config.THEME, "Default")));
		cboApparenceThemes.addActionListener(alThemes);

		cbJeuRetournementFin = new JCheckBox("Retourner les pièces à la fin de la partie");
		cbJeuRetournementFin.setOpaque(false);
		cbJeuRetournementFin.setSelected(Config.get(Config.RETOURNEMENT_FIN, false));
		cbJeuRetournementFin.addActionListener(alRetournementFin);

		lblJeuJoueurNoir = new JLabel("Joueur noir : ");

		cboJeuJoueurNoir = new JComboBox<String>((String[]) difficultes);
		cboJeuJoueurNoir.setOpaque(false);
		cboJeuJoueurNoir.setSelectedIndex(Integer.valueOf(Config.get(Config.DIFFICULTE_NOIR, "0")));
		cboJeuJoueurNoir.addActionListener(alDifficulteNoir);

		lblJeuJoueurBlanc = new JLabel("Joueur blanc : ");

		cboJeuJoueurBlanc = new JComboBox<String>((String[]) difficultes);
		cboJeuJoueurBlanc.setOpaque(false);
		cboJeuJoueurBlanc.setSelectedIndex(Integer.valueOf(Config.get(Config.DIFFICULTE_BLANC, "0")));
		cboJeuJoueurBlanc.addActionListener(alDifficulteBlanc);

		// Placements
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		int y = 0;

		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Apparence
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(lblTitreApparence, gbc);

		gbc.gridy = y++;
		add(Box.createVerticalStrut(30));

		gbc.gridy = y++;
		add(cbApparenceAfficherGrille, gbc);

		gbc.gridy = y++;
		add(cbApparenceCasesPossibles, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = y;
		add(lblApparenceTheme, gbc);

		gbc.gridx = 1;
		gbc.gridy = y++;
		add(cboApparenceThemes, gbc);

		// Jeu
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(Box.createVerticalStrut(30), gbc);

		gbc.gridy = y++;
		add(lblTitreJeu, gbc);

		gbc.gridy = y++;
		add(Box.createVerticalStrut(10), gbc);

		gbc.gridy = y++;
		add(cbJeuRetournementFin, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(lblJeuJoueurNoir, gbc);

		gbc.gridx = 1;
		add(cboJeuJoueurNoir, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = y++;
		add(lblJeuJoueurBlanc, gbc);

		gbc.gridx = 1;
		add(cboJeuJoueurBlanc, gbc);

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
		g2d.setColor(Couleur.assombrir(gui.getBackground(), 20));
		g2d.fillRect(MARGE_TITRE, 0, titreLargeur + 20, getHeight());

		g2d.setColor(new Color(50, 50, 50));
		// g2d.fillRect(0, 20, titreLargeur + 20, titreHauteur + 20);
		g2d.fillRect(MARGE_TITRE, 0, titreLargeur + 20, getHeight());

		g2d.translate(MARGE_TITRE + titreLargeur + 7, titreHauteur + 25);
		g2d.rotate(Math.toRadians(-90));

		g2d.setFont(POLICE_TITRE);
		g2d.setColor(Color.WHITE);
		g2d.drawString(TITRE, 0, 0);

		g2d.rotate(Math.toRadians(90));
		g2d.translate(-MARGE_TITRE - titreLargeur - 7, -titreHauteur - 25);

		// Bordure.
		// g2d.setColor(Themes.getThemeCourant().getBackground());
		// g2d.drawLine(MARGE_TITRE, 0, MARGE_TITRE, getHeight());
	}

	ActionListener alAfficherGrille = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selectionne = cbApparenceAfficherGrille.isSelected();
			Config.set(Config.AFFICHER_GRILLE, selectionne);
			cbApparenceAfficherGrille.setSelected(selectionne);
			gui.getGrille().repaint();
		}
	};

	ActionListener alCoupsPossibles = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			boolean selectionne = cbApparenceCasesPossibles.isSelected();
			Config.set(Config.AFFICHER_COUPS_POSSIBLES, selectionne);
			cbApparenceCasesPossibles.setSelected(selectionne);
			cbApparenceCasesPossibles.repaint();
			gui.getGrille().repaint();
		}
	};

	ActionListener alThemes = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String nom = cboApparenceThemes.getItemAt(cboApparenceThemes.getSelectedIndex()).toString();
			// boolean valide =
			// Themes.estValide(cboThemes.getItemAt(cboThemes.getSelectedIndex()).toString());
			Config.set(Config.THEME, nom);

			Themes.setThemeCourant(nom);
			try {
				gui.getBanque().chargerBanque(Themes.getThemeCourant().getSequencePath());
			} catch (SequenceIntrouvableException | IOException e) {
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
			boolean selectionne = cbJeuRetournementFin.isSelected();
			Config.set(Config.RETOURNEMENT_FIN, selectionne);
		}
	};

	ActionListener alDifficulteNoir = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = cboJeuJoueurNoir.getSelectedIndex();
			Config.set(Config.DIFFICULTE_NOIR, String.valueOf(index));
			reversi.updateJoueurs();
		}
	};

	ActionListener alDifficulteBlanc = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = cboJeuJoueurBlanc.getSelectedIndex();
			Config.set(Config.DIFFICULTE_BLANC, String.valueOf(index));
			reversi.updateJoueurs();
		}
	};

	private void redessiner() {
		Color couleurFond = Themes.getThemeCourant().getBackground();
		Color couleurTexte = Couleur.luminosite(Themes.getThemeCourant().getBackground()) < 128 ? Color.WHITE : Color.BLACK;

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
