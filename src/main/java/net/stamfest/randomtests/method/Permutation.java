package net.stamfest.randomtests.method;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Permutation {

    private static class S {
        int compRes;         // 1
        int runsRes1;        // 1
        int runsRes2;        // 1
        double derRes;       // 1
        int runsTrendRes1;   // 1
        int runsTrendRes2;   // 1
        int runsTrendRes3;   // 1
        double covRes;       // 1
        int colisRes1;       // 1
        int colisRes2;       // 1

        //                     10


        public S(int compRes, int runsRes1, int runsRes2, double derRes, int runsTrendRes1, int runsTrendRes2,
                 int runsTrendRes3, double covRes, int colisRes1, int colisRes2) {
            this.compRes = compRes;
            this.runsRes1 = runsRes1;
            this.runsRes2 = runsRes2;
            this.derRes = derRes;
            this.runsTrendRes1 = runsTrendRes1;
            this.runsTrendRes2 = runsTrendRes2;
            this.runsTrendRes3 = runsTrendRes3;
            this.covRes = covRes;
            this.colisRes1 = colisRes1;
            this.colisRes2 = colisRes2;
        }
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
                Files.newInputStream(Path.of("data.e"))), 10000);

        ArrayList<Integer> integers = new ArrayList<>();
        bits.forEach(integers::add);
        long N = integers.size();

        S[][] Sij= new S[10][1001];

        int compRes;
        int[] runsRes;
        double derRes;
        int[] runsTrendRes;
        double covRes;
        int[] colisRes;
        S temp;

        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> newSeq = new ArrayList<>();
            for (int j = (int) (i * N / 10); j < (i + 1) * N / 10; j++) {
                newSeq.add(integers.get(j));
            }
            compRes = compressionTest(newSeq);
            runsRes = runsTest(newSeq);
            derRes = maxCumDev(newSeq);
            runsTrendRes = runsTestWithTrend(newSeq);
            covRes = covarianceTest(newSeq);
            colisRes = collisionsTest(newSeq);

            temp = new S(compRes, runsRes[0], runsRes[1], derRes, runsTrendRes[0], runsTrendRes[1], runsTrendRes[2],
                    covRes, colisRes[0], colisRes[1]);

            Sij[i][0] = temp;

            for(int j = 1; j <= 1000; j++) {
                fisherYates(newSeq);
                compRes = compressionTest(newSeq);
                runsRes = runsTest(newSeq);
                derRes = maxCumDev(newSeq);
                runsTrendRes = runsTestWithTrend(newSeq);
                covRes = covarianceTest(newSeq);
                colisRes = collisionsTest(newSeq);

                temp = new S(compRes, runsRes[0], runsRes[1], derRes, runsTrendRes[0], runsTrendRes[1], runsTrendRes[2],
                        covRes, colisRes[0], colisRes[1]);
                Sij[i][j] = temp;
            }
        }

        int[][] R = new int[10][10];

        for(int i = 0; i < 10; i++) {

        }



    }

    public static void fisherYates(ArrayList<Integer> array) {
        Random rand = new Random();

        for (int i = array.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);

            int temp = array.get(i);
            array.set(i, array.get(j));
            array.set(j, temp);
        }
    }

    public static int compressionTest(ArrayList<Integer> integers) {
        ArrayList<Integer> newSeq = new ArrayList<>();
        for (int i = 0; i < integers.size() - 8; i += 8) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < 8; j++) {
                s.append(integers.get(i + j).toString());
            }
            newSeq.add(Integer.parseInt(s.toString(), 2));
        }
        StringBuilder sb = new StringBuilder();
        for (int i : newSeq) {
            sb.append(i).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        int len = 0;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BZip2CompressorOutputStream bzip2os = new BZip2CompressorOutputStream(baos)) {

            byte[] inputBytes = sb.toString().getBytes();
            bzip2os.write(inputBytes);
            bzip2os.finish();
            len = baos.size();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        return len;
    }


        public static int[] runsTest (ArrayList < Integer > integers) {
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

        public static int getLongestSeriesOfInt (ArrayList < Integer > list,int el){
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

        public static double maxCumDev (ArrayList < Integer > integers) {
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

        public static int[] runsTestWithTrend (ArrayList < Integer > integers) {
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

        public static void removeLeadingZeros (List < Integer > list) {
            while (!list.isEmpty() && list.get(0) == 0) {
                list.remove(0);
            }
        }

        public static int countSeries (List < Integer > list) {
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

        public static int calculateMaxSeriesLength (List < Integer > list) {
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


        public static double covarianceTest (ArrayList < Integer > integers) {
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

        public static int[] collisionsTest (ArrayList < Integer > integers) {
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
            return new int[]{min, max};
        }

        public static int findFirstRepeatingIndex (List < Integer > sequence) {
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
