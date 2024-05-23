package net.stamfest.randomtests.method;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Permutation {

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

        ArrayList<Integer> integers = new ArrayList<>();
        bits.forEach(integers::add);
        long N = integers.size();

        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> newSeq = new ArrayList<>();
            for (int j = (int) (i * N / 10); j < (i + 1) * N / 10; j++) {
                newSeq.add(integers.get(j));
            }
            int[] runsRes = runsTest(newSeq);
            double derRes = maxCumDev(newSeq);
            int[] runsTrendRes = runsTestWithTrend(newSeq);
            double covRes = covarianceTest(newSeq);
            int[] colisRes = collisionsTest(newSeq);
            System.out.println(runsRes[0] + ", " + runsRes[1] + "  " + derRes + "  " +
                    runsTrendRes[0] + ", " + runsTrendRes[1] + ", " + runsTrendRes[2] + "  " + covRes +
                    "  " + colisRes[0] + ", " + colisRes[1]);
        }


    }


    public static int[] runsTest(ArrayList<Integer> integers) {
        double median = 0.5;
        ArrayList<Integer> newSeq = new ArrayList<>();
        for (int i : integers) {
            if (i == 0) {
                newSeq.add(-1);
            } else {
                newSeq.add(1);
            }
        }

        return new int[]{getLongestSeriesOfInt(newSeq, 1), getLongestSeriesOfInt(newSeq, -1)};
    }

    public static int getLongestSeriesOfInt(ArrayList<Integer> list, int el) {
        int currentSeries = 0;
        int longestSeries = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == el) {
                currentSeries++;
                if (currentSeries > longestSeries) {
                    longestSeries = currentSeries;
                }
            } else {
                currentSeries = 0;
            }
        }

        return longestSeries;
    }

    public static double maxCumDev(ArrayList<Integer> integers) {
        double m = 0;
        for (int x : integers) {
            m += x;
        }
        m /= integers.size();

        ArrayList<Double> DI = new ArrayList<>();
        for (int i = 0; i < integers.size(); i++) {
            double d = 0;
            for (int j = 0; j < i; j++) {
                d += integers.get(j) - i * m;
            }
            DI.add(Math.abs(d));
        }
        return DI.stream().mapToDouble(Double::doubleValue).max().orElse(DI.get(0));
    }

    public static int[] runsTestWithTrend(ArrayList<Integer> integers) {
        ArrayList<Integer> newSeq = new ArrayList<>();
        int si = 0;
        int si1 = 0;
        int add = 0;
        for (int i = 0; i < integers.size() - 1; i++) {
            si = integers.get(i);
            si1 = integers.get(i + 1);
            if (si > si1) {
                add = -1;
            } else if (si < si1) {
                add = 1;
            } else {
                add = 0;
            }
            newSeq.add(add);
        }
        removeLeadingZeros(newSeq);
        int C0 = (int) integers.stream().filter(x -> x == 0).count();
        int C1 = integers.size() - C0;
        int CX = Math.max(C0, C1);
        return new int[]{countSeries(newSeq), calculateMaxSeriesLength(newSeq), CX};
    }

    public static void removeLeadingZeros(List<Integer> list) {
        while (!list.isEmpty() && list.get(0) == 0) {
            list.remove(0);
        }
    }

    public static int countSeries(List<Integer> list) {
        int seriesCount = 0;
        boolean inSeries = false;

        for (int num : list) {
            if (num == 1 && !inSeries) {
                seriesCount++;
                inSeries = true;
            } else if (num == -1) {
                inSeries = false;
            } else if (inSeries) {
                seriesCount++;
            }
        }

        return seriesCount;
    }

    public static int calculateMaxSeriesLength(List<Integer> list) {
        int maxLength = 0;
        int currentLength = 0;
        boolean inSeries = false;

        for (int num : list) {
            if (num == 1 && !inSeries) {
                currentLength++;
                inSeries = true;
            } else if (num == -1) {
                inSeries = false;
                maxLength = Math.max(maxLength, currentLength);
                currentLength = 0;
            } else if (inSeries) {
                currentLength++;
            }
        }

        maxLength = Math.max(maxLength, currentLength);

        return maxLength;
    }


    public static double covarianceTest(ArrayList<Integer> integers) {
        double m = 0;
        for (int x : integers) {
            m += x;
        }
        m /= integers.size();

        double res = 0;
        for (int i = 0; i < integers.size() - 1; i++) {
            res += (integers.get(i) - m) * (integers.get(i + 1) - m);
        }
        res /= integers.size() - 1;
        return res;
    }

    public static int[] collisionsTest(ArrayList<Integer> integers) {
        ArrayList<Integer> newSeq = new ArrayList<>();
        for (int i = 0; i < integers.size() - 8; i += 8) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                s.append(integers.get(i + j).toString());
            }
            newSeq.add(Integer.parseInt(s.toString(), 2));
        }

        ArrayList<Integer> C = new ArrayList<>();
        int p = 0;
        int i = -1;
        while (p < newSeq.size()) {
            i = findFirstRepeatingIndex(newSeq.subList(p, newSeq.size()));
            if (i == -1)
                break;
            C.add(i);
            p = p + i + 1;
        }

        int max = C.stream().mapToInt(Integer::intValue).max().orElse(0);
        int min = C.stream().mapToInt(Integer::intValue).min().orElse(0);
        return new int[] {min, max};
    }

    public static int findFirstRepeatingIndex(List<Integer> sequence) {
        HashMap<Integer, Integer> observationCount = new HashMap<>();

        for (int i = 0; i < sequence.size(); i++) {
            int observation = sequence.get(i);

            if (observationCount.containsKey(observation)) {
                return i - observationCount.get(observation) + 1;
            } else {
                observationCount.put(observation, i);
            }
        }

        return -1;
    }

}
