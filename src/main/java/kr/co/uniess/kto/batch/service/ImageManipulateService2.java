package kr.co.uniess.kto.batch.service;

import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.repository.*;
import kr.co.uniess.kto.batch.model.DestImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("prototype")
public class ImageManipulateService2 implements BatchService<List<SourceImage>>  {

    private final Logger logger = LoggerFactory.getLogger(ImageManipulateService2.class);


    @Autowired
    private ContentMasterRepository contentMasterRepository;

    @Autowired
    private DatabaseMasterRepository databaseMasterRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ExcelImageUploadHistRepository excelImageUploadHistRepository;


    private static final int MARK_SKIP = 0x00;
    private static final int MARK_INSERT = 0xf0;
    private static final int MARK_INSERT_MAIN = 0xf1;
    private static final int MARK_DELETE = 0x01;

    private String eihId;

    private HashMap<String, Integer> counter;

    private static final String MARK_COUNT_SKIP = "SKIPPED";
    private static final String MARK_COUNT_SAVE = "SAVED";
    private static final String MARK_COUNT_FAIL = "FAILED";
    private static final String MARK_COUNT_DEL = "DELETED";


    HashMap<String, Integer> contentTypeCache = new HashMap<>();



    static class CombinedImage {
        String imgId;
        String cotId;
        String url;
        String title;
        int mark;

        @Override
        public String toString() {
            return "{IMG_ID=" + imgId +
                    ", COT_ID=" + cotId +
                    ", TITLE=" + title +
                    ", URL=" + url + "}";
        }
    }

    public ImageManipulateService2() {
        this.eihId = null;
        this.counter = new HashMap<>();
        resetCounter();
    }

    public void setEihId(String eihId) {
        this.eihId = eihId;
    }

    private void resetCounter() {
        contentTypeCache.clear();

        counter.clear();
        counter.put(MARK_COUNT_SKIP, 0);
        counter.put(MARK_COUNT_SAVE, 0);
        counter.put(MARK_COUNT_FAIL, 0);
        counter.put(MARK_COUNT_DEL, 0);
    }

    private void increase(String mark) {
        counter.put(mark, counter.get(mark) + 1);
    }

    private boolean isInsert(int value) {
        return (value & MARK_INSERT) == MARK_INSERT;
    }

    private boolean isMain(int value) {
        return value == MARK_INSERT_MAIN;
    }

    private boolean isDelete(int value) {
        return value == MARK_DELETE;
    }

    @Override
    public void execute(List<SourceImage> list) {
        Map<String, List<SourceImage>> sourceImageMap = new HashMap<>();
        Map<String, List<DestImage>> destImageMap = new HashMap<>();
        for (SourceImage image : list) {
            final String cotId = getCotId(image.getContentId());
            if (cotId != null) {
                if (!sourceImageMap.containsKey(cotId)) {
                    sourceImageMap.put(cotId, new ArrayList<>());
                    destImageMap.put(cotId, new ArrayList<>());
                    List<DestImage> destImages = imageRepository.findAll(cotId);
                    if (destImages != null) {
                        destImageMap.get(cotId).addAll(destImages);
                    }
                }
                sourceImageMap.get(cotId).add(image);
            } else {
                increase(MARK_COUNT_SKIP);
            }
        }

        logger.info("::START:: " + new Date());
        Map<String, List<CombinedImage>> combinedImageMap = getCombinedImageMap(sourceImageMap, destImageMap);
        for (String cotId : combinedImageMap.keySet()) {
            List<CombinedImage> combinedImages = combinedImageMap.get(cotId);
            for (CombinedImage image : combinedImages) {
                if (isInsert(image.mark)) {
                    if (isMain(image.mark)) {
                        try {
                            imageRepository.insertImage(image.imgId, image.cotId, image.title, image.url, false);
                            databaseMasterRepository.updateItemImageOnly(cotId, image.imgId);
                            increase(MARK_COUNT_SAVE);
                            logger.info("::INSERT:: " + image + " [MAIN]");
                        } catch(Exception e) {
                            increase(MARK_COUNT_FAIL);
                            logger.info("::ERROR-INSERT:: " + image + " REASON: " + e.getMessage());
                        }
                    } else {
                        try {
                            imageRepository.insertImage(image.imgId, image.cotId, image.title, image.url, false);
                            increase(MARK_COUNT_SAVE);
                            logger.info("::INSERT:: " + image);
                        } catch(Exception e) {
                            increase(MARK_COUNT_FAIL);
                            logger.info("::ERROR-INSERT:: " + image + " REASON: " + e.getMessage());
                        }
                    }
                } else if (isDelete(image.mark)) {
                    try {
                        imageRepository.deleteImage(image.imgId);
                        // TODO delete file
                        increase(MARK_COUNT_DEL);
                        logger.info("::DELETE:: " + image);
                    } catch(Exception e) {
                        logger.info("::ERROR-DELETE:: " + image + " REASON: " + e.getMessage());
                    }
                } else {
                    increase(MARK_COUNT_SKIP);
                    logger.info("::SKIP:: " + image);
                }
            }
        }
        logger.info("::END:: " + new Date());

        final int saveCount = counter.get(MARK_COUNT_SAVE);
        final int skipCount = counter.get(MARK_COUNT_SKIP);
        final int failCount = counter.get(MARK_COUNT_FAIL);
        if (eihId != null) {
            excelImageUploadHistRepository.updateCount(eihId, saveCount, skipCount, failCount);
        }
    }

