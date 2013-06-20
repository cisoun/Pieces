package pieces.utils;

/**
 * Player class.
 * Need some clean-up.
 * 
 * @author Cyriaque Skrapits
 * 
 */
public class Player {
	public static final boolean BLACK = false;
	public static final boolean WHITE = true;

	public static final int TYPE_HUMAIN = 0;
	public static final int TYPE_AI1 = 1;
	public static final int TYPE_AI2 = 2;
	public static final int TYPE_AI3 = 3;
	public static final int TYPE_NETWORK = 4;

	private boolean color;
	private int type;
	private int score;

	public Player(boolean color, int type) {
		this.color = color;
		this.type = type;
	}

	public int getScore() {
		return score;
	}

	public int getType() {
		return type;
	}

	public boolean isAI() {
		return (type == TYPE_AI1 || type == TYPE_AI2 || type == TYPE_AI3);
	}

	public void reset() {
		score = 0;
	}

	public void setType(int type) {
		this.type = type;
	}
}
