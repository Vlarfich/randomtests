package net.stamfest.randomtests.dieAgain.util;

import java.util.Arrays;

import static java.lang.Math.*;

/**
 * 
 * @author Christian Schürhoff
 *
 */
public final class Functions {
	private Functions() {
		super();
	}

	private static final double[] erfCoefficientsT = { -1.26551223, 1.00002368, 0.37409196, 0.09678418, -0.18628806,
			0.27886807, -1.13520398, 1.48851587, -0.82215223, 0.17087277 };

	/**
	 * The gamma-function, for integer <tt>z</tt> it is equal to the
	 * factorial-function.
	 * 
	 * @param z
	 * @return {@code z!} for integer values of z or 2z.
	 */
	public static double gamma(double z) {
		int tmp = (int) (2 * z);
		if (tmp != 2 * z) {
			System.err.println("2z was not an integer! Result might be inaccurate.");
		}
		if (tmp == 0) {
			return Double.POSITIVE_INFINITY;
		} else if (tmp > 0) {
			double ret = 1;
			for (; tmp > 2; tmp -= 2) {
				ret *= tmp / 2.0;
			}
			if (tmp == 2) {
				return ret;
			} else /* tmp == 1 */ {
				return ret * sqrt(PI);
			}
		} else {
			throw new IllegalArgumentException("z is less than or equal to -0.5, gamma(z) can not be calculated!");
		}
	}

	/**
	 * Calculates the double factorial of a number.
	 * 
	 * @param n
	 * @return
	 */
	public static double doubleFactorial(long n) {
		if (n < 0) {
			throw new ArithmeticException("Double Factorial not defined for negative numbers!");
		} else if (n < 2) {
			return 1;
		} else {
			return n * doubleFactorial(n - 2);
		}
	}

	/**
	 * Probability density function for the standard normal distribution.
	 * 
	 * @param x
	 * @return
	 */
	public static double pdfStandardNormal(double x) {
		return exp(-x * x / 2) / sqrt(2 * PI);
	}

	/**
	 * An approximation of the error function with a maximal error of
	 * <tt>1.2 * 10<sup>-7</sup></tt> for any given value.
	 * 
	 * @param x
	 * @return
	 */
	public static double erf(double x) {
		double t = 1 / (1 + abs(x) / 2);
		double polynomial = 0;
		for (int e = 0; e < erfCoefficientsT.length; e++) {
			polynomial += erfCoefficientsT[e] * pow(t, e);
		}
		double tau = exp(-x * x + polynomial);
		if (x < 0) {
			return tau - 1;
		} else {
			return 1 - tau;
		}
	}

	/**
	 * Cumulative density function of the standard normal distribution.
	 * 
	 * @param x
	 * @return
	 */
	public static double cdfStandardNormal(double x) {
		double tmp = x / sqrt(2);
		tmp = 1 + erf(tmp);
		return tmp / 2;
	}

	/**
	 * Probability density function of the chi-square distribution
	 * 
	 * @param df
	 * @param x
	 * @return
	 */
	public static double pdfChiSquare(int df, double x) {
		double resGamma = 2 * gamma(df / 2.0);
		if (Double.isInfinite(resGamma)) {
			return 0;
		}
		double resExp = exp(-x / 2);
		if (resExp == 0) {
			return 0;
		}
		return (pow(x / 2, (df - 2) / 2.0) * resExp) / resGamma;
	}

	/**
	 * Cumulative density function of the chi-square distribution.
	 * 
	 * @param df degrees of freedom
	 * @param x  the value
	 * @return The relative amount of numbers smaller than the given value of x.
	 */
	public static double cdfChiSquare(int df, double x) {
		double[] prevCDFs = new double[(df + 1) / 2];
		if (df % 2 == 0) {
			prevCDFs[0] = 1 - exp(-x / 2); // 0->2
			for (int i = 1; i < prevCDFs.length; i++) {
				prevCDFs[i] = prevCDFs[i - 1] - 2 * pdfChiSquare(2 * i + 2, x); // 1->4, 2->6, 3->8
			}
		} else /* df % 2 == 1 */ {
			prevCDFs[0] = 2 * cdfStandardNormal(sqrt(x)) - 1; // 0->1
			for (int i = 1; i < prevCDFs.length; i++) {
				prevCDFs[i] = prevCDFs[i - 1] - 2 * pdfChiSquare(2 * i + 1, x); // 1->3, 2->5, 3->7
			}
		}
		return prevCDFs[prevCDFs.length - 1];
	}

