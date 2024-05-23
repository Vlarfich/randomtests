package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Christian Schürhoff
 */
public class MiddleSquareDuo extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 329566499844353412L;
	private AtomicLong usedSeed;
	private AtomicInteger a, b;
	private boolean initialized = false;

	public MiddleSquareDuo() {
		this(System.nanoTime());
	}

	public MiddleSquareDuo(long seed) {
		super(seed);
		usedSeed = new AtomicLong(seed);
		a = new AtomicInteger((int) (seed >>> 32));
		b = new AtomicInteger((int) seed);
		initialized = true;
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			usedSeed.set(seed);
			a.set((int) (seed >>> 32));
			b.set((int) seed);
		}
	}

	@Override
	protected int next(int bits) {
		long oldUS, newUS;
		long newA, newB;
		int oldA, oldB;
		do {
			newUS = oldUS = usedSeed.get();
			newA = oldA = a.get();
			newB = oldB = b.get();
			if (newA == 0) {
				if (newB == 0) {
					newUS = Long.rotateLeft(newUS, 1) + 1;
					newA = newUS;
					newUS = Long.rotateLeft(newUS, 1) + 1;
					newB = newUS;
				} else {
					newA = ~newB;
				}
			} else {
				if (newB == 0) {
					newB = ~newA;
				}
			}
			newA = Integer.toUnsignedLong((int) newA);
			newB = Integer.toUnsignedLong((int) newB);
			newA *= newA;
			newB *= newB;
			newA = construct(newA);
			newB = construct(newB);
		} while (!(usedSeed.compareAndSet(oldUS, newUS) && a.compareAndSet(oldA, (int) newA)
				&& b.compareAndSet(oldB, (int) newB)));
		return (int) ((newA ^ newB) >>> (32 - bits));
	}

	private long construct(long input) {
		// 0: highest 16, 3: lowest 16.
		int[] unsignedShorts = new int[4];
		for (int i = 0; i < 4; i++) {
			unsignedShorts[i] = (int) ((input >>> (48 - 16 * i)) & 0xFFFF);
		}
		int out = unsignedShorts[2] ^ (unsignedShorts[1] << 16);
		// Usually that would the final value, but we have some extra bits, that we can
		// use.
		out += unsignedShorts[0] ^ (unsignedShorts[3] << 16);
		return Integer.toUnsignedLong(out);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MiddleSquareDuo [usedSeed=");
		builder.append(usedSeed);
		builder.append(", a=");
		builder.append(a);
		builder.append(", b=");
		builder.append(b);
		builder.append("]");
		return builder.toString();
	}
}