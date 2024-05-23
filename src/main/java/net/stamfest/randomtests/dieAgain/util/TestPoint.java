package net.stamfest.randomtests.dieAgain.util;

/**
 * The TestPoints is used for statistical tests, which only generate a single
 * value. According to the central limit theorem, any sum of many independent
 * and maybe even different distributions can be approximated by a normal
 * distribution.
 * 
 * @author Christian Schürhoff
 *
 */
public class TestPoint {
	/**
	 * How many generated values went into forming x.
	 */
	private int points;
	/**
	 * the resulting p-value for a two sided test.
	 */
	private double p;
	/**
	 * The measured value.
	 */
	private double x;
	/**
	 * The expected value.
	 */
	private double y;
	/**
	 * The standard deviation.
	 */
	private double sigma;
	/**
	 * The resulting p-value for a single sided test.
	 */
	private double pValue;

	public TestPoint() {
		super();
	}

	/**
	 * Gets the number of data points that went into forming X.
	 * 
	 * @return
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Sets the number of data points that went into forming X.
	 * 
	 * @param points
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Gets the p-value of a half-normal distribution
	 * 
	 * @return
	 */
	public double getP() {
		return p;
	}

	/**
	 * Gets the measured value.
	 * 
	 * @return
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the measured value.
	 * 
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the expected value.
	 * 
	 * @return
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the expected value.
	 * 
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Gets the standard deviation from the expected value.
	 * 
	 * @return
	 */
	public double getSigma() {
		return sigma;
	}

	/**
	 * Sets the standard deviation from the expected value.
	 * 
	 * @param sigma
	 */
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}

	/**
	 * Gets the p-value of a normal distribution.
	 * 
	 * @return
	 */
	public double getpValue() {
		return pValue;
	}

	/**
	 * Evaluates the the measured value against the expected value and standard
	 * deviation. {@code X} is expected to follow a normal distribution with mean
	 * {@code Y} and standard deviation {@code sigma}.
	 */
	public void evaluate() {
		pValue = 1 - Functions.cdfStandardNormal((y - x) / sigma);
		p = 1 - 2 * Math.abs(pValue - 0.5);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(p);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(pValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + points;
		temp = Double.doubleToLongBits(sigma);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TestPoint)) {
			return false;
		}
		TestPoint other = (TestPoint) obj;
		if (Double.doubleToLongBits(p) != Double.doubleToLongBits(other.p)) {
			return false;
		}
		if (Double.doubleToLongBits(pValue) != Double.doubleToLongBits(other.pValue)) {
			return false;
		}
		if (points != other.points) {
			return false;
		}
		if (Double.doubleToLongBits(sigma) != Double.doubleToLongBits(other.sigma)) {
			return false;
		}
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TestPoint [points=");
		builder.append(points);
		builder.append(", p=");
		builder.append(p);
		builder.append(", x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", sigma=");
		builder.append(sigma);
		builder.append(", pValue=");
		builder.append(pValue);
		builder.append("]");
		return builder.toString();
	}
}