package kr.co.uniess.kto.batch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import kr.co.uniess.kto.batch.repository.ContentMasterRepository;
import kr.co.uniess.kto.batch.repository.DatabaseMasterRepository;
import kr.co.uniess.kto.batch.repository.ImageRepository;
import kr.co.uniess.kto.batch.repository.RepositoryUtils;

@SpringBootApplication
public class ExcelImageHandlerApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(ExcelImageHandlerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ExcelImageHandlerApplication.class, args);
	}

	@Autowired
	private ContentMasterRepository contentMasterRepository;

	@Autowired
	private DatabaseMasterRepository databaseMasterRepository;

	@Autowired
  private ImageRepository imageRepository;

	@Override
	public void run(String... args) throws Exception {
		if (args.length == 0) {
			System.exit(1);
		}
	
		// 생태관광
		XlsMeta meta = new XlsMeta();
		meta.filePath = args[0];
		meta.sheetName = "생태관광";
		meta.startRow = 6;
		meta.contentIdColumn = 0;
		meta.contentTitleColumn = 2;
		meta.imagePathColumn = 12;
		meta.primaryColumn = 13;

		// 시티투어
		// meta.filePath = args[0];
		// meta.sheetName = "시티투어";
		// meta.startRow = 7;
		// meta.contentIdColumn = 0;
		// meta.contentTitleColumn = 2;
		// meta.imagePathColumn = 11;
		// meta.primaryColumn = 12;

		List<ExcelImage> list = distinct(XlsReader.loadExcelFile(meta));
		for (ExcelImage item : list) {
			logger.info(item.toString());
		}

		clearCache();

		// for (ExcelImage item : list) {
		// 	handleItem(item);
		// }
	}

	/**
	 * 정렬이된 경우에만 정상동작한다.
	 * @param list
	 * @return
	 */
	private List<ExcelImage> distinct(List<ExcelImage> list) {
		ArrayList<ExcelImage> result = new ArrayList<>(list.size());
		for (int i = 0, size = list.size(); i < size; i ++) {
			ExcelImage item = list.get(i);
			boolean first = i == 0;
			if (first) {
				result.add(item);
			} else {
				if (!result.get(result.size() - 1).equals(item)) {
					result.add(item);
				}
			}
		}
		return result;
	}

	private Map<String, String> cacheForContentId = new HashMap<>();
	private void clearCache() {
		cacheForContentId.clear();
	}

	@Transactional
	private void handleItem(ExcelImage item) {
		final String cotentId = item.contentId;
		String cotId = null;
		if (cacheForContentId.containsKey(cotentId)) {
			cotId = cacheForContentId.get(cotentId);
		} else {
			cotId = contentMasterRepository.getCotId(cotentId);
			cacheForContentId.put(cotentId, cotId);
		}
		if (cotId == null) {
			logger.info(item + " - IGNORED! [COT_ID is NULL]");
			return;
		}

		if (imageRepository.hasItem(cotId, item.url)) {
			logger.info(item + " - SKIPPED!");
		} else {
			final String imgId = RepositoryUtils.generateRandomId();
			imageRepository.insertImage(imgId, cotId, item.title, item.url, item.isMain);
			logger.info(item + " - INSERTED! [IMG_ID: {}]", imgId);
			if (item.isMain) {
				databaseMasterRepository.updateItemOnlyImage(cotId, imgId);
				logger.info(item + " - UPDATED AS MAIN [IMG_ID: {}]", imgId);
			}
		}
	}
}
