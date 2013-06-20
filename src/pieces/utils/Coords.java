package pieces.utils;

public class Coords {

	private int x;
	private int y;

	public Coords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(int x, int y) {
		return x == this.x && y == this.y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return toString(x, y);
	}

	public static int toIndex(int x, int y, int rangee)
	{
		return (y) * rangee + x;
	}
	
	public static String toString(int x, int y) {
		return "[x=" + (x + 1) + ", y=" + (y + 1) + "]";

	}
}