    private String getCotId(String contentId) {
        return contentMasterRepository.getCotId(contentId);
    }

    private Map<String, List<CombinedImage>>
    getCombinedImageMap(Map<String, List<SourceImage>> sourceImageMap, Map<String, List<DestImage>> destImageMap) {
        Map<String, List<CombinedImage>> resultMap = new HashMap<>();

        //  Excel Images --- DB Images ---  Results
        //  A  (main)        A              Skip A
        //  B                B              Skip B
        //  C                               Insert C
        //                   D              Delete D

        for (String cotId : sourceImageMap.keySet()) {
            List<SourceImage> sourceImages = sourceImageMap.get(cotId);
            List<DestImage> destImages = destImageMap.get(cotId);

            resultMap.put(cotId, new ArrayList<>());

            int i = 0;
            for (SourceImage image : sourceImages) {
                if (!contains(destImages, image)) {
                    CombinedImage combinedImage = new CombinedImage();
                    combinedImage.imgId = RepositoryUtils.generateRandomId();
                    combinedImage.cotId = cotId;
                    if (image.main) {
                        combinedImage.title = image.getTitle();
                        combinedImage.mark = MARK_INSERT_MAIN;
                    } else {
                        combinedImage.title = image.getTitle() + " " + (++i);
                        combinedImage.mark = MARK_INSERT;
                    }
                    combinedImage.url = image.getUrl();
                    resultMap.get(cotId).add(combinedImage);
                } else {
                    CombinedImage combinedImage = new CombinedImage();
                    combinedImage.imgId = "[UNKNOWN]";
                    combinedImage.cotId = cotId;
                    combinedImage.title = image.getTitle();
                    combinedImage.url = image.getUrl();
                    combinedImage.mark = MARK_SKIP;
                    resultMap.get(cotId).add(combinedImage);
                }
            }

            for (DestImage image : destImages) {
                // NOTE. `숙박`의 경우 침실 이미지가 액셀로 전달되지 않고 있다.
                // 그래서 임시조치로 숙박의 경우는 스킵처리하도록 한다.
                // TODO: 삭제 로직이 반드시 들어가야 한다. 룸이미지를 제외하더라도...
                if (!isAccommodation(cotId) && !contains(sourceImages, image)) {
                    CombinedImage combinedImage = new CombinedImage();
                    combinedImage.imgId = image.getImgId();
                    combinedImage.cotId = cotId;
                    combinedImage.title = image.getImageDescription();
                    combinedImage.url = image.getUrl();
                    combinedImage.mark = MARK_DELETE;
                    resultMap.get(cotId).add(combinedImage);
                }
            }
        }
        return resultMap;
    }


    private boolean isAccommodation(String cotId) {
        Integer contentType = contentTypeCache.get(cotId);
        if (contentType == null) {
            contentType = contentMasterRepository.getContentType(cotId);
            contentTypeCache.put(cotId, contentType);
        }
        return contentType != null && contentType == 32;
    }

    private boolean contains(List<DestImage> images, SourceImage image) {
        if (images == null || images.isEmpty()) {
            return false;
        }
        for (DestImage i : images) {
            if (i.getUrl() != null && i.getUrl().equals(image.getUrl())) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(List<SourceImage> images, DestImage image) {
        if (images == null || images.isEmpty()) {
            return false;
        }
        for (SourceImage i : images) {
            if (i.getUrl() != null && i.getUrl().equals(image.getUrl())) {
                return true;
            }
        }
        return false;
    }
}
