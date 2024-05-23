package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Jenkins extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 507944173644930234L;
	private AtomicInteger a;
	private char next = 0;
	private boolean initalized = false;
	public final static int OFFSET_A = 0xBB48E941;

	public Jenkins() {
		this(System.nanoTime());
	}

	public Jenkins(long seed) {
		super(seed);
		a = new AtomicInteger();
		initalized = true;
		setSeed(seed);
	}

	/**
	 * The lower 16 bits are used to set the next character for the jenkins
	 * one-at-a-time hash function, the middle 32 bits are used for setting the
	 * initial value of the hash function. The uppermost 16 bits are ignored.
	 */
	@Override
	public synchronized void setSeed(long seed) {
		if (initalized) {
			next = (char) seed;
			a.set((int) ((seed >>> 16) + OFFSET_A));
		}
	}

	@Override
	protected int next(int bits) {
		int oldA, newA, b;
		do {
			oldA = a.get();
			newA = oldA + next;
			newA += newA << 10;
			newA ^= newA >> 6;
			b = newA + (newA << 3);
			b ^= b >> 11;
			b += b << 15;
		} while (!a.compareAndSet(oldA, newA));
		return b >> (32 - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Jenkins [a=");
		builder.append(a);
		builder.append(", next=");
		builder.append(next);
		builder.append(", initalized=");
		builder.append(initalized);
		builder.append("]");
		return builder.toString();
	}
	
}