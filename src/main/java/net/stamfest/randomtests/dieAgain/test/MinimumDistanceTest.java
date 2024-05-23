package net.stamfest.randomtests.dieAgain.test;

import net.stamfest.randomtests.dieAgain.util.Functions;
import net.stamfest.randomtests.dieAgain.util.StandardTest;
import net.stamfest.randomtests.dieAgain.util.TestData;
import net.stamfest.randomtests.dieAgain.util.TestObserver;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class MinimumDistanceTest implements ITest, Comparator<double[]> {
	public static final TestData MINIMUM_DISTANCE;
	static {
		MINIMUM_DISTANCE = new TestData();
		MINIMUM_DISTANCE.setName("Minimum Distance Test");
		MINIMUM_DISTANCE.setDescription("");
		MINIMUM_DISTANCE.setNkps(1);
		MINIMUM_DISTANCE.setpSamplesStandard(1000);
		MINIMUM_DISTANCE.settSamplesStandard(10000);
		MINIMUM_DISTANCE.setTestMethod(new MinimumDistanceTest());
	}
	private static final double[] Q = { 0.0, 0.0, 0.4135, 0.5312, 0.6202, 1.3789 };

	public MinimumDistanceTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		for (StandardTest current : parameters) {
			if (current.getnTuple() > Q.length - 1) {
				current.setnTuple((byte) (Q.length - 1));
			}
			if (current.getnTuple() < 2) {
				current.setnTuple((byte) 2);
			}
			final byte dimensions = current.getnTuple();
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				double[][] points = new double[current.gettSamples()][dimensions];
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					for (byte dim = 0; dim < dimensions; dim++) {
						points[tSample][dim] = rng.nextDouble();
					}
				}
				Arrays.sort(points, this);
				double mindist = Math.sqrt(dimensions), dist;
				for (int i = 0; i < current.gettSamples(); i++) {
					for (int j = i + 1; j < current.gettSamples(); j++) {
						if (points[j][0] - points[i][0] > mindist)
							break;
						dist = distance(points[i], points[j]);
						if (dist < mindist)
							mindist = dist;
					}
				}
				double dVolume;
				if ((current.getnTuple() % 2) == 0) {
					dVolume = Math.pow(Math.PI, dimensions / 2) * Math.pow(mindist, dimensions)
							/ Functions.gamma(dimensions / 2);
				} else {
					dVolume = 2.0 * Math.pow(Math.PI * 2.0, (dimensions - 1) / 2) * Math.pow(mindist, dimensions)
							/ Functions.doubleFactorial(dimensions);
				}
				double earg = -1.0 * current.gettSamples() * (current.gettSamples() - 1) * dVolume / 2.0;
				double qarg = 1.0
						+ ((2.0 + Q[dimensions]) / 6.0) * Math.pow(current.gettSamples(), 3) * dVolume * dVolume;
				current.getpValues()[pSample] = 1.0 - Math.exp(earg) * qarg;
			}
			current.getPvLabels()[0] = "Exponential Distribution of minimum Distances";
			current.evaluate();
		}
	}

	@Override
	public int compare(double[] a, double[] b) {
		double diff = a[0] - b[0];
		if (diff > 0)
			return 1;
		if (diff < 0)
			return -1;
		return 0;
	}

	private static final double distance(double[] a, double[] b) {
		double delta, distance = 0;
		for (int i = 0; i < a.length; i++) {
			delta = a[i] - b[i];
			distance += delta * delta;
		}
		return Math.sqrt(distance);
	}

	@Deprecated
	public static void main(String[] args) {
		StandardTest test = MINIMUM_DISTANCE.createTest(10, 100000);
		test.setnTuple((byte) 4);
		TestObserver observer = new TestObserver();
		observer.setTests(test);
		Thread t = new Thread(observer);
		t.start();
		MINIMUM_DISTANCE.getTestMethod().runTestOn(new PCGHash(), test);
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