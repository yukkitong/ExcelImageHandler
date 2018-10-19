package kr.co.uniess.kto.batch.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.uniess.kto.batch.CsvWriter;
import kr.co.uniess.kto.batch.component.OutputFilenameGenerator;
import kr.co.uniess.kto.batch.model.ExcelImage;

@Service
public class XlsToCsvConversionService extends AbstractBatchService {

  private final Logger logger = LoggerFactory.getLogger(XlsToCsvConversionService.class);

  private OutputFilenameGenerator generator;

  @Autowired
  public void setOutputFileNameGenerator(OutputFilenameGenerator csvFilenameGenerator) {
    this.generator = csvFilenameGenerator;
  }

  @Override
  public void execute() {
    try {
      List<ExcelImage> list = (List<ExcelImage>) getParameter("list");
      String inputFilename = (String) getParameter("file");
      CsvWriter.write(list, generator.generateNameBy(inputFilename));
    } catch (IOException e) {
      logger.error("\n", e);
    }
  }
}
