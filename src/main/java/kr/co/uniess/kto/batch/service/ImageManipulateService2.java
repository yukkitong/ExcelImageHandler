package kr.co.uniess.kto.batch.service;

import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.repository.*;
import kr.co.uniess.kto.batch.model.DestImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;
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

    @Autowired
    private AccommodationInfoRepository accommodationInfoRepository;


    private static final int MARK_SKIP = 0x00;
    private static final int MARK_INSERT = 0xf0;
    private static final int MARK_INSERT_MAIN = 0xff;
    private static final int MARK_MAIN = 0x0f;
    private static final int MARK_DELETE = 0x01;

    private String eihId;

    private HashMap<String, Integer> counter;

    private static final String MARK_COUNT_SKIP = "SKIPPED";
    private static final String MARK_COUNT_SAVE = "SAVED";
    private static final String MARK_COUNT_FAIL = "FAILED";
    private static final String MARK_COUNT_DEL = "DELETED";


    private HashMap<String, Integer> contentTypeCache = new HashMap<>();

    @Value("${image.upload.path}")
    private String imageLocation;


    static class CombinedImage {
        String imgId;
        String cotId;
        String url;
        String title;
        String path;
        int mark;

        @Override
        public String toString() {
            return "{IMG_ID=" + imgId +
                    ", COT_ID=" + cotId +
                    ", TITLE=" + title +
                    ", PATH=" + path +
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
        return (value & MARK_MAIN) == MARK_MAIN;
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
            // System.out.print(".");
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
                        logger.info("::DELETE:: " + image);
                        if (image.path != null && !image.path.isEmpty()) {
                            File imageFile = new File(imageLocation + image.path);
                            if (imageFile.exists() && imageFile.delete()) {
                                logger.info("::DELETE-FILE:: " + imageFile.getAbsolutePath());
                            }
                        }
                        increase(MARK_COUNT_DEL);
                    } catch(Exception e) {
                        logger.info("::ERROR-DELETE:: " + image + " REASON: " + e.getMessage());
                    }
                } else if (isMain(image.mark)) {
                    try {
                        databaseMasterRepository.updateItemImageOnly(cotId, image.imgId);
                        logger.info("::UPDATE:: " + image + " [MAIN]");
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

        //  Excel Images (source) --- DB Images (dest) ---  Results
        //  A  (main)                 A                     Skip A
        //  B                         B                     Skip B
        //  C                                               Insert C
        //                            D                     Delete D

        for (String cotId : sourceImageMap.keySet()) {
            List<SourceImage> sourceImages = sourceImageMap.get(cotId);
            List<DestImage> destImages = destImageMap.get(cotId);

            resultMap.put(cotId, new ArrayList<>());

            int i = 0;
            for (SourceImage image : sourceImages) {
                if (isCourse(cotId)) {
                    if (image.main) {
                        CombinedImage combinedImage = new CombinedImage();
                        combinedImage.imgId = RepositoryUtils.generateRandomId();
                        combinedImage.cotId = cotId;
                        combinedImage.title = image.getTitle();
                        combinedImage.mark = MARK_MAIN;
                        combinedImage.url = image.getUrl();
                        resultMap.get(cotId).add(combinedImage);
                    }
                } else {
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
            }

            for (DestImage image : destImages) {
                if (!contains(sourceImages, image)) {
                    if (isAccommodation(cotId)) {
                        // NOTE. `숙박`의 경우 침실 이미지가 액셀로 전달되지 않고 있다.
                        if (!accommodationInfoRepository.containsRoomImage(cotId, image.getImgId())) {
                            CombinedImage combinedImage = new CombinedImage();
                            combinedImage.imgId = image.getImgId();
                            combinedImage.cotId = cotId;
                            combinedImage.title = image.getImageDescription();
                            combinedImage.url = image.getUrl();
                            combinedImage.path = image.getPath();
                            combinedImage.mark = MARK_DELETE;
                            resultMap.get(cotId).add(combinedImage);
                        }
                    } else {
                        CombinedImage combinedImage = new CombinedImage();
                        combinedImage.imgId = image.getImgId();
                        combinedImage.cotId = cotId;
                        combinedImage.title = image.getImageDescription();
                        combinedImage.url = image.getUrl();
                        combinedImage.path = image.getPath();
                        combinedImage.mark = MARK_DELETE;
                        resultMap.get(cotId).add(combinedImage);
                    }
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


    private boolean isCourse(String cotId) {
        Integer contentType = contentTypeCache.get(cotId);
        if (contentType == null) {
            contentType = contentMasterRepository.getContentType(cotId);
            contentTypeCache.put(cotId, contentType);
        }
        return contentType != null && contentType == 25;
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
