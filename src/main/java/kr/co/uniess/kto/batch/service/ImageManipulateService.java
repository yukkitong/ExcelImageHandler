package kr.co.uniess.kto.batch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.repository.ContentMasterRepository;
import kr.co.uniess.kto.batch.repository.DatabaseMasterRepository;
import kr.co.uniess.kto.batch.repository.ExcelImageUploadHistRepository;
import kr.co.uniess.kto.batch.repository.ImageRepository;
import kr.co.uniess.kto.batch.repository.RepositoryUtils;

@Service
@Scope("prototype")
public class ImageManipulateService implements BatchService<List<SourceImage>> {

    private final Logger logger = LoggerFactory.getLogger(ImageManipulateService.class);

    private Map<String, String> cacheForContentId = new HashMap<>();

    @Autowired
    private ContentMasterRepository contentMasterRepository;

    @Autowired
    private DatabaseMasterRepository databaseMasterRepository;

    @Autowired
    private ExcelImageUploadHistRepository excelImageUploadHistRepository;

    @Autowired
    private ImageRepository imageRepository;

    private boolean isDebug;

    private String eihId;

    private HashMap<String, Integer> counter;

    private static final String MARK_SKIP = "SKIPPED";
    private static final String MARK_SAVE = "SAVEED";
    private static final String MARK_FAIL = "FAILED";

    public ImageManipulateService(boolean isDebug) {
        this.isDebug = isDebug;
        this.eihId = null;
        this.counter = new HashMap<>();
    }

    public void setEihId(String eihId) {
        this.eihId = eihId;
    }

    private void clearCache() {
        cacheForContentId.clear();
    }

    private void resetCounter() {
        counter.clear();
        counter.put(MARK_SKIP, 0);
        counter.put(MARK_SAVE, 0);
        counter.put(MARK_FAIL, 0);
    }

    private void increase(String mark) {
        counter.put(mark, counter.get(mark) + 1);
    }
    
    @Override
    public void execute(List<SourceImage> list) {
        resetCounter();
        clearCache();

        int index = 0;
        for (SourceImage item : list) {
            handleItem(item, index);
            index ++;
        }
        
        final int saveCount = counter.get(MARK_SAVE);
        final int skipCount = counter.get(MARK_SKIP);
        final int failCount = counter.get(MARK_FAIL);
        if (eihId != null) {
            excelImageUploadHistRepository.updateCount(eihId, saveCount, skipCount, failCount);
        }
    }

    protected void handleItem(SourceImage item, int index) {
        final String contentId = item.contentId;
        String cotId;
        if (cacheForContentId.containsKey(contentId)) {
            cotId = cacheForContentId.get(contentId);
        } else {
            cotId = contentMasterRepository.getCotId(contentId);
            cacheForContentId.put(contentId, cotId);
        }

        if (cotId == null) {
            increase(MARK_SKIP);
            logger.info(item + ":::SKIPPED [ `COT_ID` is NULL ]");
            return;
        }

        if (imageRepository.findOne(cotId, item.url) != null) {
            increase(MARK_SKIP);
            logger.info(item + ":::SKIPPED [ Already Stored! ]");
        } else {
            try {
                final String title = getTitle(item, index);
                final String newImgId = RepositoryUtils.generateRandomId();
                imageRepository.insertImage(newImgId, cotId, title, item.url, item.main);
                if (item.main) {
                    databaseMasterRepository.updateItemImageOnly(cotId, newImgId);
                }

                increase(MARK_SAVE);
                if (item.main) {
                    logger.info(item + ":::INSERT {} [as MAIN] {} - {}", cotId, newImgId, title);
                } else {
                    logger.info(item + ":::INSERT {} IMAGE TITLE {} - {}", cotId, newImgId, title);
                }
            } catch(Exception e) {
                increase(MARK_FAIL);
                logger.info(item + ":::FAILED [ " + e.getMessage() + " ]");
                // TODO insert log message
            }
        }
    }

    private String getTitle(SourceImage item, int index) {
        return item.title + " " + index;
    }
}