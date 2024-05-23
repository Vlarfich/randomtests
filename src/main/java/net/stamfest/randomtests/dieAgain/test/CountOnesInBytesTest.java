package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.*;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Random;

/**
 * 
 * @author Christian Schürhoff
 *
 */
public class CountOnesInBytesTest implements ITest {
	private static final byte[] BYTE_TO_LETTER = { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 2, 0, 0, 0, 1, 0, 1, 1,
			2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 0, 0, 1, 0, 1, 1, 2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2,
			3, 2, 3, 3, 4, 0, 0, 0, 1, 0, 1, 1, 2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3,
			4, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 4, 0, 0, 0,
			1, 0, 1, 1, 2, 0, 1, 1, 2, 1, 2, 2, 3, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 0, 1, 1, 2, 1, 2, 2,
			3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 4, 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2,
			3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4,
			4, 2, 3, 3, 4, 3, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4 };
	private static final double[] LETTER_PROBABILITIES = { 0.14453125, 0.21875, 0.2734375, 0.21875, 0.14453125 };
	public static final TestData COUNT1S_BYTES;
	static {
		COUNT1S_BYTES = new TestData();
		COUNT1S_BYTES.setName("Count 1s in Bytes");
		COUNT1S_BYTES.setDescription(
				"Counts the set bits in five successive non-overlapping bytes and uses these to create a 5-letter world.");
		COUNT1S_BYTES.setpSamplesStandard(100);
		COUNT1S_BYTES.settSamplesStandard(256000);
		COUNT1S_BYTES.setNkps(1);
		COUNT1S_BYTES.setTestMethod(new CountOnesInBytesTest());
	}

	public CountOnesInBytesTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser byteSource = new Dispenser();
		byteSource.setRandom(rng);
		for (StandardTest current : parameters) {
			TestVector test4 = new TestVector();
			TestVector test5 = new TestVector();
			TestPoint pTest = new TestPoint();
			pTest.setY(2500);
			pTest.setSigma(Math.sqrt(5000));
			current.setnTuple((byte) 0);
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				test4.setNvec(625);
				test4.setCutoff(5);
				test4.setNdof(0);
				test5.setNvec(3125);
				test5.setNdof(0);
				test5.setCutoff(5);
				int j;
				for (int i = 0; i < 625; i++) {
					test4.getX()[i] = 0.0;
					test4.getY()[i] = current.gettSamples();
					j = i;
					for (int k = 0; k < 4; k++) {
						test4.getY()[i] *= LETTER_PROBABILITIES[j % 5];
						j /= 5;
					}
				}
				for (int i = 0; i < 3125; i++) {
					test5.getX()[i] = 0.0;
					test5.getY()[i] = current.gettSamples();
					j = i;
					for (int k = 0; k < 5; k++) {
						test5.getY()[i] *= LETTER_PROBABILITIES[j % 5];
						j /= 5;
					}
				}
				j = 0;
				for (long tSample = 0; tSample < current.gettSamples(); tSample++) {
					// Create next "word"
					for (int k = 0; k < 5; k++) {
						j = j * 5 + BYTE_TO_LETTER[(short) byteSource.getBits((byte) 8)];
					}
					j %= 3125; //Cut off all letters above the fifth one.
					test5.getX()[j]++;
					j %= 625; //Remove the fifth letter.
					test4.getX()[j]++;
				}
				test4.evaluate();
				test5.evaluate();
				pTest.setX(test5.getChsq() - test4.getChsq());
				pTest.evaluate();
				current.getpValues()[pSample] = pTest.getpValue();
			}
			current.getPvLabels()[0] = "Normality of Chi-Square difference";
			current.evaluate();
		}
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = COUNT1S_BYTES.createTest(48);
		COUNT1S_BYTES.getTestMethod().runTestOn(new PCGHash(), test);
		System.out.println(test.getPvLabels()[0]);
		for (double p : test.getpValues()) {
			System.out.println(p);
		}
		System.out.println("Final KS p-Value: " + test.getKs_pValue());
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