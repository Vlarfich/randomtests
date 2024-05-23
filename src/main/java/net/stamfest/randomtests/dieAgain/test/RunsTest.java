package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.Dispenser;
import net.stamfest.randomtests.dieAgain.util.StandardTest;
import net.stamfest.randomtests.dieAgain.util.TestData;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Random;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class RunsTest implements ITest {
	public static final TestData RUNS;
	static {
		RUNS = new TestData();
		RUNS.setpSamplesStandard(100);
		RUNS.settSamplesStandard(100000);
		RUNS.setNkps(2);
		RUNS.setName("Runs Test");
		RUNS.setDescription("");
		RUNS.setTestMethod(new RunsTest());
	}
	private static final double[][] a = { { 4529.4, 9044.9, 13568.0, 18091.0, 22615.0, 27892.0 },
			{ 9044.9, 18097.0, 27139.0, 36187.0, 45234.0, 55789.0 },
			{ 13568.0, 27139.0, 40721.0, 54281.0, 67852.0, 83685.0 },
			{ 18091.0, 36187.0, 54281.0, 72414.0, 90470.0, 111580.0 },
			{ 22615.0, 45234.0, 67852.0, 90470.0, 113262.0, 139476.0 },
			{ 27892.0, 55789.0, 83685.0, 111580.0, 139476.0, 172860.0 } };
	/**
	 * Half of the chances of gaining a run of a certain length.
	 */
	private static final double[] b = { 1.0 / 6.0, 5.0 / 24.0, 11.0 / 120.0, 19.0 / 720.0, 29.0 / 5040.0, 1.0 / 840.0 };
	public static final byte RUN_MAX = 6;

	public RunsTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		long current, prev, first;
		for (StandardTest currentTest : parameters) {
			for (int sample = 0; sample < currentTest.getpSamples(); sample++) {
				int[] upRuns = new int[RUN_MAX], downRuns = new int[RUN_MAX];
				int upCount = 1, downCount = 1;
				current = prev = first = bitSource.getBits(currentTest.getnTuple());
				for (int numbers = 1; numbers < currentTest.gettSamples(); numbers++) {
					current = bitSource.getBits(currentTest.getnTuple());
					if (current > prev) {
						upCount = Math.min(upCount + 1, RUN_MAX);
						downRuns[downCount - 1]++;
						downCount = 1;
					} else {
						downCount = Math.min(downCount + 1, RUN_MAX);
						upRuns[upCount - 1]++;
						upCount = 1;
					}
					prev = current;
				}
				if (first > current) {
					upCount = Math.min(upCount + 1, RUN_MAX);
					downRuns[downCount - 1]++;
					downCount = 1;
				} else {
					downCount = Math.min(downCount + 1, RUN_MAX);
					upRuns[upCount - 1]++;
					upCount = 1;
				}
				double uv = 0, dv = 0;
				for (byte i = 0; i < RUN_MAX; i++) {
					for (byte j = 0; j < RUN_MAX; j++) {
						uv += (upRuns[i] - currentTest.gettSamples() * b[i])
								* (upRuns[j] - currentTest.gettSamples() * b[j]) * a[i][j];
						dv += (downRuns[i] - currentTest.gettSamples() * b[i])
								* (downRuns[j] - currentTest.gettSamples() * b[j]) * a[i][j];
					}
				}
				uv /= currentTest.gettSamples();
				dv /= currentTest.gettSamples();
				currentTest.getpValues()[2 * sample] = 1 - Math.exp(-0.5 * uv) * (1 + 0.5 * uv + 0.125 * uv * uv);
				currentTest.getpValues()[2 * sample + 1] = 1 - Math.exp(-0.5 * dv) * (1 + 0.5 * dv + 0.125 * dv * dv);
			}
			currentTest.getPvLabels()[0] = "Up-Runs";
			currentTest.getPvLabels()[1] = "Down-Runs";
			currentTest.evaluate();
		}
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = RUNS.createTest(48);
		test.setnTuple((byte) 8);
		RUNS.getTestMethod().runTestOn(new PCGHash(), test);
		// System.out.println(test);
		for (int nk = 0; nk < test.getNkps(); nk++) {
			System.out.print(test.getPvLabels()[nk] + "  \t");
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