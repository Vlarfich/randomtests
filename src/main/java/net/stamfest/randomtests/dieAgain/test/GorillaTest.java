package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.*;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Random;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class GorillaTest implements ITest {
	/**
	 * 
	 */
	public static final int MEAN = 24687971;
	/**
	 * 
	 */
	public static final int SIGMA = 4170;
	/**
	 * The amount of samples to take, this number is fixed.
	 */
	public static final int T_SAMPLES = 1 << 26;
	public static final TestData GORILLA;
	static {
		GORILLA = new TestData();
		GORILLA.setName("Gorilla Test");
		GORILLA.setDescription(
				"The Gorilla Test creates its numbers by creating only from a singular bit position, as such its effectivly 32 tests in parallel. "
						+ "26 32-bit integers are getting arranged into 32 26-bit integers and overlapping samples are taking. It neasures how many bit-patterns from those "
						+ "26-bit integers never occur in over 67 million samples.");
		GORILLA.setpSamplesStandard(2);
		GORILLA.settSamplesStandard(T_SAMPLES);
		GORILLA.setNkps(32);
		GORILLA.setTestMethod(new GorillaTest());
	}

	public GorillaTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest currentTest : parameters) {
			currentTest.setnTuple((byte) 0);
			boolean[][] visited = new boolean[32][T_SAMPLES];
			int[] intsFromBits = new int[32];
			TestPoint pTest = new TestPoint();
			pTest.setPoints(T_SAMPLES);
			pTest.setSigma(SIGMA);
			pTest.setY(MEAN);
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				// Clear previous run
				for (int i = 0; i < 32; i++) {
					intsFromBits[i] = 0;
					for (int j = 0; j < T_SAMPLES; j++) {
						visited[i][j] = false;
					}
				}
				// Preload 25 Bits
				for (int i = 0; i < 25; i++) {
					for (int j = 0; j < 32; j++) {
						intsFromBits[j] <<= 1;
						intsFromBits[j] += bitSource.getBits((byte) 1);
					}
				}
				for (int tSample = 0; tSample < T_SAMPLES; tSample++) {
					for (int i = 0; i < 32; i++) {
						intsFromBits[i] <<= 1;
						intsFromBits[i] += bitSource.getBits((byte) 1);
						intsFromBits[i] &= T_SAMPLES - 1;
						visited[i][intsFromBits[i]] = true;
					}
				}
				// Evaluate
				for (int i = 0; i < 32; i++) {
					pTest.setX(0);
					for (int j = 0; j < T_SAMPLES; j++) {
						if (!visited[i][j]) {
							pTest.setX(pTest.getX() + 1.0);
						}
					}
					pTest.evaluate();
					currentTest.getpValues()[32 * pSample + i] = pTest.getpValue();
				}
			}
			for (int i = 31; i >= 0; i--) {
				currentTest.getPvLabels()[31 - i] = "Bit " + i;
			}
			currentTest.evaluate();
		}
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = GORILLA.createTest(24);
		TestObserver observer = new TestObserver();
		observer.setTests(test);
		Thread t = new Thread(observer);
		t.start();
		GORILLA.getTestMethod().runTestOn(new PCGHash(), test);
		for (int nk = 0; nk < test.getNkps(); nk++) {
			System.out.print(test.getPvLabels()[nk] + "\t\t");
		}
		System.out.println();
		for (int pSample = 0; pSample < test.getpSamples(); pSample++) {
			for (int nk = 0; nk < test.getNkps(); nk++) {
				System.out.print("%f\t".formatted(test.getpValues()[pSample * test.getNkps() + nk]));
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