package kr.co.uniess.kto.batch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import kr.co.uniess.kto.batch.model.SourceImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvWriter {

    private final static Logger logger = LoggerFactory.getLogger(CsvWriter.class);

    public static void write(List<SourceImage> list, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            StatefulBeanToCsv<SourceImage> beanToCsv = new StatefulBeanToCsvBuilder<SourceImage>(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(';').build();
            for (SourceImage item : list) {
                try {
                    beanToCsv.write(item);
                } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}