package net.stamfest.randomtests.dieAgain.test;

import net.stamfest.randomtests.dieAgain.util.*;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Random;
import java.util.TreeMap;

/**
 * 
 * @author Christian Schürhoff
 *
 */
public class BitFlippingTest implements ITest {
	public static final TestData BIT_FLIPS;
	static {
		BIT_FLIPS = new TestData();
		BIT_FLIPS.setName("Bit Flipping Test");
		BIT_FLIPS.setDescription("WARNING: VERY HARD TEST!\n"
				+ "Each nTuple Bits are observed for flips (0 to 1 or 1 to 0). Between two tuples should be a binomially distributed number of flipped bits. "
				+ "The number of tuples for any given bit position to flip again should be geometrically distributed. And on average, any two bits should have only flipped "
				+ "half the time together: Their correlation should be close two zero.");
		BIT_FLIPS.setNkps(3);
		BIT_FLIPS.setpSamplesStandard(24);
		BIT_FLIPS.settSamplesStandard(1024);
		BIT_FLIPS.setTestMethod(new BitFlippingTest());
	}

	public BitFlippingTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest currentTest : parameters) {
			if (currentTest.getnTuple() == 0) {
				currentTest.setnTuple((byte) 16);
			}
			final byte bitCount = currentTest.getnTuple();
			for (int pSample = 0; pSample < currentTest.getpSamples(); pSample++) {
				long currentNumber, prevNumber = bitSource.getBits(bitCount), difference;
				TestVector numberOfBitFlips = new TestVector();
				numberOfBitFlips.setNvec(bitCount + 1);
				numberOfBitFlips.setNdof(0);
				numberOfBitFlips.setCutoff(5);
				int[] lastBitFlip = new int[bitCount];
				for (byte bit = 0; bit < bitCount; bit++) {
					lastBitFlip[bit] = -1; // Nicht bekannt.
				}
				int[][] simultaneousBitFlips = new int[bitCount][bitCount];
				TreeMap<Integer, Integer> waitingTimes = new TreeMap<Integer, Integer>();
				for (int tSample = 0; tSample < currentTest.gettSamples(); tSample++) {
					currentNumber = bitSource.getBits(bitCount);
					difference = currentNumber ^ prevNumber;
					numberOfBitFlips.getX()[Long.bitCount(difference)] += 1.0;
					prevNumber = currentNumber;
					for (byte bitI = 0; bitI < bitCount; bitI++) {
						if (lastBitFlip[bitI] != -1) {
							if (isBitSet(difference, bitI)) {
								waitingTimes.compute(lastBitFlip[bitI], (distance, frequency) -> {
									if (frequency == null || frequency == 0) {
										return 1;
									} else {
										return frequency + 1;
									}
								});
							} else {
								lastBitFlip[bitI]++;
							}
						}
						if (isBitSet(difference, bitI)) {
							lastBitFlip[bitI] = 0;
							for (byte bitJ = 0; bitJ < bitCount; bitJ++) {
								if (isBitSet(difference, bitJ)) {
									simultaneousBitFlips[bitI][bitJ]++;
								}
							}
						}
					}
				}
				for (byte bit = 0; bit <= bitCount; bit++) {
					numberOfBitFlips.getY()[bit] = currentTest.gettSamples()
							* Functions.binomialCoefficent(bitCount, bit) * Math.pow(0.5, bitCount);
				}
				numberOfBitFlips.evaluate();
				currentTest.getpValues()[3 * pSample] = numberOfBitFlips.getpValue();
				TestVector waitingTimesDistribution = new TestVector();
				waitingTimesDistribution.setNvec(waitingTimes.lastKey() + 1);
				waitingTimesDistribution.setNdof(0);
				waitingTimesDistribution.setCutoff(5);
				waitingTimesDistribution.getY()[0] = currentTest.gettSamples() * bitCount * 0.25;
				waitingTimesDistribution.getX()[0] = waitingTimes.getOrDefault(0, 0);
				for (int distance = 1; distance < waitingTimesDistribution.getNvec(); distance++) {
					waitingTimesDistribution.getY()[distance] = waitingTimesDistribution.getY()[distance - 1] * 0.5;
					waitingTimesDistribution.getX()[distance] = waitingTimes.getOrDefault(distance, 0);
				}
				waitingTimesDistribution.evaluate();
				currentTest.getpValues()[3 * pSample + 1] = waitingTimesDistribution.getpValue();
				double expectedCoFlips;
				double chsq = 0.0;
				for (byte bitI = 1; bitI < bitCount; bitI++) {
					for (byte bitJ = 0; bitJ < bitI; bitJ++) {
						expectedCoFlips = Math.min(simultaneousBitFlips[bitI][bitI], simultaneousBitFlips[bitJ][bitJ])
								/ 2.0;
						chsq += (simultaneousBitFlips[bitI][bitJ] - expectedCoFlips)
								* (simultaneousBitFlips[bitI][bitJ] - expectedCoFlips) / expectedCoFlips;
					}
				}
				currentTest.getpValues()[3 * pSample + 2] = 1
						- Functions.cdfChiSquare((bitCount - 1) * (bitCount) / 2, chsq);
			}
			currentTest.getPvLabels()[0] = "Binomial Distributions of total Flips";
			currentTest.getPvLabels()[1] = "Geometric Waiting Time between Flips";
			currentTest.getPvLabels()[2] = "Correlation between flipped Bits.";
			currentTest.evaluate();
		}
	}

	public static boolean isBitSet(long value, byte bit) {
		return (value & (1L << bit)) != 0;
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = BIT_FLIPS.createTest(40, 0x10000);
		test.setnTuple((byte) 8);
		BIT_FLIPS.getTestMethod().runTestOn(new PCGHash(), test);
		// System.out.println(test);
		for (int nk = 0; nk < test.getNkps(); nk++) {
			System.out.print("|" + test.getPvLabels()[nk] + "\t");
		}
		System.out.println();
		for (int pSample = 0; pSample < test.getpSamples(); pSample++) {
			for (int nk = 0; nk < test.getNkps(); nk++) {
				System.out.print("%f\t\t\t\t".formatted(test.getpValues()[pSample * test.getNkps() + nk]));
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