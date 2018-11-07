package kr.co.uniess.kto.batch;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.controller.ConversionController;
import kr.co.uniess.kto.batch.controller.ExcelImageController;

@Component
public class ImageManipulateCommandLineRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(ImageManipulateCommandLineRunner.class);

    @Autowired
    @Lazy
    private ExcelImageController excelImageController;

    @Autowired
    @Lazy
    private ConversionController conversionController;

    @Override
    public void run(String... args) throws Exception {    
        logger.info("START with - {}", Arrays.toString(args));  
        
        // Parse Command and Options
        Options options = createOptions();
        CommandLine cmd = null; 
        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch(ParseException e) {
            e.printStackTrace();
            printUsage(options);
            System.exit(1);
        }

        if (!cmd.hasOption("i")) {
            printUsage(options);
            System.exit(1);
        }

        String filePath = cmd.getOptionValue("i");
        try {
            if (filePath.endsWith(".xls")) {
                String[] sheetNames = null;
                if (cmd.hasOption("s")) {
                    sheetNames = cmd.getOptionValues("s");
                }
                XlsConfig config = null;
                if (sheetNames != null) {
                    config = new XlsConfig.Builder().sheetNames(sheetNames).build();
                }
                XlsReader reader = new XlsReader(config);
                if (cmd.hasOption("csv")) {
                    String csvFileName = filePath.substring(0, filePath.lastIndexOf(".xls")) + ".csv";
                    conversionController.setOuputFilename(csvFileName);
                    conversionController.run(reader.read(filePath));
                } else {
                    if (cmd.hasOption("eid")) {
                        String eihId = cmd.getOptionValue("eid");
                        excelImageController.setEihId(eihId);
                    }
                    excelImageController.run(reader.read(filePath));
                }
            } else if (filePath.endsWith(".csv")) {
                if (cmd.hasOption("eid")) {
                    String eihId = cmd.getOptionValue("eid");
                    excelImageController.setEihId(eihId);
                }
                excelImageController.run(CsvReader.read(filePath));
            } else {
                throw new RuntimeException("No matched handler. [ " + filePath + " ]");
            }
            logger.info("END SUCCESSFULLY!");
        } catch (Exception e) {
            logger.error("END with errors", e);
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private Options createOptions() {
        Options options = new Options();
        options.addOption("i", "input", true, "처리할 액셀파일(.xls) 또는 CSV파일(.csv)을 입력하세요.");
        options.addOption("csv", false, "액셀파일을 CSV 포맷으로 변환합니다.");
        options.addOption("eid", "eih-id", true, "EIH 테이블의 ID를 입력하세요.(Admin전용)");
        Option sheetOption = new Option("s", "sheet-names", true, "액셀파일에서 특정 시트를 선택하여 처리합니다. [name1 name2 ...]");
        sheetOption.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(sheetOption);
        return options;
    }

    private static void printUsage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ExcelImageHandler 1.0", options);
    }
}