	/**
	 * Probability density function of the poisson distribution.
	 * 
	 * @param lambda
	 * @param k
	 * @return
	 */
	public static double pdfPoisson(double lambda, int k) {
		if (k == 0) {
			return exp(-lambda);
		} else {
			return exp(-lambda) * pow(lambda, k) / gamma(k + 1);
		}
	}

	/**
	 * Probability density function of the anderson darling distribution.
	 * 
	 * @param z
	 * @return
	 */
	public static double cdfAndersonDarling(double z) {
		if (z < 0.01)
			return 0;
		if (z <= 2)
			return 2 * exp(-1.2337 / z) * (1 + z / 8 - 0.04958 * z * z / (1.325 + z)) / sqrt(z);
		if (z <= 4)
			return 1 - 0.6621361 * exp(-1.091638 * z) - 0.95095 * exp(-2.005138 * z);
		return 1 - 0.4938691 * exp(-1.050321 * z) - 0.5946335 * exp(-1.527198 * z);
	}

	/**
	 * Probability mass function of the binomial distribution.
	 * 
	 * @param k
	 * @param n
	 * @param p
	 * @return
	 */
	public static double pmfBinomial(long k, long n, double p) {
		if (0 <= p && p <= 1) {
			if (0 <= k && k <= n) {
				return binomialCoefficent(n, k) * pow(p, k) * pow(1 - p, n - k);
			} else {
				throw new IllegalArgumentException("k is unsupported.");
			}
		} else {
			throw new IllegalArgumentException("Probability must be between 0 and 1.");
		}
	}

	/**
	 * Cumulative probability function of the binomial distribution.
	 * 
	 * @param k
	 * @param n
	 * @param p
	 * @return
	 */
	public static double cdfBinomial(long k, long n, double p) {
		if (0 <= p && p <= 1) {
			if (0 <= k && k <= n) {
				double sum = 0;
				for (long i = 0; i <= k; i++) {
					sum += pmfBinomial(i, n, p);
				}
				return sum;
			} else {
				throw new IllegalArgumentException("k is unsupported.");
			}
		} else {
			throw new IllegalArgumentException("Probability must be between 0 and 1.");
		}
	}

	/**
	 * Computes nCk or alternatively n!/(k!*(n-k)!)
	 * 
	 * @param n the size of the set of elements
	 * @param k the number of chosen elements.
	 * @return Number of unordered subsets containing exactly k elements.
	 */
	public static double binomialCoefficent(long n, long k) {
		if (k < 0 || k > n)
			return 0;
		if (k == 0 || k == n)
			return 1;
		k = min(k, n - k);
		double c = 1;
		for (long i = 0; i < k; i++) {
			c = c * (n - i) / (i + 1);
		}
		return c;
	}

	public static double spline(double x, int n) {
		double tmp = abs(10 * x + 0.5 - n);
		if (n < 7) {
			if (tmp > 1.5)
				return 0;
			if (tmp <= 0.5)
				return 1.5 - 2 * tmp * tmp;
			else
				return 2.25 - tmp * (3 - tmp);
		} else {
			switch (n) {
			case 7:
				if (x <= 0.8 || x >= 1)
					return 0;
				else
					return 100 * (x - 0.9) * (x - 0.9) - 1;
			case 8:
				if (x <= 0 || x >= 0.05)
					return 0;
				if (x <= 0.01)
					return -100 * x;
				else
					return 25 * (x - 0.05);
			case 9:
				if (x <= 0.98 || x >= 1)
					return 0;
				else
					return 0.1 - 10 * abs(x - 0.99);
			default:
				throw new ArithmeticException("spline(x) can not be computed for n > 9");
			}
		}
	}

	/**
	 * Kolmogorow-Smirnov-Test
	 * 
	 * @param x
	 * @return
	 */
	public static double ksTest(double... x) {
		double pvalue, tmp, z = -x.length * x.length, epsilon = pow(10, -20);
		double[] xCopy = new double[x.length];
		System.arraycopy(x, 0, xCopy, 0, x.length);
		Arrays.sort(xCopy);
		for (int i = 0; i < xCopy.length; i++) {
			tmp = xCopy[i] * (1 - xCopy[xCopy.length - 1 - i]);
			tmp = max(epsilon, tmp);
			z -= (2 * i + 1) * log(tmp);
		}
		z /= xCopy.length;
		pvalue = 1 - cdfAndersonDarling(z);
		return pvalue;
	}

