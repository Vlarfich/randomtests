package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements the middle-square algorithm for pseudo-random numbers using a
 * single 64-bit number.
 * 
 * @author Christian Schürhoff
 */
public class MiddleSquareSolo extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3139031035635606891L;
	private AtomicLong a, usedSeed;
	private boolean initialized = false;

	public MiddleSquareSolo() {
		this(System.nanoTime());
	}

	public MiddleSquareSolo(long seed) {
		super(seed);
		a = new AtomicLong(seed);
		usedSeed = new AtomicLong(seed);
		initialized = true;
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			a.set(seed);
			usedSeed.set(seed);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int next(int bits) {
		long oldA, oldUS;
		long newA, newUS;
		long tempH, tempL;
		do {
			newA = oldA = a.get();
			newUS = oldUS = usedSeed.get();
			if (Long.bitCount(newA) <= 1) {
				newUS = Long.rotateLeft(newUS, 1) + 1;
				newA = newUS;
			}
			tempH = Math.multiplyHigh(newA, newA);
			tempL = newA * newA;
			newA = (tempL >>> 32) ^ (tempH << 32);
			// Usually that would be it, but we have some extra bits, that we can use.
			newA += (tempL << 32) ^ (tempH >>> 32);
		} while (!(a.compareAndSet(oldA, newA) && usedSeed.compareAndSet(oldUS, newUS)));
		return (int) ((newA ^ (newA << 32)) >>> (64 - bits));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MiddleSquareSolo [a=");
		builder.append(a);
		builder.append(", usedSeed=");
		builder.append(usedSeed);
		builder.append("]");
		return builder.toString();
	}
}