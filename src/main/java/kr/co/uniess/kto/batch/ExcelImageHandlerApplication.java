package kr.co.uniess.kto.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import kr.co.uniess.kto.batch.model.ExcelImage;
import kr.co.uniess.kto.batch.service.ImageManipulateService;

@SpringBootApplication
public class ExcelImageHandlerApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ExcelImageHandlerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ExcelImageHandlerApplication.class, args);
	}

	@Autowired
	private ImageManipulateService<ExcelImage> imageManipulateService;

	@Override
	public void run(String... args) throws Exception {
		if (args.length == 0) {
			System.exit(1);
		}
		
		final String filePath = args[0];
	
		// 생태관광
		XlsConfig config = new XlsConfig.Builder()
			.sheetName("생태관광")
			.startRow(6)
			.colOfContentId(0)
			.colOfContentTitle(2)
			.colOfImagePath(12)
			.colOfMain(13)
			.build();

		// 시티투어
		// meta.sheetName = "시티투어";
		// meta.startRow = 7;
		// meta.contentIdColumn = 0;
		// meta.contentTitleColumn = 2;
		// meta.imagePathColumn = 11;
		// meta.primaryColumn = 12;

		XlsReader reader = new XlsReader(config);
		List<ExcelImage> list = reader.loadExcelFile(filePath);
		imageManipulateService.execute(list);
	}

}
