package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author George Marsaglia (C-Version)
 * @author Christian Schürhoff (Java-Version)
 */
public class KISS32 extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5611147565705032105L;
	private AtomicInteger x;
	private AtomicInteger y;
	private AtomicInteger z;
	private AtomicInteger c;
	private boolean initialized = false;
	private static final int LCG_MULT = 69069;
	private static final short LCG_ADD = 12345;
	private static final long MWC_MULT = 698769069L;

	public KISS32() {
		this(System.nanoTime());
	}

	public KISS32(long seed) {
		super();
		x = new AtomicInteger();
		y = new AtomicInteger();
		z = new AtomicInteger();
		c = new AtomicInteger();
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			x.set((int) (seed ^ (seed >>> 32)));
			y.set(x.get());
			z.set((int) seed);
			c.set((int) (1 + (seed >>> 32)));
		}
	}

	@Override
	protected int next(int bits) {
		int oldX, oldY, oldZ, oldC;
		int newX, newY, newZ, newC;
		long t;
		do {
			newX = oldX = x.get();
			newY = oldY = y.get();
			newZ = oldZ = z.get();
			newC = oldC = c.get();

			newX = LCG_MULT * newX + LCG_ADD;

			newY ^= newY << 13;
			newY ^= newY >> 17;
			newY ^= newY << 5;

			t = MWC_MULT * newZ + newC;
			newC = (int) (t >> 32);
			newZ = (int) t;
		} while (!(x.compareAndSet(oldX, newX) && y.compareAndSet(oldY, newY) && z.compareAndSet(oldZ, newZ)
				&& c.compareAndSet(oldC, newC)));
		return (newX + newY + newZ) >>> (32 - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KISS32 [x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", z=");
		builder.append(z);
		builder.append(", c=");
		builder.append(c);
		builder.append(", initialized=");
		builder.append(initialized);
		builder.append("]");
		return builder.toString();
	}
}