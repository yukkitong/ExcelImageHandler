package kr.co.uniess.kto.batch.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import kr.co.uniess.kto.batch.CsvWriter;
import kr.co.uniess.kto.batch.model.SourceImage;

@Service
public class ConvertService implements BatchService<List<SourceImage>> {

    private final Logger logger = LoggerFactory.getLogger(ConvertService.class);
    
    private String outputFilename;

    public void setOuputFilename(String filename) {
        this.outputFilename = filename;
    }

    @Override
    public void execute(List<SourceImage> list) {
        try {
            CsvWriter.write(list, outputFilename);
        } catch (IOException e) {
            logger.error("\n", e);
        }
    }
}
