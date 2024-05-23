package net.stamfest.randomtests.dieAgain.util;

import java.util.Arrays;

/**
 * The TestVector is used by statistical tests, which have a concrete expected
 * distribution ready.
 * 
 * @author Christian Schürhoff
 *
 */
public class TestVector {
	/**
	 * The Value e<sup>2</sup>.
	 */
	public static final double E_SQUARE = Math.E * Math.E;

	/**
	 * The transformation to be used for calculating the area mismatch.
	 * 
	 * @author Christian Sch�rhoff
	 * @see TestVector#evaluateAbsoluteAreaMismatch(Transform)
	 */
	public static enum Transform {
		/**
		 * Applies no transformation in the forward direction.
		 */
		IDENTITY {
			@Override
			public double forward(double d) {
				return d;
			}

			@Override
			public double backward(double d) {
				return d / 2;
			}
		},
		/**
		 * The forward transform is the natural logarithm. The backwards transform is
		 * the exp-function, followed by a division.
		 * 
		 * @see Math#log1p(double)
		 * @see Math#exp(double)
		 * @see TestVector#E_SQUARE
		 */
		LOGARITHMIC_DIVIDE {
			@Override
			public double forward(double d) {
				return Math.log1p(d);
			}

			@Override
			public double backward(double d) {
				return Math.exp(d) / E_SQUARE;
			}
		},
		/**
		 * The forward transform is the natural logarithm. The backwards transform is
		 * the exp-function, followed by taking the square root and a final division.
		 * 
		 * @see Math#log1p(double)
		 * @see Math#exp(double)
		 * @see Math#sqrt(double)
		 */
		LOGARITHMIC_SQRT {
			@Override
			public double forward(double d) {
				return Math.log1p(d);
			}

			@Override
			public double backward(double d) {
				return Math.sqrt(Math.exp(d)) / Math.E;
			}
		},
		/**
		 * The forward transformation is the expm1-function. The backward function is
		 * the natural logarithm.
		 * 
		 * @see Math#expm1(double)
		 * @see Math#log(double)
		 */
		EXPONENTIAL_DIVIDE {
			@Override
			public double forward(double d) {
				return Math.expm1(d);
			}

			@Override
			public double backward(double d) {
				return 1 - Math.log(d / 2);
			}
		},
		BOX_COX_RECIPROCAL {
			@Override
			public double forward(double d) {
				return 1.0 - d;
			}

			@Override
			public double backward(double d) {
				return 1.0 - 1.0 / (d + 1.0);
			}
		},
		BOX_COX_NEGATIVE_EXPONENTIAL {
			@Override
			public double forward(double d) {
				return 1.0 - d;
			}

			@Override
			public double backward(double d) {
				return 1.0 - Math.exp(-d);
			}
		};

		/**
		 * Transforms a value from the interval [0;1]. Used for individual area
		 * mismatches.
		 * 
		 * @param d
		 * @return
		 */
		public abstract double forward(double d);

		/**
		 * Undoes a transformation by the same instance to a sum of transformed values.
		 * Returns a number from the interval [0;1].
		 * 
		 * @param d
		 * @return
		 */
		public abstract double backward(double d);
	}

	/**
	 * Length of the two arrays.
	 */
	private int nvec;
	/**
	 * Degrees of freedom, usually {@link #nvec}-1.
	 */
	private int ndof;
	/**
	 * An element in y must be greater than this value to be included. The default
	 * is 5, unless specified otherwise.
	 */
	private double cutoff = 5;
	/**
	 * Measured values.
	 */
	private double[] x;
	/**
	 * Expected values.
	 */
	private double[] y;
	/**
	 * Resulting Chi-Square test statistic.
	 */
	private double chsq;
	/**
	 * Resulting p-value.
	 */
	private double pValue;
	/**
	 * Resulting G test statistic.
	 */
	private double g;
	/**
	 * 
	 */
	private double kl;

	/**
	 * Creates a new TestVector.
	 */
	public TestVector() {
		super();
	}

	/**
	 * Get the length of the two arrays.
	 * 
	 * @return
	 */
	public int getNvec() {
		return nvec;
	}

