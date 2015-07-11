package geometry.math;

public class PrecisionUtil {
	public static final double APPROX = 0.000001;

	public static boolean areEquals(double p, double q) {
		return p > q - APPROX && p < q + APPROX;
	} 
}
