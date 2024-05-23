package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Arcfour8PRG extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3960190189758613104L;
	private AtomicIntegerArray s;
	private AtomicInteger i, j;
	private boolean initialized = false;

	public Arcfour8PRG() {
		this(System.nanoTime());
	}

	public Arcfour8PRG(long seed) {
		super(seed);
		s = new AtomicIntegerArray(0x100);
		i = new AtomicInteger(0);
		j = new AtomicInteger(0);
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			for (int ii = 0; ii < 0x100; ii++) {
				s.set(ii, ii);
			}
			for (int ii = 0, ij = 0, tmp; ii < 0x100; ii++) {
				ij = (int) ((ij + s.get(ii) + Long.rotateRight(seed, ii)) & 0xFF);
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
		byte rounds = (byte) Math.ceil(bits / 8.0);
		int ret = 0;
		for (; rounds > 0; rounds--) {
			int oldI, oldJ, newI, newJ, tmp;
			ret <<= 8;
			do {
				oldI = i.get();
				oldJ = j.get();

				newI = (oldI + 1) & 0xFF;
				newJ = (oldJ + s.get(newI)) & 0xFF;
				tmp = s.get(newI);
				s.set(newI, s.get(newJ));
				s.set(newJ, tmp);
				tmp = s.get((s.get(newI) + s.get(newJ)) & 0xFF);
			} while (!(i.compareAndSet(oldI, newI) && j.compareAndSet(oldJ, newJ)));
			ret ^= tmp;
		}
		return ret >> (8 * rounds - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Arcfour8PRG [s=");
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