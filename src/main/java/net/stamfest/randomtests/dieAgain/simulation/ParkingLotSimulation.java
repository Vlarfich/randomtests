package net.stamfest.randomtests.dieAgain.simulation;

import java.util.*;

public class ParkingLotSimulation {
	private Random r = new Random();
	private int dimensions = 2;
	private double length = 100;

	public ParkingLotSimulation() {
		super();
	}

	/**
	 * Try to park {@link tries} squares/cubes/hypercubes on a equally dimensioned
	 * grid of a certain size.
	 * 
	 * @param tries How many placement attempts should be made
	 * @return The actual number of placed "objects".
	 */
	public int runForTries(long tries) {
		List<double[]> points = new LinkedList<>();
		double[] point = new double[dimensions];
		boolean crashed = false;
		for (int d = 0; d < dimensions; d++) {
			point[d] = r.nextDouble() * length;
		}
		points.add(point);
		for (; tries > 0; tries--) {
			point = new double[dimensions];
			crashed = false;
			for (int d = 0; d < dimensions; d++) {
				point[d] = r.nextDouble() * length;
			}
			for (double[] parked : points) {
				int tcc = 0;
				for (int d = 0; d < dimensions; d++) {
					if (Math.abs(point[d] - parked[d]) <= 1.0) {
						tcc++;
					}
				}
				if (tcc == dimensions) {
					crashed = true;
					break;
				}
			}
			if (!crashed) {
				points.add(point);
			}
		}
		return points.size();
	}

	/**
	 * Tries to park as many squares/cubes/hyper-cubes as possible on the grid.
	 * 
	 * @return The amount of parked objects, before the first crash occurred.
	 * @implNote The returned value is very likely to follow a "Modified half-normal
	 *           distribution".
	 */
	public int runUntilCrash() {
		List<double[]> points = new LinkedList<>();
		double[] point = new double[dimensions];
		boolean crashed = false;
		for (int d = 0; d < dimensions; d++) {
			point[d] = r.nextDouble() * length;
		}
		points.add(point);
		do {
			point = new double[dimensions];
			for (int d = 0; d < dimensions; d++) {
				point[d] = r.nextDouble() * length;
			}
			for (double[] parked : points) {
				int tcc = 0;
				for (int d = 0; d < dimensions; d++) {
					if (Math.abs(point[d] - parked[d]) <= 1.0) {
						tcc++;
					}
				}
				if (tcc == dimensions) {
					crashed = true;
					break;
				}
			}
			if (!crashed) {
				points.add(point);
			}
		} while (!crashed);
		return points.size();
	}

	public static void main(String... args) {
		ParkingLotSimulation sim = new ParkingLotSimulation();
		sim.dimensions = 3;
		sim.length = 10;
		int result;
		TreeMap<Integer, Long> counts = new TreeMap<Integer, Long>();
		for (long count = 1_000_000; count > 0; count--) {
			result = sim.runUntilCrash();
			counts.compute(result, (k, v) -> {
				if (v == null || v == 0) {
					return 1L;
				} else {
					return v + 1L;
				}
			});
		}
		int entryCount = 0;
		for (Map.Entry<Integer, Long> e : counts.entrySet()) {
			System.out.print(e.getKey() + "=" + e.getValue() + "\t");
			if (++entryCount % 5 == 0) {
				System.out.println();
			}
		}
	}
}