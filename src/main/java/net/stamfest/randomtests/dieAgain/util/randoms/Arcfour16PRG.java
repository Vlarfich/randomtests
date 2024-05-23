package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Arcfour16PRG extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5485178410017833467L;
	private AtomicIntegerArray s;
	private AtomicInteger i, j;
	private boolean initialized = false;

	public Arcfour16PRG() {
		this(System.nanoTime());
	}

	public Arcfour16PRG(long seed) {
		super(seed);
		s = new AtomicIntegerArray(0x10000);
		i = new AtomicInteger(0);
		j = new AtomicInteger(0);
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			for (int ii = 0; ii < 0x10000; ii++) {
				s.set(ii, ii);
			}
			for (int ii = 0, ij = 0, tmp; ii < 0x10000; ii++) {
				ij = (int) ((ij + s.get(ii) + Long.rotateRight(seed, ii)) & 0xFFFF);
				tmp = s.get(ij);
				s.set(ii, s.get(ij));
				s.set(ij, tmp);
			}
			i.set(0);
			j.set(0);
		}
	}

	@Override
	protected int next(int bits) {
		byte rounds = (byte) Math.ceil(bits / 16.0);
		int ret = 0;
		for (; rounds > 0; rounds--) {
			int oldI, oldJ, newI, newJ, tmp;
			ret <<= 16;
			do {
				oldI = i.get();
				oldJ = j.get();

				newI = (oldI + 1) & 0xFFFF;
				newJ = (oldJ + s.get(newI)) & 0xFFFF;
				tmp = s.get(newI);
				s.set(newI, s.get(newJ));
				s.set(newJ, tmp);
				tmp = s.get((s.get(newI) + s.get(newJ)) & 0xFFFF);
			} while (!(i.compareAndSet(oldI, newI) && j.compareAndSet(oldJ, newJ)));
			ret ^= tmp;
		}
		return ret >> (16 * rounds - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Arcfour16PRG [s=");
		builder.append(s);
		builder.append(", i=");
		builder.append(i);
		builder.append(", j=");
		builder.append(j);
		builder.append(", initialized=");
		builder.append(initialized);
		builder.append("]");
		return builder.toString();
	}
}