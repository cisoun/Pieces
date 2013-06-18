package pieces.gui.utils;

/**
 * Class for Ease functions. Some functions taken from
 * http://www.gizma.com/easing/.
 */
public class Ease {
	public static double InOutSine(double iter, double start, double delta, double end) {
		return (-delta / 2 * (Math.cos(Math.PI * iter / end) - 1) + start);
	};
}
