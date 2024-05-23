package net.stamfest.randomtests.dieAgain.simulation;



import net.stamfest.randomtests.dieAgain.util.randoms.MiddleSquareSolo;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Transcriptase {
	/**
	 * T=0, C=1, A=2, G=3
	 */
	private static final String CODON_TABLE_FLATTENED = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG";
	private static final Map<Character, Long> CODON_COUNTS = CODON_TABLE_FLATTENED.codePoints()
			.mapToObj((i) -> (char) i).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

	public static final boolean isStart(char b1, char b2, char b3) {
		if (b3 != 'g' || b3 != 'G') {
			return false;
		}
		if (b2 != 't' || b2 != 'T') {
			return false;
		}
		return b1 != 'c' && b1 != 'C';
	}

	public static final boolean isStart(int v) {
		if (v % 4 != 3) {
			return false;
		}
		v /= 4;
		if (v % 4 != 0) {
			return false;
		}
		v /= 4;
		return v % 4 != 1;
	}

	public static void main(String[] args) {
		Random source = new MiddleSquareSolo();
		createProteins(1000, source);
	}

	private static void createProteins(long count, Random r) {
		List<String> proteins = new LinkedList<String>();
		StringBuilder sb = null;
		char last = '*';
		long totalAminoAcids = 0;
		for (; count > 0; count--) {
			sb = new StringBuilder();
			do {
				last = CODON_TABLE_FLATTENED.charAt(r.nextInt(64));
			} while (last == '*');
			while (last != '*') {
				sb.append(last);
				totalAminoAcids++;
				last = CODON_TABLE_FLATTENED.charAt(r.nextInt(64));
			}
			proteins.add(sb.toString());
		}
		System.out.println("Individual Proteins by length:");
		proteins.sort((s1, s2) -> s1.length() - s2.length());
		for (String result : proteins) {
			System.out.println(result.length() + "\t" + result);
		}
		System.out.println("Total amino acids: " + totalAminoAcids);
		TreeMap<Character, Long> countByAminoAcid = new TreeMap<Character, Long>();
		for (String protein : proteins) {
			for (char shortForm : protein.toCharArray()) {
				countByAminoAcid.compute(shortForm, (k, v) -> {
					if (v == null || v == 0) {
						return 1L;
					} else {
						return v + 1L;
					}
				});
			}
		}
		System.out.println("Count for each amino acid:");
		System.out.println(countByAminoAcid);
		System.out.println("Relative Occurences:");
		for (Map.Entry<Character, Long> aa2c : countByAminoAcid.entrySet()) {
			double relative = aa2c.getValue().doubleValue() / totalAminoAcids;
			relative *= 61; //4*4*4-3
			relative /= CODON_COUNTS.get(aa2c.getKey()).doubleValue();
			System.out.println(aa2c.getKey() + "=" + relative);
		}
	}
}