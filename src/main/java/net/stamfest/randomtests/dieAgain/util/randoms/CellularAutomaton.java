package net.stamfest.randomtests.dieAgain.util.randoms;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class CellularAutomaton extends Random {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6570347899181855192L;
	/**
	 * Number of cells in the automaton.
	 */
	public static final int CA_WIDTH = 2056;
	public static final int RULESIZE = 256;
	public static final int INITIAL_DUMP = CA_WIDTH * CA_WIDTH / 4;
	private static final int[] RULE = { 100, 75, 16, 3, 229, 51, 197, 118, 24, 62, 198, 11, 141, 152, 241, 188, 2, 17,
			71, 47, 179, 177, 126, 231, 202, 243, 59, 25, 77, 196, 30, 134, 199, 163, 34, 216, 21, 84, 37, 182, 224,
			186, 64, 79, 225, 45, 143, 20, 48, 147, 209, 221, 125, 29, 99, 12, 46, 190, 102, 220, 80, 215, 242, 105, 15,
			53, 0, 67, 68, 69, 70, 89, 109, 195, 170, 78, 210, 131, 42, 110, 181, 145, 40, 114, 254, 85, 107, 87, 72,
			192, 90, 201, 162, 122, 86, 252, 94, 129, 98, 132, 193, 249, 156, 172, 219, 230, 153, 54, 180, 151, 83, 214,
			123, 88, 164, 167, 116, 117, 7, 27, 23, 213, 235, 5, 65, 124, 60, 127, 236, 149, 44, 28, 58, 121, 191, 13,
			250, 10, 232, 112, 101, 217, 183, 239, 8, 32, 228, 174, 49, 113, 247, 158, 106, 218, 154, 66, 226, 157, 50,
			26, 253, 93, 205, 41, 133, 165, 61, 161, 187, 169, 6, 171, 81, 248, 56, 175, 246, 36, 178, 52, 57, 212, 39,
			176, 184, 185, 245, 63, 35, 189, 206, 76, 104, 233, 194, 19, 43, 159, 108, 55, 200, 155, 14, 74, 244, 255,
			222, 207, 208, 137, 128, 135, 96, 144, 18, 95, 234, 139, 173, 92, 1, 203, 115, 223, 130, 97, 91, 227, 146,
			4, 31, 120, 211, 38, 22, 138, 140, 237, 238, 251, 240, 160, 142, 119, 73, 103, 166, 33, 148, 9, 111, 136,
			168, 150, 82, 204 };
	private boolean initialized = false;
	private AtomicIntegerArray config;
	private AtomicInteger currentCell;

	public CellularAutomaton() {
		this(System.nanoTime());
	}

	public CellularAutomaton(long seed) {
		super(seed);
		config = new AtomicIntegerArray(CA_WIDTH);
		currentCell = new AtomicInteger();
		initialized = true;
		setSeed(seed);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (initialized) {
			for (int i = 0; i < CA_WIDTH; i++) {
				config.set(i, 0);
			}
			config.set(CA_WIDTH - 1, (int) (seed & 0xFF));
			config.set(CA_WIDTH - 2, (int) ((seed >> 8) & 0xFF));
			config.set(CA_WIDTH - 3, (int) ((seed >> 16) & 0xFF));
			config.set(CA_WIDTH - 4, (int) ((seed >> 24) & 0xFF));
			config.set(CA_WIDTH - 5, (int) ((seed >> 32) & 0xFF));
			config.set(CA_WIDTH - 6, (int) ((seed >> 40) & 0xFF));
			config.set(CA_WIDTH - 7, (int) ((seed >> 48) & 0xFF));
			config.set(CA_WIDTH - 8, (int) ((seed >> 56) & 0xFF));
			if (seed != -1) {
				seed++;
			}
			for (int i = 0; i < CA_WIDTH - 8; i++) {
				config.set(i, (int) (Long.rotateLeft(seed, i % 64) & 0xFF));
			}
			currentCell.set(CA_WIDTH - 1);
			for (int i = 0; i < INITIAL_DUMP; i++) {
				next(0);
			}
		}
	}

	@Override
	protected int next(int bits) {
		int oldCellD, newCellD;
		int cellC, cellB, cellA;
		int oldConfigCD, newConfigCD;
		int oldConfigCC, oldConfigCB, oldConfigCA, newConfigCC, newConfigCB, newConfigCA;
		do {
			oldCellD = newCellD = currentCell.get();
			oldConfigCD = newConfigCD = config.get(oldCellD);
			cellC = newCellD - 1;
			cellB = cellC - 1;
			cellA = cellB - 1;
			oldConfigCC = newConfigCC = config.get(cellC);
			oldConfigCB = newConfigCB = config.get(cellB);
			oldConfigCA = newConfigCA = config.get(cellA);
			newConfigCD = RULE[(newConfigCC + newConfigCD) % RULESIZE];
			newConfigCC = RULE[(newConfigCB + newConfigCC) % RULESIZE];
			newConfigCB = RULE[(newConfigCA + newConfigCB) % RULESIZE];
			if (cellA == 0) {
				newConfigCA = RULE[(config.get(CA_WIDTH - 1) + newConfigCA) % RULESIZE];
				newCellD = CA_WIDTH - 1;
			} else {
				newConfigCA = RULE[(config.get(cellA - 1) + newConfigCA) % RULESIZE];
				newCellD -= 4;
			}
		} while (!(currentCell.compareAndSet(oldCellD, newCellD)
				&& config.compareAndSet(oldCellD, oldConfigCD, newConfigCD)
				&& config.compareAndSet(cellC, oldConfigCC, newConfigCC)
				&& config.compareAndSet(cellB, oldConfigCB, newConfigCB)
				&& config.compareAndSet(cellA, oldConfigCA, newConfigCA)));
		int ret = newConfigCA;
		ret <<= 8;
		ret ^= newConfigCB;
		ret <<= 8;
		ret ^= newConfigCC;
		ret <<= 8;
		ret ^= newConfigCD;
		return ret >>> (32 - bits);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CellularAutomaton [initialized=");
		builder.append(initialized);
		builder.append(", config=");
		builder.append(config);
		builder.append(", currentCell=");
		builder.append(currentCell);
		builder.append("]");
		return builder.toString();
	}

}