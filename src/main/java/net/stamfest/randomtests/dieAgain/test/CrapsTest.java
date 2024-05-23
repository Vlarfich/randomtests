package net.stamfest.randomtests.dieAgain.test;


import net.stamfest.randomtests.dieAgain.util.StandardTest;
import net.stamfest.randomtests.dieAgain.util.TestData;
import net.stamfest.randomtests.dieAgain.util.TestPoint;
import net.stamfest.randomtests.dieAgain.util.TestVector;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Arrays;
import java.util.Random;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class CrapsTest implements ITest {
	public static final TestData CRAPS;
	static {
		CRAPS = new TestData();
		CRAPS.setName("Craps Game Test");
		CRAPS.setDescription("");
		CRAPS.setNkps(2);
		CRAPS.setpSamplesStandard(100);
		CRAPS.settSamplesStandard(200000);
		CRAPS.setTestMethod(new CrapsTest());
	}
	public static final double WIN_PROBABILITY = 244.0 / 495.0;

	public CrapsTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		int point, thrown, tries;
		for (StandardTest current : parameters) {
			current.setnTuple((byte) 0);
			TestPoint wins = new TestPoint();
			wins.setPoints(current.gettSamples());
			wins.setY(WIN_PROBABILITY * wins.getPoints());
			wins.setSigma(wins.getY() * (1 - WIN_PROBABILITY));
			TestVector lengths = new TestVector();
			lengths.setNvec(21);
			lengths.setCutoff(5);
			double sum = 1.0 / 3.0;
			lengths.getY()[0] = sum * current.gettSamples();
			for (int i = 1; i < 20; i++) {
				lengths.getY()[i] = (27 * Math.pow(27.0 / 36.0, i - 1) + 40 * Math.pow(13.0 / 18.0, i - 1)
						+ 55 * Math.pow(25.0 / 36.0, i - 1)) / 648;
				sum += lengths.getY()[i];
				lengths.getY()[i] *= current.gettSamples();
			}
			lengths.getY()[20] = (1.0 - sum) * current.gettSamples();
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				wins.setX(0);
				Arrays.fill(lengths.getX(), 0);
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					//nextInt: [0;5], gleichverteilt
					//nextInt+nextInt: [0;10], dreiecksverteilt
					point = rng.nextInt(6) + rng.nextInt(6) + 2;
					tries = 0;
					if (point == 7 || point == 11) {
						wins.setX(wins.getX() + 1);
						lengths.getX()[tries]++;
					} else if (point == 2 || point == 3 || point == 12) {
						lengths.getX()[tries]++;
					} else {
						while (true) {
							thrown = rng.nextInt(6) + rng.nextInt(6) + 2;
							tries = Math.min(20, tries + 1);
							if (thrown == 7) {
								lengths.getX()[tries]++;
								break;
							} else if (thrown == point) {
								wins.setX(wins.getX() + 1);
								lengths.getX()[tries]++;
								break;
							}
						}
					}
				}
				wins.evaluate();
				lengths.evaluate();
				current.getpValues()[2 * pSample] = wins.getpValue();
				current.getpValues()[2 * pSample + 1] = lengths.getpValue();
			}
			current.evaluate();
			current.getPvLabels()[0] = "Binomial Distribution of Wins";
			current.getPvLabels()[1] = "Geometric Distribution of Lengths";
		}
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = CRAPS.createTest(40);
		CRAPS.getTestMethod().runTestOn(new PCGHash(), test);
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