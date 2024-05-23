package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author George Marsaglia (C-Version)
 * @author Christian Schürhoff (Java-Version)
 */
public class KISS64 extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6132274730913057146L;
	private AtomicLong x;
	private AtomicLong y;
	private AtomicLong z;
	private AtomicLong c;
	private boolean initialized = false;
	private static final long LCG_MULT = 6906969069L;
	private static final int LCG_ADD = 1234567;

	public KISS64() {
		this(System.nanoTime());
	}

	public KISS64(long seed) {
		super();
		x = new AtomicLong();
		y = new AtomicLong();
		z = new AtomicLong();
		c = new AtomicLong();
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			x.set(seed);
			y.set(x.get());
			z.set(seed);
			c.set(1);
		}
	}

	@Override
	protected int next(int bits) {
		long oldX, oldY, oldZ, oldC;
		long newX, newY, newZ, newC;
		long t;
		do {
			newX = oldX = x.get();
			newY = oldY = y.get();
			newZ = oldZ = z.get();
			newC = oldC = c.get();

			newX = LCG_MULT * newX + LCG_ADD;

			newY ^= newY << 13;
			newY ^= newY >> 17;
			newY ^= newY << 43;

			t = (newZ << 58) + newC;
			newC = newZ >> 6;
			newZ += t;
			newC += newZ < t ? 1 : 0;
		} while (!(x.compareAndSet(oldX, newX) && y.compareAndSet(oldY, newY) && z.compareAndSet(oldZ, newZ)
				&& c.compareAndSet(oldC, newC)));
		return (int) ((newX + newY + newZ) >>> (64 - bits));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KISS64 [x=");
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