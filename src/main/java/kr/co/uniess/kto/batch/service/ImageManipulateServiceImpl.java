package kr.co.uniess.kto.batch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.uniess.kto.batch.model.ExcelImage;
import kr.co.uniess.kto.batch.repository.ContentMasterRepository;
import kr.co.uniess.kto.batch.repository.DatabaseMasterRepository;
import kr.co.uniess.kto.batch.repository.ImageRepository;
import kr.co.uniess.kto.batch.repository.RepositoryUtils;

@Service
public class ImageManipulateServiceImpl implements ImageManipulateService<ExcelImage> {

    private final Logger logger = LoggerFactory.getLogger(ImageManipulateService.class);

    private Map<String, String> cacheForContentId = new HashMap<>();

    @Autowired
    private ContentMasterRepository contentMasterRepository;

    @Autowired
    private DatabaseMasterRepository databaseMasterRepository;

    @Autowired
    private ImageRepository imageRepository;

    private boolean isRehearsal;

    public ImageManipulateServiceImpl() {
        isRehearsal = false;
    }

    public ImageManipulateService<ExcelImage> rehearsalMode() {
        isRehearsal = true;
        return this;
    }

    private void clearCache() {
        cacheForContentId.clear();
    }

    @Override
    public void execute(List<ExcelImage> list) {
        clearCache();
        for (ExcelImage item : list) {
            handleItem(item);
        }
    }

    @Transactional
    protected void handleItem(ExcelImage item) {
        final String cotentId = item.contentId;
        String cotId = null;
        if (cacheForContentId.containsKey(cotentId)) {
            cotId = cacheForContentId.get(cotentId);
        } else {
            cotId = contentMasterRepository.getCotId(cotentId);
            cacheForContentId.put(cotentId, cotId);
        }

        if (cotId == null) {
            logger.info(item + " - SKIPPED [COT_ID is NULL]");
            return;
        }

        if (imageRepository.hasItem(cotId, item.url)) {
            logger.info(item + " - SKIPPED");
        } else {
            final String imgId = RepositoryUtils.generateRandomId();
            try {
                if (item.isMain) {
                    if (!isRehearsal) {
                        imageRepository.insertImage(imgId, cotId, item.title, item.url, item.isMain);
                        databaseMasterRepository.updateItemOnlyImage(cotId, imgId);
                    }
                    logger.info(item + " - INSERTED [as MAIN]");
                } else {
                    if (!isRehearsal) {
                        imageRepository.insertImage(imgId, cotId, item.title, item.url, item.isMain);
                    }
                    logger.info(item + " - INSERTED");
                }
            } catch(Exception e) {
                logger.info(item + " - FAILED");
            }
        }
    }
}