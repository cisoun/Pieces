package pieces.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import pieces.Game;
import pieces.gui.menu.Menu;
import pieces.gui.menu.MenuBar;
import pieces.gui.utils.Couleur;
import pieces.gui.utils.Ease;
import pieces.gui.utils.Themes;
import pieces.network.Network;
import pieces.utils.Config;


public class Multiplayer extends MenuBar {
	private Game reversi;

	private JLabel lblIP;
	private JLabel lblPort;
	private JTextField txtIP;
	private JTextField txtPort;
	private JButton btnOK;
	private JButton btnCreerPartie;

	private Menu menuRejoindre;
	private Menu menuCreerPartie;

	private int hauteur;
	private boolean cache = true;

	public Multiplayer(final Game reversi) {
		super(true);

		this.reversi = reversi;

		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		lblIP = new JLabel("Adresse IP de l'adversaire : ");
		lblPort = new JLabel("Port : ");
		txtIP = new JTextField(Config.get(Config.SERVER, "127.0.0.1"));
		txtIP.setColumns(10);
		txtPort = new JTextField(String.valueOf(Network.DEFAULT_PORT));
		txtPort.setColumns(4);

		menuRejoindre = new Menu("Rejoindre") {
			@Override
			public void action() {
				super.action();
				String serveur = txtIP.getText();
				int port = Integer.valueOf(txtPort.getText());
				Config.set(Config.SERVER, serveur);
				Config.set(Config.PORT, port);
				reversi.rejoindre();
				afficher();
			}
		};
		menuCreerPartie = new Menu("Créer une partie") {
			@Override
			public void action() {
				super.action();
				int port = Integer.valueOf(txtPort.getText());
				Config.set(Config.PORT, port);
				reversi.multiplayer();
				afficher();
			}
		};

		add(lblIP);
		add(txtIP);
		add(Box.createHorizontalStrut(10));
		add(lblPort);
		add(txtPort);
		add(Box.createHorizontalStrut(10));
		add(menuRejoindre);
		add(Box.createHorizontalStrut(10));
		add(menuCreerPartie);

		hauteur = getPreferredSize().height;
		setPreferredSize(new Dimension(getWidth(), 0));

		redessiner();
	}

	public void afficher() {
		Thread animation = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean c = cache;
				for (int i = 0; i <= 10; i++) {
					int y = (int) (Math.abs((c ? 1.0 : 0.0) - (Ease.InOutSine(i, 0, 1, 10))) * hauteur);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setPreferredSize(new Dimension(getWidth(), y));
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

	public void redessiner() {
		Color couleurFond = Themes.getThemeCourant().getBackground();
		Color couleurTexte = Couleur.luminosite(Themes.getThemeCourant().getBackground()) < 128 ? Color.WHITE : Color.BLACK;

		setBackground(couleurFond);
		setForeground(couleurTexte);

		for (Component j : getComponents()) {
			// j.setForeground(couleurTexte);
			// j.setBackground(couleurFond);
			if (j instanceof JLabel)
				((JLabel) j).setForeground(couleurTexte);
		}
	}
}
