package net.stamfest.randomtests;

import org.apache.commons.cli.*;

import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {

        Option nist = Option.builder("nist")
                .desc("NIST STS")
                .build();

        Option permutation = Option.builder("perm")
                .longOpt("permutation ")
                .desc("тесты перестановок")
                .build();

        Option indep = Option.builder("indep")
                .longOpt("independence")
                .desc("Проверка независимости для бинарных данных")
                .build();

        Option distr = Option.builder("distr")
                .longOpt("distribution ")
                .desc("Проверка одинаковой распределённости для бинарных данных")
                .build();

        Option all = Option.builder("all")
                .desc("Выполнить все тесты")
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

        Option directory = Option.builder("dir")
                .argName("dir")
                .hasArg()
                .desc("путь к директории с файлами")
                .build();

        Option out = Option.builder("out")
                .longOpt("out")
                .argName("file")
                .hasArgs()
                .desc("файл для записи результатов (по умолчанию - result.txt)")
                .build();

        Options options = new Options();

        options.addOption(help);
        options.addOption(in);
        options.addOption(directory);
        options.addOption(out);
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
        try {
            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                printHelp(formatter, options);
                return;
            }
            if(line.hasOption("in")) {
                inPath = line.getOptionValue("in");
            }
            else {
                throw new MissingArgumentException("No input file name provided");
            }
            if(line.hasOption("out")) {
                outPath = line.getOptionValue("out");
            }

            //TransformKey.transformPrivateKey(inPath, outPath, StandardCharsets.UTF_8);

        } catch (ParseException exp) {
            System.out.println("Parsing failed.  Reason: " + exp.getMessage());
            System.out.println();
            printHelp(formatter, options);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void printHelp(HelpFormatter formatter, Options options) {
        formatter.printHelp(100,"Zhuravlev STS", "\nOptions:\n\n",
                options, "\n", false);
    }

}
