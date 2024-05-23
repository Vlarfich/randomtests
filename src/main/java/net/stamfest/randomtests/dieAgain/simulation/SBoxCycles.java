package net.stamfest.randomtests.dieAgain.simulation;

import net.stamfest.randomtests.dieAgain.util.randoms.KISS32;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class SBoxCycles {
	public byte bits = 8;

	public SBoxCycles() {
		super();
	}

	public static long determineCycle(int[] sbox) {
		if (sbox == null || sbox.length == 0) {
			return 0;
		} else {
			boolean[] visited = new boolean[sbox.length];
			long result = 1;
			for (int i = 0; i < sbox.length; i++) {
				if (!visited[i]) {
					visited[i] = true;
					int current = 1;
					for (int j = sbox[i]; j != i; j = sbox[j]) {
						current++;
						visited[j] = true;
					}
					result *= current / gcd(result, current);
				}
			}
			return result;
		}
	}
	/**
	 * Extracting the cycles of a randomly created s-box follows a harmonic distribution.
	 * @param sbox
	 * @return
	 */
	public static Map<Integer, Integer> extractCycles(int[] sbox) {
		if (sbox == null || sbox.length == 0) {
			return null;
		} else {
			Map<Integer, Integer> ret = new TreeMap<Integer, Integer>();
			boolean[] visited = new boolean[sbox.length];
			for (int i = 0; i < sbox.length; i++) {
				if (!visited[i]) {
					visited[i] = true;
					int current = 1;
					for (int j = sbox[i]; j != i; j = sbox[j]) {
						current++;
						visited[j] = true;
					}
					ret.compute(current, (length, count) -> {
						if (count == null || count == 0) {
							return 1;
						} else {
							return count + 1;
						}
					});
				}
			}
			return ret;
		}
	}

	private static long gcd(long a, long b) {
		if (a == 0) {
			return b;
		} else if (b == 0) {
			return a;
		} else if (a == b) {
			return a;
		}
		if (a < b) {
			a ^= b;
			b ^= a;
			a ^= b;
		}
		// a is now greater than b.
		do {
			a %= b;
			a ^= b;
			b ^= a;
			a ^= b;
		} while (b != 0);
		return a;
	}

	public int[] createIdentitySBox() {
		int[] ret = new int[1 << bits];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = i;
		}
		return ret;
	}

	public static void shuffleSBox(Random rng, int[] sbox) {
		for (int i = 0; i < sbox.length; i++) {
			int j = (i + rng.nextInt(sbox.length)) % sbox.length;
			if (i != j) {
				sbox[i] ^= sbox[j];
				sbox[j] ^= sbox[i];
				sbox[i] ^= sbox[j];
			}
		}
	}

	public static void main(String[] args) {
		SBoxCycles sim = new SBoxCycles();
		sim.bits = 8;
		int[] sbox = sim.createIdentitySBox();
		System.out.println("Numbers: " + sbox.length);
		Random rng = new KISS32();
		TreeMap<Long, Long> cycleSize2Count = new TreeMap<Long, Long>();
		for (long count = 2_000_000L; count > 0; count--) {
			SBoxCycles.shuffleSBox(rng, sbox);
			for (Map.Entry<Integer, Integer> entry : SBoxCycles.extractCycles(sbox).entrySet()) {
				cycleSize2Count.compute(entry.getKey().longValue(), (cycle, amount) -> {
					if (amount == null || amount == 0) {
						return entry.getValue().longValue();
					} else {
						return amount + entry.getValue();
					}
				});
			}
		}
		for (Map.Entry<Long, Long> entry : cycleSize2Count.entrySet()) {
			System.out.print(entry + ",\t");
			if (entry.getKey() % 8 == 0) {
				System.out.println();
			}
		}
	}
}