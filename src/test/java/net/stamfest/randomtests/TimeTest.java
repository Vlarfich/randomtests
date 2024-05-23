package net.stamfest.randomtests;

import net.stamfest.randomtests.bits.ArrayBits;
import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.StringBits;
import net.stamfest.randomtests.nist.*;
import net.stamfest.randomtests.nist.test.*;
import net.stamfest.randomtests.utils.IO;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

public class TimeTest {

    public static void main(String[] args) {
        try {
            new TimeTest().ShowTimeTest();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void ShowTimeTest() throws IOException {
        FrequencyTime();
        BlockFrequencyTime();
        CumulativeSumsTime();
        RunsTime();
        LongestRunOfOnesTime();
        RankTime();
        DiscreteFourierTransformTime();
        OverlappingTemplateTime();
        UniversalTime();
        ApproximateEntropyTime();
        RandomExcursionsTime();
        RandomExcursionVariantTime();
        SerialTime();
        NonOverlappingTemplateTime();
        LinearComplexityTime();
    }

    public void ApproximateEntropyTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);

        ApproximateEntropy ae = new ApproximateEntropy(2);

        long startTime = System.currentTimeMillis();
        Result[] results = ae.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("ApproximateEntropy time for " + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //ae.report(System.out, results);
    }

    public void BlockFrequencyTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);

        BlockFrequency f = new BlockFrequency(3);

        long startTime = System.currentTimeMillis();
        Result[] results = f.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("BlockFrequency time for \t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //f.report(System.out, results);
    }

    public void CumulativeSumsTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);

        CumulativeSums cs = new CumulativeSums();

        long startTime = System.currentTimeMillis();
        Result[] results = cs.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("CumulativeSums time for \t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //cs.report(System.out, results);
  }

    public void DiscreteFourierTransformTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);
        DiscreteFourierTransform dft = new DiscreteFourierTransform();

        long startTime = System.currentTimeMillis();
        Result[] results = dft.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Spectral time for \t\t\t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //dft.report(System.out, results);
    }

    public void FrequencyTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);
        Frequency f = new Frequency();

        long startTime = System.currentTimeMillis();
        Result[] results = f.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Frequency time for \t\t\t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //f.report(System.out, results);
    }

    public void LinearComplexityTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);
        LinearComplexity lc = new LinearComplexity(1000);

        long startTime = System.currentTimeMillis();
        Result[] results = lc.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("LinearComplexity time for \t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //lc.report(System.out, results);
    }

    public void LongestRunOfOnesTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                        NonOverlappingTemplateMatchingsTest.class.getResourceAsStream("/data.e")),
                500000);

        LongestRunOfOnes l = new LongestRunOfOnes();
        long startTime = System.currentTimeMillis();
        Result[] results = l.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("LongestRunOfOnes time for \t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");

        //l.report(System.out, results);
    }

    public void NonOverlappingTemplateTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                NonOverlappingTemplateMatchingsTest.class.getResourceAsStream("/data.e")),
                500000);
        NonOverlappingTemplateMatchings n = new NonOverlappingTemplateMatchings(9);

        long startTime = System.currentTimeMillis();
        Result[] results = n.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("NonOverlappingTemp time for " + bits.getLength() +
                " bits: " + timeElapsed + " ms");
        //n.report(System.out, results);
    }

    public void OverlappingTemplateTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                OverlappingTemplateMatchingTest.class.getResourceAsStream("/data.e")),
                500000);
        OverlappingTemplateMatching o = new OverlappingTemplateMatching(9);

        long startTime = System.currentTimeMillis();
        Result[] results = o.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("OverlappingTempl time for \t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //o.report(System.out, results);
    }

    public void RandomExcursionsTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                RandomExcursionsTest.class.getResourceAsStream("/data.e")), 500000);

        RandomExcursions re = new RandomExcursions();
        long startTime = System.currentTimeMillis();
        Result[] results = re.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("RandomExcursions time for \t" + bits.getLength() +
                " bits: " + timeElapsed + " ms");

        //re.report(System.out, results);
    }

    public void RandomExcursionVariantTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                RandomExcursionsVariantTest.class.getResourceAsStream("/data.e")), 500000);

        RandomExcursionsVariant re = new RandomExcursionsVariant();
        long startTime = System.currentTimeMillis();
        RandomExcursionsVariant.RandomExcursionsVariantResult[] results = (RandomExcursionsVariant.RandomExcursionsVariantResult[]) re.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("RandomExcursionVar time for " + bits.getLength() +
                " bits: " + timeElapsed + " ms");

        //re.report(System.out, results);
    }

    public void RankTime() throws IOException {
        Bits b = IO.readAscii(Objects.requireNonNull(
                RankTest.class.getResourceAsStream("/data.e")), 500000);

        Rank r = new Rank();
        long startTime = System.currentTimeMillis();
        Result[] results = r.runTest(b);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Rank time for \t\t\t\t" + b.getLength() +
                " bits: " +timeElapsed + " ms");

        //r.report(System.out, results);
    }

    public void RunsTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                LinearComplexityTest.class.getResourceAsStream("/data.e")), 500000);

        Runs r = new Runs();

        long startTime = System.currentTimeMillis();
        Result[] results = r.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Runs time for \t\t\t\t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //r.report(System.out, results);
    }

    public void SerialTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                SerialTest.class.getResourceAsStream("/data.e")), 500000);
        Serial s = new Serial(2);

        long startTime = System.currentTimeMillis();
        Result[] results = s.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Serial time for \t\t\t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");
        //s.report(System.out, results);
    }

    public void UniversalTime() throws IOException {
        Bits bits = IO.readAscii(Objects.requireNonNull(
                UniversalTest.class.getResourceAsStream("/data.e")), 500000);
        Universal u = new Universal();

        long startTime = System.currentTimeMillis();
        Result[] results = u.runTest(bits);
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Universal time for \t\t\t" + bits.getLength() +
                " bits: " +timeElapsed + " ms");

        //u.report(System.out, results);
    }
}