	/**
	 * Sets the length of the two arrays and also sets {@code ndof} to
	 * {@code nvec - 1}.
	 * 
	 * @param nvec
	 */
	public void setNvec(int nvec) {
		this.nvec = nvec;
		ndof = nvec - 1;
		x = new double[nvec];
		y = new double[nvec];
		chsq = -1;
		g = 0;
		kl = -1;
		pValue = -1;
	}

	/**
	 * Gets the number of degrees of freedom
	 * 
	 * @return
	 */
	public int getNdof() {
		return ndof;
	}

	/**
	 * Sets the number of degrees of freedom. Setting it to zero indicates that this
	 * value is currently unkown must be figured out during evaluation. The cutoff
	 * must have been specified or else its default value will be used.
	 * 
	 * @param ndof
	 * @see #setCutoff(double)
	 */
	public void setNdof(int ndof) {
		this.ndof = ndof;
	}

	/**
	 * Gets the current cutoff-value for expected values.
	 * 
	 * @return
	 */
	public double getCutoff() {
		return cutoff;
	}

	/**
	 * Sets the cutoff for expected values.
	 * 
	 * @param cutoff
	 */
	public void setCutoff(double cutoff) {
		this.cutoff = cutoff;
	}

	/**
	 * Gets the array of measured values.
	 * 
	 * @return
	 */
	public double[] getX() {
		return x;
	}

	/**
	 * Gets the array of expected values.
	 * 
	 * @return
	 */
	public double[] getY() {
		return y;
	}

	/**
	 * Gets the chi-square value.
	 * 
	 * @return
	 */
	public double getChsq() {
		return chsq;
	}

	/**
	 * Gets the g value.
	 * 
	 * @return
	 */
	public double getG() {
		return g;
	}

	/**
	 * Gets the resulting p-value.
	 * 
	 * @return
	 */
	public double getpValue() {
		return pValue;
	}

	/**
	 * Gets the resulting KL-divergence.
	 * 
	 * @return
	 */
	public double getKl() {
		return kl;
	}

	/**
	 * Scales the expected values in order to make their sums match the one of
	 * measured values.
	 */
	public void equalize() {
		double sumX = 0, sumY = 0;
		for (int i = 0; i < nvec; i++) {
			sumX += x[i];
			sumY += y[i];
		}
		double ratio = sumX / sumY;
		for (int i = 0; i < nvec; i++) {
			y[i] *= ratio;
		}
	}

	/**
	 * Evaluates the measured data against the expected data.
	 * 
	 * @return If the evaluation was successful.
	 */
	public boolean evaluate() {
		if (evaluateChiSquareTest()) {
			return true;
		}
		equalize();
		if (evaluateGTest()) {
			return true;
		}
		return evaluateKLDivergence();
	}

	/**
	 * Evaluates the measured data against the expected data using the
	 * Chi-Square-Test.
	 * 
	 * @return If the evaluation was successful.
	 */
	public boolean evaluateChiSquareTest() {
		chsq = 0;
		boolean calcNDOF = ndof == 0;
		int indexTail = -1;
		for (int i = 0; i < nvec; i++) {
			if (y[i] >= cutoff) {
				chsq += (x[i] - y[i]) * (x[i] - y[i]) / y[i];
				if (calcNDOF) {
					ndof++;
				}
			} else {
				if (indexTail == -1) {
					indexTail = i;
				} else {
					x[indexTail] += x[i];
					y[indexTail] += y[i];
				}
			}
		}
		if (indexTail != -1) {
			if (y[indexTail] >= cutoff) {
				chsq += (x[indexTail] - y[indexTail]) * (x[indexTail] - y[indexTail]) / y[indexTail];
				if (calcNDOF) {
					ndof++;
				}
			}
		}
		if (calcNDOF) {
			ndof--;
		}
		pValue = Math.min(Math.max(1 - Functions.cdfChiSquare(ndof, chsq), 0), 1);
		if (Double.isNaN(pValue)) {
			pValue = 0.5;
			return false;
		}
		return true;
	}

