package kr.co.uniess.kto.batch.service;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import kr.co.uniess.kto.batch.CsvWriter;
import kr.co.uniess.kto.batch.component.OutputFilenameGenerator;
import kr.co.uniess.kto.batch.model.SourceImage;

@Service
@Scope("prototype")
public class XlsToCsvConversionService extends AbstractBatchService {

    private final Logger logger = LoggerFactory.getLogger(XlsToCsvConversionService.class);

    private OutputFilenameGenerator generator;

    @Autowired
    public void setOutputFileNameGenerator(OutputFilenameGenerator csvFilenameGenerator) {
        this.generator = csvFilenameGenerator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        try {
            List<SourceImage> list = (List<SourceImage>) getParameter("list");
            String inputFilename = (String) getParameter("file");
            CsvWriter.write(list, generator.generateNameBy(inputFilename));
        } catch (IOException e) {
            logger.error("\n", e);
        }
    }
}