	/**
	 * Calculates the number of leading zeros for a short.
	 * 
	 * @param s
	 * @return a value between 0 and 16.
	 */
	public static int numberOfLeadingZeros(short s) {
		if (s < 0) {
			return 0;
		} else if (s == 0) {
			return Short.SIZE;
		} else {
			int ret = 0;
			do {
				s <<= 1;
				ret++;
			} while (s > 0);
			return ret;
		}
	}

	/**
	 * Calculates the number of trailing zeros for a short.
	 * 
	 * @param s
	 * @return a value between 0 and 16.
	 */
	public static int numberOfTrailingZeros(short s) {
		if (s == 0) {
			return Short.SIZE;
		} else {
			int ret = 0;
			while ((s & 1) != 1) {
				s >>>= 1;
				ret++;
			}
			return ret;
		}
	}

	/**
	 * Calculates the number of leading zeros for a byte.
	 * 
	 * @param b
	 * @return a value between 0 and 8.
	 */
	public static int numberOfLeadingZeros(byte b) {
		if (b < 0) {
			return 0;
		} else if (b == 0) {
			return Byte.SIZE;
		} else {
			int ret = 0;
			do {
				b <<= 1;
				ret++;
			} while (b > 0);
			return ret;
		}
	}

	/**
	 * Calculates the number of trailing zeros for a byte.
	 * 
	 * @param b
	 * @return a value between 0 and 8.
	 */
	public static int numberOfTrailingZeros(byte b) {
		if (b == 0) {
			return Byte.SIZE;
		} else {
			int ret = 0;
			while ((b & 1) != 1) {
				b >>>= 1;
				ret++;
			}
			return ret;
		}
	}

	public static double evaluateMostExtreme(double... pValues) {
		double ext = 1.0;
		int sign = 1;
		for (int i = 0; i < pValues.length; i++) {
			double p = pValues[i];
			int cursign = -1;
			if (p > 0.5) {
				p = 1 - p;
				cursign = 1;
			}
			if (p < ext) {
				ext = p;
				sign = cursign;
			}
		}
		ext = pow(1.0 - ext, pValues.length);
		if (sign == 1) {
			ext = 1 - ext;
		}
		return ext;
	}

	/**
	 * Calculates the probability that the observed data came from a poisson
	 * distribution with the given parameters.
	 * 
	 * @param observed what actually was measured
	 * @param lambda   first parameter of the poisson distribution
	 * @param kmax     second parameter of the poisson distribution
	 * @param nSamples the total amount of samples taken.
	 * @return a value between 0 and 1 indicating the goodness of fit.
	 * @see #pdfPoisson(double, int)
	 */
	public static double chiSquarePoisson(int[] observed, double lambda, int kmax, int nSamples) {
		double[] expected = new double[kmax];
		double chisq = 0;
		for (int k = 0; k < kmax; k++) {
			expected[k] = nSamples * pdfPoisson(lambda, k);
		}
		for (int k = 0; k < kmax; k++) {
			chisq += (observed[k] - expected[k]) * (observed[k] - expected[k]) / expected[k];
		}
		return 1 - cdfChiSquare(kmax - 1, chisq);
	}

	/**
	 * Calculates the probability that the observed data has the same underlying
	 * distribution as the expected data.
	 * 
	 * @param observed what was measured.
	 * @param expected what was expected.
	 * @return a value between 0 and 1 indicating the goodness of fit.
	 */
	public static double chiSquarePearson(double[] observed, double[] expected) {
		double chisq = 0;
		final int kmax = min(observed.length, expected.length);
		for (int k = 0; k < kmax; k++) {
			chisq += (observed[k] - expected[k]) * (observed[k] - expected[k]) / expected[k];
		}
		return 1 - cdfChiSquare(kmax - 1, chisq);
	}

	/**
	 * Calculates the probability the the observed data as a matrix is following a
	 * uniform distribution.
	 * 
	 * @param observed what was measured.
	 * @param rows     the number of rows to assume.
	 * @param cols     the number of columns to assume.
	 * @param n        the total number of measurements.
	 * @return a value between 0 and 1 indicating the goodness of fit.
	 */
	public static double chiSquare2D(int[] observed, int rows, int cols, long n) {
		double chisq = 0;
		final int ndof = (rows - 1) * (cols - 1);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int sum1 = 0, sum2 = 0;
				double expected, top;
				for (int k = 0; k < cols; k++) {
					sum1 += observed[i * cols + k];
				}
				for (int k = 0; k < rows; k++) {
					sum2 += observed[k * cols + j];
				}
				expected = (double) sum1 * sum2 / n;
				top = observed[i * cols + j] - expected;
				chisq += (top * top) / expected;
			}
		}
		return 1 - cdfChiSquare(ndof, chisq);
	}
}