	/**
	 * Evaluates the measured data against the expected data using the G-Test.
	 * 
	 * @return If the evaluation was successful.
	 */
	public boolean evaluateGTest() {
		g = 0;
		boolean calcNDOF = ndof == 0;
		int indexTail = -1;
		for (int i = 0; i < nvec; i++) {
			if (y[i] > 0 && x[i] > 0) {
				if (calcNDOF) {
					ndof++;
				}
				g += x[i] * Math.log(x[i] / y[i]);
			} else {
				if (indexTail == -1) {
					indexTail = i;
				} else {
					x[indexTail] += x[i];
					y[indexTail] += y[i];
				}
			}
		}
		if (indexTail != -1) {
			if (y[indexTail] > 0 && x[indexTail] > 0) {
				ndof++;
				g += x[indexTail] * Math.log(x[indexTail] / y[indexTail]);
			}
		}
		g *= 2;
		if (calcNDOF) {
			ndof--;
		}
		pValue = Math.min(Math.max(1 - Functions.cdfChiSquare(ndof, g), 0), 1);
		if (Double.isNaN(pValue)) {
			pValue = 0.5;
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @return Always true, as {@code NaN} values are not possible.
	 */
	public boolean evaluateKLDivergence() {
		double sumX = 0;
		for (double p : x) {
			sumX += p;
		}
		kl = 0;
		for (int i = 0; i < nvec; i++) {
			if (x[i] > 0 && y[i] > 0) {
				kl += x[i] / sumX * Math.log(x[i] / y[i]);
			}
		}
		pValue = 1 - Math.exp(-kl);
		return true;
	}

	/**
	 * Evaluates the measured data against the expected data using the absolute are
	 * mismatch.
	 * 
	 * @param t
	 * @return Always true, as {@code NaN} values are not possible.
	 */
	public boolean evaluateAbsoluteAreaMismatch(Transform t) {
		double sumX = 0, sumY = 0;
		for (int i = 0; i < nvec; i++) {
			sumX += x[i];
			sumY += y[i];
		}
		double mismatch = 0, tmp;
		if (t == null) {
			t = Transform.IDENTITY;
		}
		for (int i = 0; i < nvec; i++) {
			tmp = Math.abs(x[i] / sumX - y[i] / sumY);
			tmp = t.forward(tmp);
			mismatch += tmp;
		}
		pValue = 1 - t.backward(mismatch);
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(chsq);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(cutoff);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(g);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ndof;
		result = prime * result + nvec;
		temp = Double.doubleToLongBits(pValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + Arrays.hashCode(x);
		result = prime * result + Arrays.hashCode(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TestVector)) {
			return false;
		}
		TestVector other = (TestVector) obj;
		if (Double.doubleToLongBits(chsq) != Double.doubleToLongBits(other.chsq)) {
			return false;
		}
		if (Double.doubleToLongBits(cutoff) != Double.doubleToLongBits(other.cutoff)) {
			return false;
		}
		if (Double.doubleToLongBits(g) != Double.doubleToLongBits(other.g)) {
			return false;
		}
		if (ndof != other.ndof) {
			return false;
		}
		if (nvec != other.nvec) {
			return false;
		}
		if (Double.doubleToLongBits(pValue) != Double.doubleToLongBits(other.pValue)) {
			return false;
		}
		if (!Arrays.equals(x, other.x)) {
			return false;
		}
		if (!Arrays.equals(y, other.y)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestVector [nvec=");
		builder.append(nvec);
		builder.append(", ndof=");
		builder.append(ndof);
		builder.append(", cutoff=");
		builder.append(cutoff);
		builder.append(", x=");
		builder.append(Arrays.toString(x));
		builder.append(", y=");
		builder.append(Arrays.toString(y));
		builder.append(", chsq=");
		builder.append(chsq);
		builder.append(", pValue=");
		builder.append(pValue);
		builder.append(", g=");
		builder.append(g);
		builder.append(", kl=");
		builder.append(kl);
		builder.append("]");
		return builder.toString();
	}
}