package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.Dispenser;
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
public class SBoxCyclesTest implements ITest {
	public static final TestData SBOX_CYCLES;
	static {
		SBOX_CYCLES = new TestData();
		SBOX_CYCLES.setName("S-Box Cycles Test");
		SBOX_CYCLES.setDescription("");
		SBOX_CYCLES.setNkps(1);
		SBOX_CYCLES.setpSamplesStandard(100);
		SBOX_CYCLES.settSamplesStandard(100000);
		SBOX_CYCLES.setTestMethod(new SBoxCyclesTest());
	}

	public SBoxCyclesTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest currentTest : parameters) {
			if (currentTest.getnTuple() > 31) {
				currentTest.setnTuple((byte) 31);
			} else if (currentTest.getnTuple() == 0) {
				currentTest.setnTuple((byte) 8);
			}
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				TestVector test = new TestVector();
				test.setNvec(1 << currentTest.getnTuple());
				test.setCutoff(5);
				int[] sbox = new int[test.getNvec()];
				for (int i = 0; i < sbox.length; i++) {
					sbox[i] = i;
					test.getY()[i] = currentTest.gettSamples() / (i + 1.0);
				}
				for (int tSample = 0; tSample < currentTest.gettSamples(); tSample++) {
					for (int i = 0; i < sbox.length; i++) {
						final int j = bitSource.getBitsAsInteger(currentTest.getnTuple());
						if (i != j) {
							sbox[i] ^= sbox[j];
							sbox[j] ^= sbox[i];
							sbox[i] ^= sbox[j];
						}
					}
					boolean[] visited = new boolean[sbox.length];
					for (int i = 0; i < sbox.length; i++) {
						if (!visited[i]) {
							visited[i] = true;
							int current = 1;
							for (int j = sbox[i]; j != i; j = sbox[j]) {
								current++;
								visited[j] = true;
							}
							test.getX()[current - 1] += 1.0;
						}
					}
				}
				test.equalize();
				//test.evaluateGTest();
				test.evaluateKLDivergence();
				currentTest.getpValues()[pSample] = test.getpValue();
			}
			currentTest.evaluate();
			currentTest.getPvLabels()[0] = "Harmonic Distribution of Cycles";
		}
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = SBOX_CYCLES.createTest();
		test.setnTuple((byte) 4);
		test.setpSamples(10);
		SBOX_CYCLES.getTestMethod().runTestOn(new PCGHash(), test);
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