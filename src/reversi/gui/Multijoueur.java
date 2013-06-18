package reversi.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import reversi.Reversi;
import reversi.gui.menu.BarreMenu;
import reversi.gui.menu.Menu;
import reversi.gui.utils.Couleur;
import reversi.gui.utils.Ease;
import reversi.gui.utils.Themes;
import reversi.reseau.Reseau;
import reversi.utils.Config;
import reversi.utils.Utils;

public class Multijoueur extends BarreMenu {
	private Reversi reversi;

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

	public Multijoueur(final Reversi reversi) {
		super(true);

		this.reversi = reversi;

		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		lblIP = new JLabel("Adresse IP de l'adversaire : ");
		lblPort = new JLabel("Port : ");
		txtIP = new JTextField(Config.get(Config.SERVEUR, "127.0.0.1"));
		txtIP.setColumns(10);
		txtPort = new JTextField(String.valueOf(Reseau.PORT_DEFAUT));
		txtPort.setColumns(4);

		menuRejoindre = new Menu("Rejoindre") {
			@Override
			public void action() {
				super.action();
				String serveur = txtIP.getText();
				int port = Integer.valueOf(txtPort.getText());
				Config.set(Config.SERVEUR, serveur);
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
				reversi.multijoueur();
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
