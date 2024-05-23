package net.stamfest.randomtests.method;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.special.Erf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Independence {
    public static boolean run(Bits bits) {

        boolean passed = true;
        double chiSquaredValue = 0;
        double chi2 = 0;

        ArrayList<Integer> integers = new ArrayList<>();
        bits.forEach(integers::add);
        long N = integers.size();
        long C0 = integers.stream().filter(x -> x == 0).count();
        long C1 = integers.size() - C0;
        long CX =Math.min(C0, C1);
        double p = (double) C1 / N;

        int finalK = 0;

        List<Double> pvalues = new ArrayList<>();

        for(int k = 2; k < 12; k ++) {
            if(Math.pow((((double) CX)) / N, 4) > (((double)5) / N)) {
                ArrayList<Integer> newSeq = new ArrayList<>();
                for(int i = 0; i < integers.size() - k; i+=k) {
                    StringBuilder s = new StringBuilder();
                    for(int j = 0; j < k; j++) {
                        s.append(integers.get(i + j).toString());
                    }
                    newSeq.add(Integer.parseInt(s.toString(), 2));
                }
                Set<Integer> V = new HashSet<>(newSeq);
                ArrayList<Integer> VArray = new ArrayList<>(V);
                for(int i = 0; i < Math.pow(2, k); i++) {
                    VArray.add(i);
                }
                ArrayList<Integer> WVArray = new ArrayList<>();
                for(int v : VArray) {
                    WVArray.add(hammingWeight(v));
                }

                ArrayList<Double> PV = new ArrayList<>();
                double nk = (double) N / k;
                for(int i = 0; i < VArray.size(); i++) {
                    PV.add(nk * Math.pow(p, WVArray.get(i)) * Math.pow(1 - p, k - WVArray.get(i)));
                }

                chi2 = 0;

                ArrayList<Long> NV = new ArrayList<>();
                for(int v : VArray) {
                    NV.add(newSeq.stream().filter(x -> x == v).count());
                }
                for(int i = 0; i < Math.pow(2, k); i++) {
                    chi2 += (NV.get(i) - PV.get(i)) * (NV.get(i) - PV.get(i)) / PV.get(i);
                }

                ChiSquaredDistribution chiSquaredDistribution =
                        new ChiSquaredDistribution(Math.pow(2, k) - 1);
                chiSquaredValue = chiSquaredDistribution.inverseCumulativeProbability(1 - 0.001);

                pvalues.add(1 - chiSquaredDistribution.cumulativeProbability(chi2));

                finalK = k;

                if(chi2  > chiSquaredValue) {
                    passed = false;
                    break;
                }
            }
        }

        if(passed) {
            System.out.println("Выборка прошла проверку на независимость");
        }
        else {
            System.out.println("Выборка НЕ прошла проверку на независимость");
        }
        System.out.println("ХИ квадрат = " + chi2 + ", пороговое значение = " + chiSquaredValue);

        System.out.println("P-values:");
        for(int i = 0; i < pvalues.size(); i++) {
            System.out.println("k = " + (i +2) + "\t: " +pvalues.get(i));
        }

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
