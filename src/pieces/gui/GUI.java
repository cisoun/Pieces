package pieces.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pieces.Game;
import pieces.gui.menu.Menu;
import pieces.gui.menu.MenuBar;
import pieces.gui.utils.Bank;
import pieces.gui.utils.Themes;
import pieces.gui.utils.Bank.SequenceNotFoundException;
import pieces.utils.Config;
import pieces.utils.Matrix.MatrixPiece;

public class GUI extends JFrame {

	private Game game;
	public Themes theme;
	private Bank bank = null;

	private MenuBar menu;
	private Grid grid;
	private Message message;
	private Scores scores;
	private Options options;
	private Multiplayer multiplayer;
	private Board board;

	private Menu menuNewGame;
	private Menu menuMultiplayer;
	private Menu menuOptions;
	private Menu menuAbout;

	public GUI(Game game) {

		this.game = game;

		// Load config.
		Config.load();

		// Load theme.
		Themes.chargerThemes();
		Themes.setThemeCourant(Config.get(Config.THEME, "Pieces"));
		try {
			bank = new Bank("themes/" + Themes.getThemeCourant().getName() + "/sequence.png", 10);
		} catch (SequenceNotFoundException snfe) {
			String message = "Cannot load current theme : " + Themes.getThemeCourant().getName();
			System.err.println(message);
			JOptionPane.showMessageDialog(this, message, game.APP_NAME, JOptionPane.ERROR_MESSAGE, null);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		setBackground(Themes.getThemeCourant().getGridBackground());

		// Components initialization.
		menu = new MenuBar();
		message = new Message(this);
		grid = new Grid(game, bank);
		scores = new Scores(game, bank);
		options = new Options(game, this);
		multiplayer = new Multiplayer(game);
		board = new Board(grid, message, options, multiplayer);

		// Ugly layout hack.
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(multiplayer, BorderLayout.NORTH);
		panel.add(board, BorderLayout.CENTER);

		createMenus();

		// Display.
		setLayout(new BorderLayout());
		add(menu, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		add(scores, BorderLayout.SOUTH);
		add(options, BorderLayout.EAST);

		// NEEDED FOR WINDOWS ?
		revalidate();
		repaint();

		setTitle(game.APP_NAME);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
	}

	private void createMenus() {
		menuNewGame = new Menu("New game") {
			@Override
			public void action() {
				super.action();
				game.newGame();
			}
		};

		menuMultiplayer = new Menu("Multiplayer") {
			@Override
			public void action() {
				super.action();
				if (game.isMultiplayer())
					game.logout();
				else
					multiplayer.toggle();
				multiplayer();
			}
		};

		menuOptions = new Menu("Options") {
			@Override
			public void action() {
				super.action();
				options.afficher();
			}
		};

		menuAbout = new Menu("?") {
			@Override
			public void action() {
				super.action();
				about();
			}
		};

		menu.addMenu(menuNewGame);
		menu.addMenu(menuMultiplayer);
		menu.addMenu(menuOptions);
		menu.addMenu(menuAbout);
	}

	public void about() {
		StringBuilder message = new StringBuilder();
		message.append("<html>");
		message.append("<h1>" + game.APP_NAME + "</h1>");
		message.append("Version " + game.APP_VERSION + "<br/>");
		message.append("<br/>");
		message.append("<p>Authors : ");
		message.append("<br/>");
		message.append("Guillaume Simon-Gentil (AI)<br/>");
		message.append("Cyriaque Skrapits (core developer)");
		message.append("</p></html>");
		Icon icone = new Icon() {

			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawImage(bank.getImage(0), 0, 0, 60, 60, null);
			}

			@Override
			public int getIconWidth() {
				return 60;
			}

			@Override
			public int getIconHeight() {
				return 60;
			}
		};
		JOptionPane.showMessageDialog(this, message.toString(), "About " + game.APP_NAME + "...", JOptionPane.INFORMATION_MESSAGE, icone);
	}

	public void finalAnimation() {
		if (!Config.get(Config.END_ANIMATION, true))
			return;

		Thread animation = new Thread(new Runnable() {
			int pieces = game.getMatrix().pieces();
			int noirs = game.getMatrix().score(MatrixPiece.BLACK);

			@Override
			public void run() {
				for (int i = 0; i < 64; i++) {
					int piece = game.getMatrix().get(i);
					Piece p = grid.getPiece(i);
					if (i < pieces) {
						if (!p.isVisible())
							p.setVisible(true);
						if (p.isReversed() == noirs > 0)
							p.retourner();
						noirs--;
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						repaint();
						revalidate();
					} else {
						p.setVisible(false);
					}
				}
			}
		});
		animation.start();
	}

	public void changeRound() {
		// Shows which player is allowed to play.
		String m;
		m = "It's time for the " + (game.tour() ? "whites" : "blacks") + " to play.";
		System.out.println(m);
		message.message(m, Message.NORMAL, false, true);
	}

	public Bank getBanque() {
		return this.bank;
	}

	public Grid getGrille() {
		return this.grid;
	}

	public Scores getScores() {
		return this.scores;
	}

	public void message(String texte, Color couleur, boolean persistant, boolean urgent) {
		message.message(texte, couleur, persistant, urgent);
		revalidate();
		repaint();
	}

	public void nouvellePartie() {
		grid.initialiser();
		message.cacher();
		scores.afficher();
	}

	public void play(int piece) {
		grid.poserPiece(piece, game.tour());
	}

	public void redessiner() {
		multiplayer.redraw();
		setBackground(Themes.getThemeCourant().getGridBackground());
		repaint();
	}

	public void multiplayer() {
		menuMultiplayer.setTexte(game.isMultiplayer() ? "Disconnect" : "Multiplayer");
	}
}
