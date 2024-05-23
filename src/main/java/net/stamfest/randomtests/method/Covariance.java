package net.stamfest.randomtests.method;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.math3.special.Erf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Covariance {

    public static double run(Bits bits) {
        double pvalue = 0;

        double m = 0;

        List<Integer> integers = new ArrayList<>();
        bits.forEach(integers::add);
        for (int x : integers) {
            m += x;
        }
        m /= bits.getLength();
        System.out.println(m);
        double s2 = 0;
        int val = 0;
        for(int x : integers) {
            s2 += (x - m) * (x - m);
        }

        s2 /= (bits.getLength() - 1);

        System.out.println(s2);

        double[] Rj = new double[100];

        Rj[0] = s2;

        double def = - (m * m * bits.getLength()) / (bits.getLength() - 1);

        for(int j = 1; j < Rj.length; j++) {
            Rj[j] = def;
            double sum = 0;
            for(int i = 0; i < bits.getLength() - j - 1; i++) {
                sum += integers.get(i + j) * integers.get(i);
            }
            sum /= bits.getLength() - j - 1;
            Rj[j] += sum;
        }

        double triang = Erf.erfc(1 - 0.001 / 2);

        double porog = triang / (12 * Math.sqrt(bits.getLength() - 1));

        System.out.println(Math.abs(Rj[0] - (double) 1 /12));
        for(int j = 1; j < Rj.length; j++) {
            System.out.println(Math.abs(Rj[j]) < + porog);
        }

        return pvalue;
    }

    public static void main(String[] args) throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                Files.newInputStream(Path.of("data.e"))), 1000);

        run(bits);

    }

}
