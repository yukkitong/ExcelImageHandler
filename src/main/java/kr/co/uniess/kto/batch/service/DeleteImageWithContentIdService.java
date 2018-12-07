package kr.co.uniess.kto.batch.service;

import java.util.List;

import kr.co.uniess.kto.batch.model.SourceImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.uniess.kto.batch.repository.ImageRepository;

public class DeleteImageWithContentIdService implements BatchService<List<SourceImage>> {
    
    private final Logger logger = LoggerFactory.getLogger(DeleteImageWithContentIdService.class);

    @Autowired
    private ImageRepository imageRepository;

    private BatchService<List<SourceImage>> delegate;

    public DeleteImageWithContentIdService(boolean isDebug) {
        if (isDebug) {
            delegate = list -> {
                for (SourceImage image : list) {
                    logger.info(image + " - DELETED");
                }
            };
        } else {
            delegate = list -> {
                for (SourceImage image : list) {
                    final String contentId = image.contentId;
                    int count = imageRepository.deleteAllImageByContentId(contentId);
                    logger.info(image + " - DELETED [{}]", count);
                }
            };
        }
    }

    @Override
    public void execute(List<SourceImage> list) {
        delegate.execute(list);
    }
}