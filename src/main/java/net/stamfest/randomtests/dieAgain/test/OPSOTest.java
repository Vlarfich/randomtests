package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.Dispenser;
import net.stamfest.randomtests.dieAgain.util.StandardTest;
import net.stamfest.randomtests.dieAgain.util.TestData;
import net.stamfest.randomtests.dieAgain.util.TestPoint;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Random;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class OPSOTest implements ITest {
	public static final TestData OPSO;
	static {
		OPSO = new TestData();
		OPSO.setName("Overlapping-Pairs-Sparse-Occupancy Test");
		OPSO.setDescription("");
		OPSO.setNkps(1);
		OPSO.setpSamplesStandard(100);
		OPSO.settSamplesStandard(2097152);
		OPSO.setTestMethod(new OPSOTest());
	}
	public static final int T_SAMPLES = 2097152;
	public static final double MEAN = 141909.3299550069;
	public static final double SIGMA = 290.4622634038;

	public OPSOTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest currentTest : parameters) {
			currentTest.setnTuple((byte) 0);
			currentTest.settSamples(T_SAMPLES);
			TestPoint pTest = new TestPoint();
			pTest.setY(MEAN);
			pTest.setSigma(SIGMA);
			pTest.setPoints(T_SAMPLES);
			Dispenser index0 = new Dispenser(), index1 = new Dispenser();
			index0.setRandom(rng);
			index1.setRandom(rng);
			// Konvertiere zu Parallel.
			for (byte i = 0; i < 3; i++) {
				index0.getBits((byte) 32);
				index1.getBits((byte) 32);
			}
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				pTest.setX(0);
				boolean[] words = new boolean[1024 * 1024];
				for (int tSample = 0; tSample < T_SAMPLES; tSample++) {
					words[1024 * index0.getBitsAsInteger((byte) 10) + index1.getBitsAsInteger((byte) 10)] = true;
				}
				for (boolean word : words) {
					if (!word) {
						pTest.setX(pTest.getX() + 1.0);
					}
				}
				pTest.evaluate();
				currentTest.getpValues()[pSample] = pTest.getpValue();
			}
			currentTest.evaluate();
			currentTest.getPvLabels()[0] = "Normal Distribution of missing words";
		}
	}

	@Deprecated
	public static final void main(String... args) {
		StandardTest test = OPSO.createTest(50);
		OPSO.getTestMethod().runTestOn(new PCGHash(), test);
		System.out.println(test.getPvLabels()[0]);
		for (int pv = 0; pv < test.getpSamples(); pv++) {
			System.out.println(test.getpValues()[pv]);
		}
		System.out.println("Final p-Value: " + test.getKs_pValue());
		System.out.print("Final Verdict: ");
		if (test.hasFailed()) {
			System.out.println("Failed");
		} else if (test.isWeak()) {
			System.out.println("Weak");
		} else {
			System.out.println("Passed");
		}
	}
}