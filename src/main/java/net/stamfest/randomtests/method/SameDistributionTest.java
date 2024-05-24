package net.stamfest.randomtests.method;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class SameDistributionTest {
    public static String runSameDistributionTest(Bits sequenceInBinaryRepresentation) {
        boolean sequencePassedTheTest = true;
        double chiSquaredValueTheoretical = 0;
        double chi2ValueExperimental = 0;

        ArrayList<Integer> arrayListOfBinarySequence = new ArrayList<>();
        sequenceInBinaryRepresentation.forEach(arrayListOfBinarySequence::add);
        long N = arrayListOfBinarySequence.size();

        long n = arrayListOfBinarySequence.stream().filter(x -> x == 1).count();
        double p = (double) n / N;

        long[] ni = new long[10];

        for(int i = 0; i < 10; i++) {
            ArrayList<Integer> newBinarySeq = new ArrayList<>();
            for(int j = (int) (i * N / 10); j < (i + 1) * N / 10; j++) {
                newBinarySeq.add(arrayListOfBinarySequence.get(j));
            }
            ni[i] = newBinarySeq.stream().filter(x -> x == 1).count();
        }

        for(int i = 0; i < 10; i++) {
            chi2ValueExperimental += (ni[i] - p * N / 10) * (ni[i] - p * N / 10) * 10 / (p * N);
        }

        ChiSquaredDistribution chiSquaredDistribution =
                new ChiSquaredDistribution(9);
        chiSquaredValueTheoretical = chiSquaredDistribution.inverseCumulativeProbability(1 - 0.001);

        if(chi2ValueExperimental > chiSquaredValueTheoretical)
            sequencePassedTheTest = false;

        StringBuilder result = new StringBuilder();

        if(sequencePassedTheTest) {
            result.append("YES!\n");
            result.append("Выборка прошла проверку на одинаковую распределённость\n");
        }
        else {
            result.append("NO!\n");
            result.append("Выборка НЕ прошла проверку на одинаковую распределённость\n");
        }
        result.append("ХИ квадрат = ").append(chi2ValueExperimental)
                .append(", пороговое значение = ").append(chiSquaredValueTheoretical).append("\n");

        double pValue = 1 - chiSquaredDistribution.cumulativeProbability(chi2ValueExperimental);
        result.append("P-value = ").append(pValue).append("\n");
        return result.toString();
    }

    public static int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>= 1;
        }
        return count;
    }

    public static void main(String[] args) throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                Files.newInputStream(Path.of("data.e"))), 1000000);

        runSameDistributionTest(bits);

    }
}
