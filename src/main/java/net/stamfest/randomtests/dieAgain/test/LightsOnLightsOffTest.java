package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.*;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class LightsOnLightsOffTest implements ITest {
	public static final TestData LIGHTS_ON_LIGHTS_OFF;
	static {
		LIGHTS_ON_LIGHTS_OFF = new TestData();
		LIGHTS_ON_LIGHTS_OFF.setName("Lights On Lights Off");
		LIGHTS_ON_LIGHTS_OFF.setDescription(
				"This Test uses boolean operators (AND/OR) to first bring nTuple Bits on and then the same Bits off. "
						+ "The distribution of required steps should follow a discrete phase-type one and as such, "
						+ "it will take longer to compute the final distribution for a higher number of Bits.");
		LIGHTS_ON_LIGHTS_OFF.setNkps(1);
		LIGHTS_ON_LIGHTS_OFF.settSamplesStandard(50000);
		LIGHTS_ON_LIGHTS_OFF.setpSamplesStandard(64);
		LIGHTS_ON_LIGHTS_OFF.setTestMethod(new LightsOnLightsOffTest());
	}

	public LightsOnLightsOffTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest current : parameters) {
			if (current.getnTuple() > 31) {
				current.setnTuple((byte) 31);
			} else if (current.getnTuple() == 0) {
				current.setnTuple((byte) 16);
			}
			byte bits = current.getnTuple();
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				TreeMap<Integer, Integer> tries2Counts = new TreeMap<Integer, Integer>();
				long allOn = (1L << bits) - 1, lights = 0;
				int tries;
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					tries = 0;
					do {
						lights |= bitSource.getBits(bits);
						tries++;
					} while (lights != allOn);
					tries2Counts.compute(tries, (k, v) -> {
						if (v == null || v == 0) {
							return 1;
						} else {
							return v + 1;
						}
					});
					tries = 0;
					do {
						lights &= bitSource.getBits(bits);
						tries++;
					} while (lights != 0);
					tries2Counts.compute(tries, (k, v) -> {
						if (v == null || v == 0) {
							return 1;
						} else {
							return v + 1;
						}
					});
				}
				TestVector pTest = new TestVector();
				pTest.setNvec(tries2Counts.lastKey() + 1);
				pTest.setNdof(0);
				double[] vector = new double[1 << bits];
				Arrays.fill(vector, 0);
				vector[0] = current.gettSamples() * 2.0;
				double sum = 0;
				for (int i = 0; i < pTest.getNvec(); i++) {
					pTest.getY()[i] = vector[vector.length - 1] - sum;
					pTest.getX()[i] = tries2Counts.getOrDefault(i, 0);
					sum = vector[vector.length - 1];
					vector = updateStateVector(vector, bits);
				}
				pTest.equalize();
				pTest.evaluateGTest();
				current.getpValues()[pSample] = pTest.getpValue();
			}
			current.evaluate();
			current.getPvLabels()[0] = "DPTD of light switching";
		}
	}

	public static final double calculateTransitionChance(long start, long end, byte bits) {
		if (start == 0) {
			return Math.pow(0.5, bits);
		}
		byte startPop = (byte) Long.bitCount(start);
		byte endPop = (byte) Long.bitCount(end);
		if ((startPop >= endPop && start != end) || ((start & end) != start)) {
			return 0;
		} else {
			return Math.pow(0.5, bits - startPop);
		}
	}

	private static final double[] updateStateVector(double[] vector, byte bits) {
		double[] ret = new double[vector.length];
		Arrays.fill(ret, 0.0);
		for (int start = 0; start < vector.length; start++) {
			for (int end = 0; end < vector.length; end++) {
				ret[end] += vector[start] * calculateTransitionChance(start, end, bits);
			}
		}
		return ret;
	}

	@Deprecated
	public static void main(String[] args) {
		StandardTest test = LIGHTS_ON_LIGHTS_OFF.createTest(8, 50000);
		test.setnTuple((byte) 2);
		TestObserver observer = new TestObserver();
		observer.setTests(test);
		//Thread t = new Thread(observer);
		//t.start();
		LIGHTS_ON_LIGHTS_OFF.getTestMethod().runTestOn(new PCGHash(), test);
		for (int nk = 0; nk < test.getNkps(); nk++) {
			System.out.print(test.getPvLabels()[nk] + "\t\t");
		}
		System.out.println();
		for (int pSample = 0; pSample < test.getpSamples(); pSample++) {
			for (int nk = 0; nk < test.getNkps(); nk++) {
				System.out.print("%.7f\t".formatted(test.getpValues()[pSample * test.getNkps() + nk]));
			}
			System.out.println();
		}
		System.out.println("Final p-Value of KS-Test: %.7f".formatted(test.getKs_pValue()));
		if (test.hasFailed()) {
			System.out.println("Bits are not random!");
		} else if (test.isWeak()) {
			System.out.println("Bits are weakly non-random.");
		} else {
			System.out.println("Bits are random.");
		}
	}
}