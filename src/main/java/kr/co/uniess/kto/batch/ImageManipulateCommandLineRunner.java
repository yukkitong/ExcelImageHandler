package kr.co.uniess.kto.batch;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.component.ConversionManipulator;
import kr.co.uniess.kto.batch.component.CsvImageManipulator;
import kr.co.uniess.kto.batch.component.ExcelImageManipulator;

@Component
public class ImageManipulateCommandLineRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger("ImageManipulateCommandLineRunner");

    @Autowired
    @Lazy
    private ExcelImageManipulator excelImageManipulator;

    @Autowired
    @Lazy
    private CsvImageManipulator csvImageManipulator;

    @Autowired
    @Lazy
    private ConversionManipulator conversionManipulator;

    @Override
    public void run(String... args) throws Exception {
        logger.info("START with - {}", Arrays.toString(args));
        
        Options options = new Options();
        options.addOption("i", true, "input file(xls or csv) path with an extension [.xls/.csv]");
        options.addOption("c", false, "conversion only (xls -> csv)");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        try {
            if (cmd.hasOption("i")) {
                String filePath = cmd.getOptionValue("i");
                if (cmd.hasOption("c")) {
                    if (!filePath.endsWith(".xls")) {
                        throw new RuntimeException("option 'c' needs a xls file only. [ " + filePath + " ]");
                    }
                    conversionManipulator.run(filePath);
                } else if (filePath.endsWith(".xls")) {
                    excelImageManipulator.run(filePath);
                } else if (filePath.endsWith(".csv")) {
                    csvImageManipulator.run(filePath);
                } else {
                    printUsage(options);
                    System.exit(1);
                }
            } else {
                printUsage(options);
                System.exit(1);
            }

            logger.info("END SUCCESSFULLY!");
        } catch(Exception e) {
            logger.error("END with errors", e);
            System.exit(1);
        }
    }

    private void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ExcelImageHandler 1.0", options);
    }
}