package net.stamfest.randomtests.dieAgain.util;

import java.util.Random;

/**
 * Wraps a {@link Random}-object and stores the next 3 integers internally. It
 * can than dispense any number of bits between 0 and 64 from the stored
 * integers. After providing those bits, it can be instructed to stay where it
 * is or move any number of bits forward, where the latter one is destructive:
 * It can not be reversed.
 * 
 * @author Christian Schürhoff
 */
public class Dispenser {
	/**
	 * Turns a signed integer into an unsigned one.
	 */
	public static final long MASK = 0xFFFFFFFFL;
	/**
	 * The {@link Random}-instance to wrap.
	 */
	Random random;
	/**
	 * The next 3 generated integers.
	 */
	int[] generated;
	/**
	 * Bits in the first integer. The next two integers are always the full 32 bits.
	 */
	byte availableBits = 0;



	/**
	 * Creates a new Dispenser without a source of random bits.
	 */
	public Dispenser() {
		super();
	}

	/**
	 * Creates a new Dispenser with a source of random bits.
	 * 
	 * @param rng The source of randomness.
	 */
	public Dispenser(Random rng) {
		super();
		setRandom(rng);
	}

	public Random getRandom() {
		return random;
	}

	/**
	 * Sets the random to use, also replaces the {@link generated}-array with three
	 * new values and also sets {@link #availableBits} to 32.
	 * 
	 * @param random
	 */
	public void setRandom(Random random) {
		this.random = random;
		generated = new int[] { random.nextInt(), random.nextInt(), random.nextInt() };
		availableBits = 32;
	}

	/**
	 * 
	 * @param bits How long the output is in bits and how many bits to move forward.
	 *             Values between 0 and 64 are accepted.
	 * @return
	 */
	public long getBits(byte bits) {
		return getBits(bits, bits);
	}

	/**
	 * 
	 * @param bits       How long the output is in bits. Values between 0 and 64 are
	 *                   accepted.
	 * @param bitsToMove How many bits to move forward in the bitstream of the
	 *                   {@link #Random}-object.
	 * @return
	 * @throws ArithmeticException If the second parameter is
	 *                             {@link Long#MIN_VALUE}.
	 */
	public long getBits(byte bits, long bitsToMove) {
		if (0 <= bits && bits <= 64) {
			bitsToMove = Math.absExact(bitsToMove);
			long ret = 0;
			if (bits < availableBits) {
				ret = (generated[0] >>> (32 - bits));
			} else {
				ret = (generated[0] >>> 32 - availableBits) & MASK;
				bits -= availableBits;
				if (bits > 32) {
					ret = (ret << 32) | (generated[1] & 0xFFFFFFFFL);
					bits -= 32;
					if (bits > 0) {
						ret = (ret << bits) | (generated[2] >>> (32 - bits));
					}
				} else {
					ret = (ret << bits) | ((generated[1] >>> (32 - bits)) & ((1L << bits) - 1));
				}
			}
			if (bitsToMove > 0) {
				if (bitsToMove < availableBits) {
					generated[0] <<= bitsToMove;
					availableBits -= bitsToMove;
				} else {
					bitsToMove -= availableBits;
					shift();
					for (long drift = bitsToMove / 32; drift > 0; drift--) {
						shift();
					}
					bitsToMove %= 32;
					if (bitsToMove > 0) {
						generated[0] <<= bitsToMove;
						availableBits -= bitsToMove;
					}
				}
			}
			return ret;
		} else {
			throw new IllegalArgumentException("Can not provide " + bits + " Bits!");
		}
	}

	/**
	 * Creates a double value in the same manner as all {@link Random}-instances
	 * would: By taking the first 26 and then 27 bits of two consecutive integers
	 * 
	 * @return
	 * @see Random#nextDouble()
	 */
	public double getStandardDouble() {
		long ret = getBits((byte) 26, 32);
		ret <<= 27;
		ret |= getBits((byte) 27, 32);
		return ret / (double) (1L << 53);
	}

	/**
	 * 
	 * @param bits
	 * @return
	 */
	public double getBitsAsDouble(byte bits) {
		return getBitsAsDouble(bits, bits);
	}

	/**
	 * 
	 * @param bits
	 * @param bitsToMove
	 * @return
	 */
	public double getBitsAsDouble(byte bits, long bitsToMove) {
		if (bits <= 63) {
			long raw = getBits(bits, bitsToMove);
			double mult = Math.pow(2.0, -bits);
			return raw * mult;
		} else {
			throw new IllegalArgumentException("The highest order bit must be 0 at all times!");
		}
	}

	/**
	 * 
	 * @param bits
	 * @return
	 */
	public byte getBitsAsByte(byte bits) {
		return getBitsAsByte(bits, bits);
	}

	/**
	 * 
	 * @param bits
	 * @param bitsToMove
	 * @return
	 */
	public byte getBitsAsByte(byte bits, long bitsToMove) {
		if (bits <= 8) {
			return (byte) getBits(bits, bitsToMove);
		} else {
			throw new IllegalArgumentException("Can at most provide 8 Bits!");
		}
	}

	/**
	 * 
	 * @param bits
	 * @return
	 */
	public short getBitsAsShort(byte bits) {
		return getBitsAsShort(bits, bits);
	}

	/**
	 * 
	 * @param bits
	 * @param bitsToMove
	 * @return
	 */
	public short getBitsAsShort(byte bits, long bitsToMove) {
		if (bits <= 16) {
			return (short) getBits(bits, bitsToMove);
		} else {
			throw new IllegalArgumentException("Can at most provide 16 Bits!");
		}
	}

	/**
	 * 
	 * @param bits
	 * @return
	 */
	public int getBitsAsInteger(byte bits) {
		return getBitsAsInteger(bits, bits);
	}

	/**
	 * 
	 * @param bits
	 * @param bitsToMove
	 * @return
	 */
	public int getBitsAsInteger(byte bits, long bitsToMove) {
		if (bits <= 32) {
			return (int) getBits(bits, bitsToMove);
		} else {
			throw new IllegalArgumentException("Can at most provide 32 Bits!");
		}
	}

	/**
	 * Returns a float value by taking first 24 bits of an integer.
	 * 
	 * @return
	 * @see Random#nextFloat()
	 */
	public float getStandardFloat() {
		return getBitsAsFloat((byte) 24, 32);
	}

	/**
	 * 
	 * @param bits
	 * @return
	 */
	public float getBitsAsFloat(byte bits) {
		return getBitsAsFloat(bits, bits);
	}

	/**
	 * 
	 * @param bits
	 * @param bitsToMove
	 * @return
	 */
	public float getBitsAsFloat(byte bits, long bitsToMove) {
		if (bits <= 31) {
			int raw = getBitsAsInteger(bits, bitsToMove);
			double mult = Math.pow(2.0, -bits);
			return (float) (raw * mult);
		} else {
			throw new IllegalArgumentException("The highest order bit must be 0 at all times!");
		}
	}

	/**
	 * Shift the buffer array over by one, get the next integer and sets
	 * {@link #availableBits} to 32.
	 */
	void shift() {
		generated[0] = generated[1];
		generated[1] = generated[2];
		generated[2] = random.nextInt();
		availableBits = 32;
	}
}