package kr.co.uniess.kto.batch.service;

import java.io.File;
import java.util.*;

import kr.co.uniess.kto.batch.repository.model.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

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
    private static final String MARK_SAVE = "SAVED";
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

        HashMap<String, ArrayList<String>> contentMap = new HashMap<>();

        ArrayList<String> urlList = null;
        for (SourceImage item : list) {
            if (!contentMap.containsKey(item.getContentId())) {
                urlList = new ArrayList<>();
                contentMap.put(item.getContentId(), urlList);
            }
            urlList.add(item.url);
        }

        for (String contentId : contentMap.keySet()) {
            urlList = contentMap.get(contentId);
            if (!urlList.isEmpty()) {
                List<Image> targets = imageRepository.selectDeleteTarget(contentId, urlList.toArray(new String[0]));
                if (targets != null && !targets.isEmpty()) {
                    ArrayList<String> imgIds = new ArrayList<>(targets.size());
                    ArrayList<String> imgPathList = new ArrayList<>(targets.size());
                    for (Image i : targets) {
                        imgIds.add(i.getImgId());
                        imgPathList.add(i.getPath());
                    }

                    logger.info(":::DELETE::: {}EA, {}", targets.size(), Arrays.toString(targets.toArray()));
//                    if (imageRepository.deleteImages(imgIds.toArray(new String[0])) > 0) {
//                        // TODO LOG using `targets`
//                        // TODO if exist file then delete it
//                        for (String path : imgPathList) {
//                            // TODO path must be an absolute path.
//                            File file = new File(path);
//                            if (file.exists()) {
//                                file.delete();
//                            }
//                        }
//                    }
                }
            }
        }

        contentMap.clear();

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

    private void handleItem(SourceImage item, int index) {
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
            logger.info(item + ":::SKIPPED:::[ `COT_ID` is NULL ]");
            return;
        }

        if (imageRepository.findOne(cotId, item.url) != null) {
            increase(MARK_SKIP);
            logger.info(item + ":::SKIPPED:::[ Already Stored! ]");
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
                    logger.info(item + ":::INSERT:::{} [as MAIN] {} - {}", cotId, newImgId, title);
                } else {
                    logger.info(item + ":::INSERT:::{} IMAGE TITLE {} - {}", cotId, newImgId, title);
                }
            } catch(Exception e) {
                increase(MARK_FAIL);
                logger.info(item + ":::FAILED:::[{}]", e.getMessage());
                // TODO insert log message
            }
        }
    }

    private String getTitle(SourceImage item, int index) {
        return item.title + " " + index;
    }
}