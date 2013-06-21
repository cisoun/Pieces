package pieces.gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.VolatileImage;

import javax.swing.Box;
import javax.swing.JPanel;

import pieces.gui.utils.Themes;

public class Board extends JPanel {
	private static final long serialVersionUID = 1L;

	private Options options;
	private Multiplayer multiplayer;

	public Board(Grid grid, Message message, Options options, Multiplayer multiplayer) {
		this.options = options;
		this.multiplayer = multiplayer;

		setLayout(new BorderLayout());
		add(Box.createVerticalStrut(message.getHauteur()), BorderLayout.NORTH);
		add(grid, BorderLayout.CENTER);
		add(message, BorderLayout.SOUTH);

		setBackground(Themes.getCurrentTheme().getGridBackground());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		if (Themes.getCurrentTheme().hasBackgroundImage()) {
			VolatileImage image = Themes.getCurrentTheme().getGridBackgroundImage();
			int width = image.getWidth();
			int height = image.getHeight();
			for (int x = 0; x < getWidth() / width + 1; x++)
				for (int y = 0; y < getHeight() / height + 1; y++) {
					g2d.drawImage(image, x * width, y * height, width, height, null);
				}
		}
		if (!multiplayer.isToggled()) {
			g2d.setColor(Color.BLACK);
			for (int i = 0; i < 20; i++) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (20 - i) / 200.0f));
				g2d.drawLine(0, i, getWidth(), i);
			}
			g2d.setComposite(AlphaComposite.SrcOver);
		}

		if (!options.isToggled()) {
			g2d.setColor(Color.BLACK);
			for (int i = 0; i < 20; i++) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, i / 100.0f));
				g2d.drawLine(getWidth() - 20 + i, 0, getWidth() - 20 + i, getHeight());
			}
			g2d.setComposite(AlphaComposite.SrcOver);
		}
	}
}