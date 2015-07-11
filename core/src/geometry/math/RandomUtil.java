package geometry.math;

import java.util.Random;

public class RandomUtil {

	public static long SEED = 2;//(long) (Math.random()*10000);

	private static Random r = new Random(SEED);
//	private static Random r = new Random(System.currentTimeMillis());

	public static double between(double min, double max) {
		 return r.nextDouble()*(max-min)+min;
	}

	public static int between(int min, int max) {
		 return (int)(r.nextDouble()*(max-min)+min);
	}
	
	public static double next() {
		return r.nextDouble();
	}

	public static int nextInt(int max) {
		return r.nextInt(max);
	}
	
	public static void changeSeed(long newSeed){
		r = new Random(newSeed);
	}
}
