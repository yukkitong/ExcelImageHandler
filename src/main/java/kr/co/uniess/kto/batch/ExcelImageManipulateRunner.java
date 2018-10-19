package kr.co.uniess.kto.batch;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.model.ExcelImage;
import kr.co.uniess.kto.batch.service.ImageManipulateService;

@Component
public class ExcelImageManipulateRunner implements CommandLineRunner {

  private final Logger logger = LoggerFactory.getLogger("CommandLineRunner");

  @Autowired
  private ImageManipulateService<ExcelImage> imageManipulateService;

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
      imageManipulateService.execute(list);
    } catch(Exception e) {
      logger.error("\n", e);
      System.exit(1);
    }

    logger.info("CLOSED SUCCESSFULLY!");
  }
}