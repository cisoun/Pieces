package pieces.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedList;
import javax.swing.JPanel;

import pieces.gui.utils.ColorTools;
import pieces.gui.utils.Ease;

/**
 * Message bar.
 * We can stack new messages (MessageStruct) and show them one by one.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class Message extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final Color WARNING = new Color(255, 214, 51);
	public static final Color ERROR = new Color(255, 92, 51);
	public static final Color OK = new Color(153, 204, 51);
	public static final Color NORMAL = new Color(51, 173, 255);

	private int height;
	private int margin = 0;
	private LinkedList<MessageStruct> list;
	private Thread animation;

	private class MessageStruct {
		public String text;
		public Color color;
		public boolean persistant;
	}

	public Message() {
		list = new LinkedList<MessageStruct>();

		FontMetrics fm = getFontMetrics(getFont()); // this.getFontMetrics(new
													// Font(Font.SANS_SERIF,
													// Font.BOLD, TAILLE));
		height = fm.getHeight() + 10;
		margin = fm.getHeight() + 10;
		setPreferredSize(new Dimension(getWidth(), height));
		setDoubleBuffered(true);
		setOpaque(false);
	}

	private synchronized void animation() {
		final MessageStruct m = list.peek();
		animation = new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				try {
					for (int i = 1; i <= 10; i++) {
						margin = (int) (height * (1.0 - Ease.InOutSine(i, 0, 1, 10)));
						repaint();
						Thread.sleep(20);
					}
					if (m.persistant)
						return;
					Thread.sleep(2000);
					for (int i = 1; i <= 10; i++) {
						margin = (int) (height * Ease.InOutSine(i, 0, 1, 10));
						repaint();
						Thread.sleep(20);
					}
					nextMessage();
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		});
		animation.start();
	}

	public int getHauteur() {
		return height;
	}

	public void message(String text, Color color, boolean persistant, boolean urgent) {
		MessageStruct m = new MessageStruct();
		m.text = text;
		m.color = color;
		m.persistant = persistant;

		// If message is urgent, removes the others immediately and shows it.
		if (urgent) {
			stopAnimation();
			list.clear();
			list.addFirst(m);
		} else {
			list.add(m);
		}

		// If one message remains, shows it.
		if (list.size() == 1)
			animation();
	}

	private void nextMessage() {
		stopAnimation();
		list.poll();
		if (list.size() > 0)
			animation();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		if (list.size() == 0)
			return;
		MessageStruct m = list.peek();

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Background
		GradientPaint gp = new GradientPaint(0, 0, m.color, 0, getHeight(), ColorTools.darken(m.color, 20));
		g2d.setPaint(gp);
		g2d.fillRect(0, margin, getWidth(), getHeight());

		// Border
		g2d.setColor(m.color.darker());
		g2d.drawLine(0, margin, getWidth(), margin);

		g2d.setColor(m.color.brighter());
		g2d.drawLine(0, margin + 1, getWidth(), margin + 1);

		// Text
		FontMetrics fm = getFontMetrics(getFont());
		int x = getWidth() / 2 - fm.stringWidth(m.text) / 2;

		g2d.setColor(ColorTools.brighten(m.color, 40));
		g2d.drawString(m.text, x, margin + getHeight() - 6);

		g2d.setColor(ColorTools.darken(m.color, 150));
		g2d.drawString(m.text, x, margin + getHeight() - 7);

		g2d.dispose();
	}

	private void stopAnimation() {
		if (animation != null && animation.isAlive()) {
			animation.interrupt();
		}
	}

	public void toggle() {
		stopAnimation();
		margin = height;
		repaint();
	}
}
