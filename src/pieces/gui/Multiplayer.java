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
import pieces.gui.utils.ColorTools;
import pieces.gui.utils.Ease;
import pieces.gui.utils.Themes;
import pieces.network.Network;
import pieces.utils.Config;



public class Multiplayer extends MenuBar {
	private Game game;

	private JLabel lblIP;
	private JLabel lblPort;
	private JTextField txtIP;
	private JTextField txtPort;
	private JButton btnOK;
	private JButton btnCreerPartie;

	private Menu menuJoin;
	private Menu menuCreateGame;

	private int hauteur;
	private boolean hidden = true;

	public Multiplayer(final Game game) {
		super(true);

		this.game = game;

		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		lblIP = new JLabel("Opponent's IP : ");
		lblPort = new JLabel("Port : ");
		txtIP = new JTextField(Config.get(Config.SERVER, "127.0.0.1"));
		txtIP.setColumns(10);
		txtPort = new JTextField(String.valueOf(Network.DEFAULT_PORT));
		txtPort.setColumns(4);

		menuJoin = new Menu("Join a game") {
			@Override
			public void action() {
				super.action();
				String serveur = txtIP.getText();
				int port = Integer.valueOf(txtPort.getText());
				Config.set(Config.SERVER, serveur);
				Config.set(Config.PORT, port);
				game.login();
				toggle();
			}
		};
		menuCreateGame = new Menu("Create a game") {
			@Override
			public void action() {
				super.action();
				int port = Integer.valueOf(txtPort.getText());
				Config.set(Config.PORT, port);
				game.multiplayer();
				toggle();
			}
		};

		add(lblIP);
		add(txtIP);
		add(Box.createHorizontalStrut(10));
		add(lblPort);
		add(txtPort);
		add(Box.createHorizontalStrut(10));
		add(menuJoin);
		add(Box.createHorizontalStrut(10));
		add(menuCreateGame);

		hauteur = getPreferredSize().height;
		setPreferredSize(new Dimension(getWidth(), 0));

		redraw();
	}

	public void toggle() {
		Thread animation = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean c = hidden;
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

		hidden = !hidden;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void redraw() {
		Color backgroundColor = Themes.getThemeCourant().getBackground();
		Color textColor = ColorTools.getLuminosity(Themes.getThemeCourant().getBackground()) < 128 ? Color.WHITE : Color.BLACK;

		setBackground(backgroundColor);
		setForeground(textColor);

		for (Component j : getComponents()) {
			if (j instanceof JLabel)
				((JLabel) j).setForeground(textColor);
		}
	}
}
