package net.stamfest.randomtests.dieAgain.test;

import net.stamfest.randomtests.dieAgain.util.*;
import net.stamfest.randomtests.dieAgain.util.randoms.PCGHash;
import net.stamfest.randomtests.dieAgain.util.Dispenser;
import net.stamfest.randomtests.dieAgain.util.TestData;

import java.util.Arrays;
import java.util.Random;
/**
 * 
 * @author Christian Schürhoff
 *
 */
public class BinaryMatrixRankTest implements ITest {
	public static final TestData BINARY_MATRIX_RANK;
	static {
		BINARY_MATRIX_RANK = new TestData();
		BINARY_MATRIX_RANK.setName("Binary Matrix Rank Test");
		BINARY_MATRIX_RANK.setDescription("");
		BINARY_MATRIX_RANK.setNkps(1);
		BINARY_MATRIX_RANK.settSamplesStandard(100000);
		BINARY_MATRIX_RANK.setpSamplesStandard(100);
		BINARY_MATRIX_RANK.setTestMethod(new BinaryMatrixRankTest());
	}

	public BinaryMatrixRankTest() {
		super();
	}

	@Override
	public void runTestOn(Random rng, StandardTest... parameters) {
		Dispenser bitSource = new Dispenser();
		bitSource.setRandom(rng);
		for (StandardTest current : parameters) {
			if (current.getnTuple() == 0) {
				current.setnTuple((byte) 8);
			}
			final byte size = current.getnTuple();
			TestVector pTest = new TestVector();
			pTest.setNvec(size + 1);
			pTest.setCutoff(5);
			for (int index = 0; index <= size; index++) {
				pTest.getY()[index] = expectedCount(current.gettSamples(), size, index);
			}
			boolean[][] binaryMatrix = new boolean[size][size];
			for (int pSample = 0; pSample < current.getpSamples(); pSample++) {
				Arrays.fill(pTest.getX(), 0);
				pTest.setNdof(0);
				for (int tSample = 0; tSample < current.gettSamples(); tSample++) {
					for (boolean[] row : binaryMatrix) {
						for (int i = 0; i < size; i++) {
							row[i] = bitSource.getBits((byte) 1) == 1;
						}
					}
					pTest.getX()[binaryMatrixRank(binaryMatrix)] += 1;
				}
				pTest.evaluate();
				current.getpValues()[pSample] = pTest.getpValue();
			}
			current.getPvLabels()[0] = "Rank Distribution";
			current.evaluate();
		}
	}

	private static int binaryMatrixRank(boolean[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			if (matrix[i][i]) {
				for (int nextRow = i + 1; nextRow < matrix.length; nextRow++) {
					if (matrix[nextRow][i]) {
						for (int col = i; col < matrix.length; col++) {
							matrix[nextRow][col] = matrix[nextRow][col] ^ matrix[i][col];
						}
					}
				}
			} else {
				int swapRow = findRow(i, matrix.length, matrix);
				if (swapRow > i) {
					boolean[] aux = matrix[i];
					matrix[i] = matrix[swapRow];
					matrix[swapRow] = aux;
				}
			}
		}
		for (int i = matrix.length - 1; i >= 0; i--) {
			if (matrix[i][i]) {
				for (int nextRow = i - 1; nextRow >= 0; nextRow--) {
					if (matrix[nextRow][i]) {
						for (int col = i; col < matrix.length; col++) {
							matrix[nextRow][col] = matrix[nextRow][col] ^ matrix[i][col];
						}
					}
				}
			} else {
				int swapRow = findRow(i, -1, matrix);
				if (i > swapRow && swapRow > -1) {
					boolean[] aux = matrix[i];
					matrix[i] = matrix[swapRow];
					matrix[swapRow] = aux;
				}
			}
		}
		int rank = 0;
		for (boolean[] row : matrix) {
			for (int i = 0; i < row.length; i++) {
				if (row[i]) {
					rank++;
					break;
				}
			}
		}
		return rank;
	}

	private static int findRow(int start, int end, boolean[][] matrix) {
		int delta = start <= end ? 1 : -1;
		for (int i = start + delta; i != end; i += delta) {
			if (matrix[i][start]) {
				return i;
			}
		}
		return -1;
	}

	private static double expectedCount(int generatedMatricies, byte matrixSize, int rank) {
		double ret = generatedMatricies * Math.pow(2, rank * (2 * matrixSize - rank) - matrixSize * matrixSize);
		for (int i = 0; i < rank; i++) {
			ret *= Math.pow(1 - Math.pow(2, i - matrixSize), 2) / (1 - Math.pow(2, i - rank));
		}
		return ret;
	}

	@Deprecated
	public static void main(String... args) {
		StandardTest test = BINARY_MATRIX_RANK.createTest(8);
		test.setnTuple((byte) 32);
		TestObserver observer = new TestObserver();
		observer.setTests(test);
		//Thread t = new Thread(observer);
		//t.start();
		BINARY_MATRIX_RANK.getTestMethod().runTestOn(new PCGHash(), test);
		for (int nk = 0; nk < test.getNkps(); nk++) {
			System.out.print(test.getPvLabels()[nk] + "\t\t");
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