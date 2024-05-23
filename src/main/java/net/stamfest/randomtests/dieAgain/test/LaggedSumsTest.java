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
public class LaggedSumsTest implements ITest {
	public static final TestData LAGGED_SUMS;
	static {
		LAGGED_SUMS = new TestData();
		LAGGED_SUMS.setName("Lagged Sums Test");
		LAGGED_SUMS.setDescription(
				"Lagged Sums is summing up only 1 every (nTuple+1) numbers, for a total of t-sample numbers.");
		LAGGED_SUMS.setNkps(1);
		LAGGED_SUMS.setpSamplesStandard(100);
		LAGGED_SUMS.settSamplesStandard(1000000);
		LAGGED_SUMS.setTestMethod(new LaggedSumsTest());
	}

	public LaggedSumsTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest current : parameters) {
			for (int p = 0; p < current.getpSamples(); p++) {
				TestPoint test = new TestPoint();
				test.setY(current.gettSamples() * 0.5);
				test.setX(0);
				test.setPoints(current.gettSamples());
				test.setSigma(Math.sqrt(current.gettSamples() / 12.0));
				for (int t = 0; t < current.gettSamples(); t++) {
					test.setX(rng.nextFloat() + test.getX());
					for (int lag = 0; lag < current.getnTuple(); lag++) {
						rng.nextFloat();
					}
				}
				test.evaluate();
				current.getpValues()[p] = test.getpValue();
			}
			current.evaluate();
			current.getPvLabels()[0] = "Normal Distribution of lagged Sum";
		}
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = LAGGED_SUMS.createTest(48, 1000000);
		test.setnTuple((byte) 3);
		LAGGED_SUMS.getTestMethod().runTestOn(new PCGHash(), test);
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