package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.StandardTest;
import net.stamfest.randomtests.dieAgain.util.TestData;
import net.stamfest.randomtests.dieAgain.util.TestVector;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Random;

/**
 * 
 * @author Christian Schürhoff
 *
 */
public class GCDTest implements ITest {
	/**
	 * Average value of steps of the euclid's algorithm for finding the gcd of two
	 * numbers, when said numbers are in range from 1 to 2<sup>32</sup>-1. These
	 * numbers must be unsigned 32-bit integers.
	 */
	public static final double MEAN = expectedAverageOfSteps(Integer.toUnsignedLong(-1));
	/**
	 * Standard deviation of steps of the euclid's algorithm for finding the gcd of
	 * two numbers, when said numbers are in range from 1 to 2<sup>32</sup>-1. These
	 * numbers must be unsigned 32-bit integers.
	 */
	public static final double SIGMA = standardDeviationOfSteps(Integer.toUnsignedLong(-1));
	/**
	 * Resulting success probability for a binomial distribution. An approximate for
	 * the number of steps.
	 */
	public static final double PROBABILITY = 1 - SIGMA * SIGMA / MEAN;
	/**
	 * Euclid's algorithm takes the maximum amount of steps, when the two chosen
	 * numbers are consecutive fibonnaci-numbers.
	 */
	public static final byte N = 51;
	private static final double[] K_PROBABILITIES = { 0.0, 5.39e-09, 6.077e-08, 4.8421e-07, 2.94869e-06, 1.443266e-05,
			5.908569e-05, 0.00020658047, 0.00062764766, 0.00167993762, 0.00399620143, 0.00851629626, 0.01635214339,
			0.02843154488, 0.04493723812, 0.06476525706, 0.08533638862, 0.1030000214, 0.11407058851, 0.11604146948,
			0.10853040184, 0.09336837411, 0.07389607162, 0.05380182248, 0.03601960159, 0.02215902902, 0.01251328472,
			0.00647884418, 0.00306981507, 0.00132828179, 0.00052381841, 0.00018764452, 6.084138e-05, 1.779885e-05,
			4.66795e-06, 1.09504e-06, 2.2668e-07, 4.104e-08, 6.42e-09, 8.4e-10, 1.4e-10 };
	public static final double RECIPROCAL_ZETA_2 = 6 / (Math.PI * Math.PI);
	public static final TestData GCD;
	static {
		GCD = new TestData();
		GCD.setName("Greatest Common Divisor Test");
		GCD.setDescription("");
		GCD.setpSamplesStandard(50);
		GCD.settSamplesStandard(10000000);
		GCD.setTestMethod(new GCDTest());
		GCD.setNkps(2);
	}

	public GCDTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest currentTest : parameters) {
			currentTest.setnTuple((byte) 0);
			TestVector ktbl = new TestVector(), gcd = new TestVector();
			ktbl.setNvec(K_PROBABILITIES.length);
			ktbl.setCutoff(5);
			final int gtblsize = (int) Math.sqrt(currentTest.gettSamples() * RECIPROCAL_ZETA_2 / 100);
			gcd.setNvec(gtblsize);
			gcd.setNdof(gtblsize - 2);
			gcd.setCutoff(0);
			for (int i = 0; i < K_PROBABILITIES.length; i++) {
				ktbl.getY()[i] = currentTest.gettSamples() * K_PROBABILITIES[i];
			}
			gcd.getY()[0] = 0;
			double sum = 0;
			for (int i = 1; i < gtblsize - 1; i++) {
				gcd.getY()[i] = currentTest.gettSamples() * RECIPROCAL_ZETA_2 / (i * i);
				sum += gcd.getY()[i];
			}
			gcd.getY()[gtblsize - 1] = currentTest.gettSamples() - sum;
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				for (int i = 0; i < K_PROBABILITIES.length; i++) {
					ktbl.getX()[i] = 0;
				}
				for (int i = 0; i < gtblsize; i++) {
					gcd.getX()[i] = 0;
				}
				long u, v, k, w;
				for (int tSample = 0; tSample < currentTest.gettSamples(); tSample++) {
					k = 0;
					do {
						u = Integer.toUnsignedLong(rng.nextInt());
					} while (u == 0);
					do {
						v = Integer.toUnsignedLong(rng.nextInt());
					} while (v == 0);
					do {
						w = u % v;
						u = v;
						v = w;
						k++;
					} while (v > 0);
					u = Math.min(gtblsize - 1, u);
					gcd.getX()[(int) u]++;
					k = Math.min(k, K_PROBABILITIES.length - 1);
					ktbl.getX()[(int) k]++;
				}
				ktbl.evaluate();
				gcd.evaluate();
				currentTest.getpValues()[2 * pSample] = gcd.getpValue();
				currentTest.getpValues()[2 * pSample + 1] = ktbl.getpValue();
			}
			currentTest.evaluate();
			currentTest.getPvLabels()[0] = "Zeta(2) Distribution of GCDs";
			currentTest.getPvLabels()[1] = "Binomial Distribution of steps";
		}
	}

	public static final double expectedAverageOfSteps(long max) {
		return Math.log(4096) * Math.log(max) / (Math.PI * Math.PI) + 0.06535;
	}

	public static final double varianceOfSteps(long max) {
		return 51 * Math.log(max) / 99 + 1.0 / 6.0;
	}

	public static final double standardDeviationOfSteps(long max) {
		return Math.sqrt(varianceOfSteps(max));
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = GCD.createTest(8);
		GCD.getTestMethod().runTestOn(new PCGHash(), test);
		// System.out.println(test);
		for (int nk = 0; nk < test.getNkps(); nk++) {
			System.out.print(test.getPvLabels()[nk] + "\t");
		}
		System.out.println();
		for (int pSample = 0; pSample < test.getpSamples(); pSample++) {
			for (int nk = 0; nk < test.getNkps(); nk++) {
				System.out.print("%f\t\t\t".formatted(test.getpValues()[pSample * test.getNkps() + nk]));
			}
			System.out.println();
		}
		System.out.println("Final p-Value of KS-Test: %f".formatted(test.getKs_pValue()));
		if (test.hasFailed()) {
			System.out.println("Bits are not random!");
		} else if (test.isWeak()) {
			System.out.println("Bits are weakly non-random.");
		} else {
			System.out.println("Bits are random.");
		}
	}
}