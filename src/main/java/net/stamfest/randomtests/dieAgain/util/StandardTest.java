package net.stamfest.randomtests.dieAgain.util;

import java.util.Arrays;

public class StandardTest {
	/**
	 * Number of test statistics created per run
	 */
	private int nkps = 1;
	/**
	 * Number of samples per test if applicable
	 */
	private int tSamples = 1;
	/**
	 * Number of test runs per final p-value;
	 */
	private int pSamples = 1;
	/**
	 * Number of Bits in nTuples being tested. Usually in the range from 1 to 64. 0
	 * means unset and the test should use its default.
	 */
	private byte nTuple = 0;
	/**
	 * Array of length {@link #pSamples}*{@link #nkps} to hold test p-values.
	 */
	private double[] pValues;
	/**
	 * Holds {@link nkps} labels for p-values.
	 */
	private String[] pvLabels;
	/**
	 * Final KS p-value claculated from the runs of many tests. Any value outside
	 * [0;1] means unset.
	 */
	private double ks_pValue = -1.0;
	/**
	 * Extra Variables
	 */
	private double[] xyz;

	/**
	 * Creates a new StandardTest.
	 */
	public StandardTest() {
		super();
	}

	/**
	 * @return The number of p-values created in a single run.
	 */
	public int getNkps() {
		return nkps;
	}

	/**
	 * Sets the number of p-values created in a single run.
	 * 
	 * @param nkps
	 */
	public void setNkps(int nkps) {
		this.nkps = nkps;
		pValues = new double[pSamples * nkps];
		pvLabels = new String[nkps];
		for (int i = 0; i < pValues.length; i++) {
			pValues[i] = -1.0;
		}
	}

	/**
	 * @return Sample-count inside a single run.
	 */
	public int gettSamples() {
		return tSamples;
	}

	/**
	 * Sets the sample count inside a single run.
	 * 
	 * @param tSamples
	 */
	public void settSamples(int tSamples) {
		this.tSamples = tSamples;
	}

	/**
	 * 
	 * @return produced p-values per run.
	 */
	public int getpSamples() {
		return pSamples;
	}

	/**
	 * Sets the amount of runs.
	 * 
	 * @param pSamples
	 */
	public void setpSamples(int pSamples) {
		this.pSamples = pSamples;
		pValues = new double[pSamples * nkps];
		for (int i = 0; i < pValues.length; i++) {
			pValues[i] = -1.0;
		}
	}

	/**
	 * 
	 * @return the amount of bits to use.
	 */
	public byte getnTuple() {
		return nTuple;
	}

	/**
	 * Sets the number of bits to analyze.
	 * 
	 * @param nTuple
	 */
	public void setnTuple(byte nTuple) {
		this.nTuple = nTuple;
	}

	public double[] getpValues() {
		return pValues;
	}

	public String[] getPvLabels() {
		return pvLabels;
	}

	public double getKs_pValue() {
		return ks_pValue;
	}

	public double[] getXyz() {
		return xyz;
	}

	public void setXyz(double... xyz) {
		this.xyz = xyz;
	}

	public void evaluate() {
		ks_pValue = Functions.ksTest(pValues);
	}

	/**
	 * A test is weak, if the final evaluation of produced p-values has resulted in
	 * a non-uniformity of 0.005 or less.
	 * 
	 * @return {@code true} if the PRNG produces bits which are weakly non-random.
	 */
	public boolean isWeak() {
		return ks_pValue < 0.005;
	}

	/**
	 * A test has failed, if the final evaluation or produced p-values has resulted
	 * in a non-uniformity of 0.000001 or less.
	 * 
	 * @return {@code true} if the PRNG produces bits which are not random.
	 */
	public boolean hasFailed() {
		return ks_pValue < 0.000001;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(ks_pValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + nTuple;
		result = prime * result + nkps;
		result = prime * result + pSamples;
		result = prime * result + Arrays.hashCode(pValues);
		result = prime * result + Arrays.hashCode(pvLabels);
		result = prime * result + tSamples;
		result = prime * result + Arrays.hashCode(xyz);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof StandardTest)) {
			return false;
		}
		StandardTest other = (StandardTest) obj;
		if (Double.doubleToLongBits(ks_pValue) != Double.doubleToLongBits(other.ks_pValue)) {
			return false;
		}
		if (nTuple != other.nTuple) {
			return false;
		}
		if (nkps != other.nkps) {
			return false;
		}
		if (pSamples != other.pSamples) {
			return false;
		}
		if (!Arrays.equals(pValues, other.pValues)) {
			return false;
		}
		if (!Arrays.equals(pvLabels, other.pvLabels)) {
			return false;
		}
		if (tSamples != other.tSamples) {
			return false;
		}
		if (!Arrays.equals(xyz, other.xyz)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StandardTest [nkps=");
		builder.append(nkps);
		builder.append(", tSamples=");
		builder.append(tSamples);
		builder.append(", pSamples=");
		builder.append(pSamples);
		builder.append(", nTuple=");
		builder.append(nTuple);
		builder.append(", pValues=");
		builder.append(Arrays.toString(pValues));
		builder.append(", pvLabels=");
		builder.append(Arrays.toString(pvLabels));
		builder.append(", ks_pValue=");
		builder.append(ks_pValue);
		builder.append(", xyz=");
		builder.append(Arrays.toString(xyz));
		builder.append("]");
		return builder.toString();
	}
}