package net.stamfest.randomtests;

import net.stamfest.randomtests.bits.Bits;
import net.stamfest.randomtests.bits.Permutation;
import net.stamfest.randomtests.method.Independence;
import net.stamfest.randomtests.method.SameDistributionTest;
import net.stamfest.randomtests.utils.IO;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static net.stamfest.randomtests.method.Permutation.permutationTest;

public class Main {

    public static void main(String[] args) {

        Option nist = Option.builder("nist")
                .desc("NIST STS")
                .build();

        Option permutation = Option.builder("perm")
                .longOpt("permutation ")
                .desc("permutation")
                .build();

        Option indep = Option.builder("indep")
                .longOpt("independence")
                .desc("independence test")
                .build();

        Option distr = Option.builder("distr")
                .longOpt("distribution ")
                .desc("same distribution test")
                .build();

        Option all = Option.builder("all")
                .desc("all tests")
                .build();

        Option help = Option.builder("h")
                .longOpt("help")
                .desc("print help message")
                .build();

        Option in = Option.builder("in")
                .longOpt("in")
                .argName("file")
                .hasArg()
                .desc("input file name")
                .build();



        Option text = Option.builder("t")
                .longOpt("text")
                .desc("text instead of binary")
                .build();


        Option bits = Option.builder("bits")
                .longOpt("max")
                .argName("bits")
                .hasArg()
                .desc("max bits count")
                .build();

        Options options = new Options();

        options.addOption(help);
        options.addOption(in);
        options.addOption(text);
        options.addOption(bits);
        options.addOption(nist);
        options.addOption(permutation);
        options.addOption(indep);
        options.addOption(distr);
        options.addOption(all);


        HelpFormatter formatter = new HelpFormatter();
        CommandLineParser parser = new DefaultParser();

        formatter.setOptionComparator(null);

        String inPath = "";
        String outPath = "";
        boolean binaryFile = true;

        boolean NISTtests = true;
        boolean permtest = false;
        boolean independtest = false;
        boolean distribtest = false;

        boolean allTest = false;

        int maxbits = 100000;

        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                printHelp(formatter, options);
                return;
            }
            if (line.hasOption("in")) {
                inPath = line.getOptionValue("in");
            } else {
                throw new MissingArgumentException("No input file name provided");
            }
            if (line.hasOption("out")) {
                outPath = line.getOptionValue("out");
            }
            if (line.hasOption("t")) {
                binaryFile = false;
            }

            if (line.hasOption("all")) {
                distribtest = true;
                independtest = true;
                permtest = true;
            } else {
                if (line.hasOption("distr")) {
                    NISTtests = false;
                    distribtest = true;
                }
                if (line.hasOption("indep")) {
                    NISTtests = false;
                    independtest = true;
                }
                if (line.hasOption("perm")) {
                    NISTtests = false;
                    permtest = true;
                }
                if (line.hasOption("nist")) {
                    NISTtests = true;
                }
            }
            if (line.hasOption("bits")) {
                try {
                    maxbits = Integer.parseInt(line.getOptionValue("bits"));
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                }
            }

            process(inPath,
                    binaryFile,
                    NISTtests,
                    permtest,
                    independtest,
                    distribtest,
                    allTest,
                    maxbits);


        } catch (ParseException exp) {
            System.out.println("Parsing failed.  Reason: " + exp.getMessage());
            System.out.println();
            printHelp(formatter, options);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void printHelp(HelpFormatter formatter, Options options) {
        formatter.printHelp(100, "Zhuravlev STS", "\nOptions:\n\n",
                options, "\n", false);
    }


    private static void process(String inPath,
                                boolean binaryFile,
                                boolean NISTtests,
                                boolean permtest,
                                boolean independtest,
                                boolean distribtest,
                                boolean allTest,
                                int maxbits) {

        Bits bits;

        try {
            if (binaryFile) {
                bits = IO.readBinary(Objects.requireNonNull(
                        Files.newInputStream(Path.of(inPath))), maxbits);
            } else {
                bits = IO.readAscii(Objects.requireNonNull(
                        Files.newInputStream(Path.of(inPath))), maxbits);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (allTest) {
            independtest = true;
            distribtest = true;
            permtest = true;
            NISTtests = true;
        }

        StringBuilder result = new StringBuilder();

        if (permtest) {
            result.append(permutationTest(bits)).append("\n");
        }

        if (independtest) {
            result.append(Independence.run(bits)).append("\n");
        }

        if (distribtest) {
            result.append(SameDistributionTest.runSameDistributionTest(bits)).append("\n");
        }

        System.out.println(result.toString());

        if (NISTtests) {
            try {
                NISTTest(inPath, binaryFile, maxbits);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }


    }


    public static void NISTTest(String inPath, boolean binary, int maxbits) throws IOException {
        InputStream is = Files.newInputStream(Path.of(inPath));

        Suite s = Suite.getStandardTestSuite(null);

        for (int i = 0; i < 100; i++) {
            Bits bits;
            if (binary) {
                bits = IO.readBinary(is, maxbits / 100);
            } else {
                bits = IO.readAscii(is, maxbits / 100);
            }
            s.runSuite(bits);
        }

        PrintWriter out = new PrintWriter(System.out);
        s.report(out);
        out.flush();

    }

}
