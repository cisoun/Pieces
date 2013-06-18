package reversi.gui.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import reversi.gui.GUI;
import reversi.gui.utils.Couleur;
import reversi.gui.utils.Themes;

public class BarreMenu extends JPanel {
	private boolean flat;
	
	public BarreMenu() {
		super();

		setBackground(Themes.getThemeCourant().getBackground());
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		setPreferredSize(new Dimension(getWidth(), Menu.getHauteur() + 2));
	}
	
	public BarreMenu(boolean flat)
	{
		this();
		this.flat = flat;
	}

	public void addMenu(Menu menu) {
		add(menu);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		Color background = Themes.getThemeCourant().getBackground();
		Color backgroundDark = Couleur.assombrir(background, 20);

		//setPreferredSize(new Dimension(getWidth(), Menu.getHauteur() + 2));

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Fond.
		GradientPaint gp = new GradientPaint(0, 0, flat ? backgroundDark : background, 0, getHeight(), Couleur.assombrir(background, 20));

		g2d.setPaint(gp);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		g2d.setColor(background.darker());
		g2d.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);

		g2d.setColor(background.brighter());
		g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}
}
