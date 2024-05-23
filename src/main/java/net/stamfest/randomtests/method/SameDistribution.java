package net.stamfest.randomtests.method;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SameDistribution {
    public static boolean run(Bits bits) {
        boolean passed = true;
        double chiSquaredValue = 0;
        double chi2 = 0;

        ArrayList<Integer> integers = new ArrayList<>();
        bits.forEach(integers::add);
        long N = integers.size();

        long n = integers.stream().filter(x -> x == 1).count();
        double p = (double) n / N;

        long[] ni = new long[10];

        for(int i = 0; i < 10; i++) {
            ArrayList<Integer> newSeq = new ArrayList<>();
            for(int j = (int) (i * N / 10); j < (i + 1) * N / 10; j++) {
                newSeq.add(integers.get(j));
            }
            ni[i] = newSeq.stream().filter(x -> x == 1).count();
        }

        for(int i = 0; i < 10; i++) {
            chi2 += (ni[i] - p * N / 10) * (ni[i] - p * N / 10) * 10 / (p * N);
        }

        ChiSquaredDistribution chiSquaredDistribution =
                new ChiSquaredDistribution(9);
        chiSquaredValue = chiSquaredDistribution.inverseCumulativeProbability(1 - 0.001);

        if(chi2 > chiSquaredValue)
            passed = false;

        if(passed) {
            System.out.println("Выборка прошла проверку на одинаковую распределённость");
        }
        else {
            System.out.println("Выборка НЕ прошла проверку на одинаковую распределённость");
        }
        System.out.println("ХИ квадрат = " + chi2 + ", пороговое значение = " + chiSquaredValue);

        double pValue = 1 - chiSquaredDistribution.cumulativeProbability(chi2);
        System.out.println("P-value = " + pValue);
        return passed;
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

        run(bits);

    }
}
