package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Arcfour16APlusPRG extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -522360271923578108L;
	private AtomicIntegerArray s1, s2;
	private AtomicInteger i, j1, j2, k1, k2;
	private boolean initialized = false;
	public static final int W = 3;
	public static final int W1 = 255;
	public static final int W2 = 257;
	public static final int B1 = 1;
	public static final int B2 = 1;

	public Arcfour16APlusPRG() {
		this(System.nanoTime());
	}

	public Arcfour16APlusPRG(long seed) {
		super(seed);
		s1 = new AtomicIntegerArray(0x10000);
		s2 = new AtomicIntegerArray(0x10000);
		i = new AtomicInteger(0);
		j1 = new AtomicInteger(0);
		j2 = new AtomicInteger(0);
		k1 = new AtomicInteger(0);
		k2 = new AtomicInteger(0);
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			for (int ii = 0; ii < 0x10000; ii++) {
				s1.set(ii, ii);
				s2.set(ii, ii);
			}
			for (int ii = 0, ij1 = 0, ij2 = 0, tmp; ii < 0x10000; ii++) {
				ij1 = (int) ((ij1 + s1.get(ij1) + Long.rotateLeft(seed, ii)) & 0xFFFF);
				ij2 = (int) ((ij2 + s1.get(ij2) + Long.rotateRight(seed, ii)) & 0xFFFF);
				tmp = s1.get(ij1);
				s1.set(ij1, s1.get(ii));
				s1.set(ii, tmp);
				tmp = s2.get(ij2);
				s2.set(ij2, s2.get(ii));
				s2.set(ii, tmp);
			}
			for (int ii = 0, ij1 = 0, ij2 = 0, tmp; ii < 0x40000; ii++) {
				ij1 = (int) ((ij1 + s1.get(ij1) + W1) & 0xFFFF);
				ij2 = (int) ((ij2 + s1.get(ij2) + W2) & 0xFFFF);
				tmp = s1.get(ij1);
				s1.set(ij1, s1.get(ii & 0xFFFF));
				s1.set(ii & 0xFFFF, tmp);
				tmp = s2.get(ij2);
				s2.set(ij2, s2.get(ii & 0xFFFF));
				s2.set(ii & 0xFFFF, tmp);
			}
			i.set(0);
			j1.set(0);
			j2.set(0);
			k1.set(0);
			k2.set(0);
		}
	}

	@Override
	protected int next(int bits) {
		int c1, c2, a1, a2, output, tmp;
		int oldI, oldJ1, oldJ2, oldK1, oldK2;
		int newI, newJ1, newJ2, newK1, newK2;
		do {
			newI = oldI = i.get();
			newJ1 = oldJ1 = j1.get();
			newJ2 = oldJ2 = j2.get();
			newK1 = oldK1 = k1.get();
			newK2 = oldK2 = k2.get();

			newI = (newI + W) & 0xFFFF;
			a1 = s1.get(newI);
			a2 = s2.get(newI);
			newJ1 = (newK1 + a1 + s1.get((newJ1 + s2.get(newI)) & 0xFFFF)) & 0xFFFF;
			newK1 = (newK1 + newI + s1.get(newJ1)) & 0xFFFF;
			newJ2 = (newK2 + a2 + s2.get((newJ2 + s1.get(newI)) & 0xFFFF)) & 0xFFFF;
			newK2 = (newK2 + newI + s2.get(newJ2)) & 0xFFFF;

			c1 = (s1.get(((newI << 9) ^ (newJ1 >> 7)) & 0xFFFF) + s1.get(((newJ1 << 9) ^ (newI >> 7)) & 0xFFFF))
					& 0xFFFF;
			c2 = (s2.get(((newI << 9) ^ (newJ2 >> 7)) & 0xFFFF) + s2.get(((newJ2 << 9) ^ (newI >> 7)) & 0xFFFF))
					& 0xFFFF;

			output = s1.get((newJ1 + s2.get((newI + (s1.get((a1 + B1) & 0xFFFF) + s1.get(c1 ^ 0xAAAA))
					^ (s1.get((newJ1 + B1) & 0xFFFF) + newK1)) & 0xFFFF)) & 0xFFFF);
			output <<= 16;
			output ^= s2.get((newJ2 + s1.get((newI + (s2.get((a2 + B2) & 0xFFFF) + s2.get(c2 ^ 0x5555))
					^ (s2.get((newJ2 + B2) & 0xFFFF) + newK2)) & 0xFFFF)) & 0xFFFF);

			synchronized (s1) {
				tmp = s1.get(newJ1);
				s1.set(newJ1, s1.get(newI));
				s1.set(newI, tmp);
			}
			synchronized (s2) {
				tmp = s2.get(newJ2);
				s2.set(newJ2, s2.get(newI));
				s2.set(newI, tmp);
			}
		} while (!(i.compareAndSet(oldI, newI) && j1.compareAndSet(oldJ1, newJ1) && j2.compareAndSet(oldJ2, newJ2)
				&& k1.compareAndSet(oldK1, newK1) && k2.compareAndSet(oldK2, newK2)));
		return output >> (32 - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Arcfour16APlusPRG [s1=");
		builder.append(s1);
		builder.append(", s2=");
		builder.append(s2);
		builder.append(", i=");
		builder.append(i);
		builder.append(", j1=");
		builder.append(j1);
		builder.append(", j2=");
		builder.append(j2);
		builder.append(", k1=");
		builder.append(k1);
		builder.append(", k2=");
		builder.append(k2);
		builder.append(", initialized=");
		builder.append(initialized);
		builder.append("]");
		return builder.toString();
	}
}