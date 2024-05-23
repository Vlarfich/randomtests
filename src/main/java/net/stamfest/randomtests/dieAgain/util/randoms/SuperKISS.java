package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class SuperKISS extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = 572126915345555554L;
	public static final int Q_MAX = 41790;
	public static final int LCG_MULT = 69069;
	public static final byte LCG_ADD = 123;
	public static final long MWC_MULT = 7010176L;
	private AtomicIntegerArray q;
	private AtomicInteger index;
	private AtomicInteger carry;
	private AtomicInteger linearCongruence;
	private AtomicInteger xorShift;
	private boolean initialized = false;

	public SuperKISS() {
		this(System.nanoTime());
	}

	public SuperKISS(long seed) {
		super(seed);
		q = new AtomicIntegerArray(Q_MAX);
		index = new AtomicInteger();
		carry = new AtomicInteger();
		linearCongruence = new AtomicInteger();
		xorShift = new AtomicInteger();
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			super.setSeed(seed);
			for (int i = 0; i < Q_MAX; i++) {
				q.set(i, super.next(32));
			}
			carry.set(362436);
			linearCongruence.set(1236789);
			xorShift.set(521288629);
			index.set(Q_MAX);
		}
	}

	@Override
	protected int next(int bits) {
		int oldLC, newLC;
		int oldXS, newXS;
		int oldIndex, newIndex;
		int returnQ;
		do {
			oldLC = linearCongruence.get();
			oldXS = xorShift.get();
			oldIndex = index.get();

			newLC = LCG_MULT * oldLC + LCG_ADD;
			newXS = oldXS ^ (oldXS << 13);
			newXS ^= newXS >> 17;
			newXS ^= newXS >> 5;

			if (oldIndex == Q_MAX) {
				returnQ = refill();
				oldIndex = 1;
			} else {
				returnQ = q.get(oldIndex);
			}
			newIndex = oldIndex + 1;

		} while (!(linearCongruence.compareAndSet(oldLC, newLC) && xorShift.compareAndSet(oldXS, newXS)
				&& index.compareAndSet(oldIndex, newIndex)));
		return (returnQ + newLC + newXS) >>> (32 - bits);
	}

	private synchronized int refill() {
		long t;
		int oldQ, newQ;
		int oldCarry, newCarry;
		do {
			for (int i = 0; i < Q_MAX; i++) {
				do {
					oldQ = q.get(i);
					oldCarry = carry.get();

					t = MWC_MULT * oldQ + oldCarry;
					newCarry = (int) (t >> 32);
					newQ = (int) ~t;
				} while (!(q.compareAndSet(i, oldQ, newQ) && carry.compareAndSet(oldCarry, newCarry)));
			}
		} while (!(index.compareAndSet(Q_MAX, 1)));
		return q.get(0);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SuperKISS [q=");
		builder.append(q);
		builder.append(", index=");
		builder.append(index);
		builder.append(", carry=");
		builder.append(carry);
		builder.append(", linearCongruence=");
		builder.append(linearCongruence);
		builder.append(", xorShift=");
		builder.append(xorShift);
		builder.append(", initialized=");
		builder.append(initialized);
		builder.append("]");
		return builder.toString();
	}
}