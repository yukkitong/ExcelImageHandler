package kr.co.uniess.kto.batch;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.model.ExcelImage;
import kr.co.uniess.kto.batch.service.BatchService;

@Component
public class ExcelImageManipulateRunner implements CommandLineRunner {

  private final Logger logger = LoggerFactory.getLogger("CommandLineRunner");

  @Autowired
  private BatchService xlsToCsvConversionService;

  @Autowired
  private BatchService imageManipulateService;

  @Override
  public void run(String... args) throws Exception {
    logger.info("START with - {}", Arrays.toString(args));

    if (args.length == 0) {
      System.exit(1);
    }
    
    final String filePath = args[0];

    try {
      XlsReader reader = new XlsReader();
      List<ExcelImage> list = reader.loadExcelFile(filePath);
      
      xlsToCsvConversionService.addParameter("list", list);
      xlsToCsvConversionService.addParameter("file", filePath);
      xlsToCsvConversionService.execute();

      imageManipulateService.addParameter("list", list);
      imageManipulateService.execute();
    } catch(Exception e) {
      logger.error("\n", e);
      System.exit(1);
    }

    logger.info("CLOSED SUCCESSFULLY!");
  }
}