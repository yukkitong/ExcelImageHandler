package kr.co.uniess.kto.batch.service;

import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.repository.ContentMasterRepository;
import kr.co.uniess.kto.batch.repository.DatabaseMasterRepository;
import kr.co.uniess.kto.batch.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Scope("prototype")
public class CourseImageManipulateService implements BatchService<List<SourceImage>> {

    private final Logger logger = LoggerFactory.getLogger(CourseImageManipulateService.class);

    @Autowired
    private ContentMasterRepository contentMasterRepository;

    @Autowired
    private DatabaseMasterRepository databaseMasterRepository;

    @Autowired
    private ImageRepository imageRepository;

    private String getCotId(String contentId) {
        return contentMasterRepository.getCotId(contentId);
    }

    private int getContentTypeId(String cotId) {
        return contentMasterRepository.getContentType(cotId);
    }

    @Override
    public void execute(List<SourceImage> list) {
        logger.info("::START:: " + new Date());
        for (SourceImage image : list) {
            if (image.main) {
                String cotId = getCotId(image.getContentId());
                if (cotId != null) {
                    int contentTypeId = getContentTypeId(cotId);
                    if (contentTypeId == 25) {
                        String imageId = imageRepository.findOne(cotId, image.getUrl());
                        databaseMasterRepository.updateItemImageOnly(cotId, imageId);
                        logger.info("::UPDATE:: " + image);
                    } else {
                        logger.info("::SKIP:: " + image + ", [contentType is not a course " + contentTypeId + "]");
                    }
                } else {
                    logger.info("::SKIP:: " + image + ", [cotId is null]");
                }
            } else {
                logger.info("::SKIP:: " + image + ", [NOT A MAIN IMAGE]");
            }
        }
        logger.info("::END:: " + new Date());
    }
}
