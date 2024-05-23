package net.stamfest.randomtests.dieAgain.test;


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
public class MonobitTest implements ITest {

	public static final TestData MONO_BIT;
	static {
		MONO_BIT = new TestData();
		MONO_BIT.setName("Mono Bit Test");
		MONO_BIT.setDescription("");
		MONO_BIT.setNkps(1);
		MONO_BIT.setpSamplesStandard(100);
		MONO_BIT.settSamplesStandard(100000);
		MONO_BIT.setTestMethod(new MonobitTest());
	}

	public MonobitTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest current : parameters) {
			TestPoint pTest = new TestPoint();
			pTest.setPoints(32 * current.gettSamples());
			pTest.setSigma(Math.sqrt(pTest.getPoints()));
			pTest.setY(0);
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				pTest.setX(0);
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					pTest.setX(pTest.getX() + Integer.bitCount(rng.nextInt()));
				}
				pTest.setX(2 * pTest.getX() - pTest.getPoints());
				pTest.evaluate();
				current.getpValues()[pSample] = pTest.getpValue();
			}
			current.evaluate();
			current.getPvLabels()[0] = "Normal Distribution of set Bits";
		}
	}

	@Deprecated
	public static void main(String[] args) {
		StandardTest test = MONO_BIT.createTest(100, 1000000);
		test.setnTuple((byte) 32);
		MONO_BIT.getTestMethod().runTestOn(new PCGHash(), test);
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