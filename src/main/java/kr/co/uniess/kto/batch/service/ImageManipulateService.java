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
import kr.co.uniess.kto.batch.repository.ImageRepository;
import kr.co.uniess.kto.batch.repository.RepositoryUtils;

@Service
@Scope("prototype")
public class ImageManipulateService extends AbstractBatchService {

    private final Logger logger = LoggerFactory.getLogger(ImageManipulateService.class);

    private Map<String, String> cacheForContentId = new HashMap<>();

    @Autowired
    private ContentMasterRepository contentMasterRepository;

    @Autowired
    private DatabaseMasterRepository databaseMasterRepository;

    @Autowired
    private ImageRepository imageRepository;

    private boolean isRehearsal;


    public ImageManipulateService() {
        isRehearsal = false;
    }

    public BatchService  rehearsalMode() {
        isRehearsal = true;
        return this;
    }

    private void clearCache() {
        cacheForContentId.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        clearCache();

        List<SourceImage> list = (List<SourceImage>) getParameter("list");
        for (SourceImage item : list) {
            handleItem(item);
        }
    }

    @Transactional
    protected void handleItem(SourceImage item) {
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
                if (item.main) {
                    if (!isRehearsal) {
                        imageRepository.insertImage(imgId, cotId, item.title, item.url, item.main);
                        databaseMasterRepository.updateItemOnlyImage(cotId, imgId);
                    }
                    logger.info(item + " - INSERTED [as MAIN]");
                } else {
                    if (!isRehearsal) {
                        imageRepository.insertImage(imgId, cotId, item.title, item.url, item.main);
                    }
                    logger.info(item + " - INSERTED");
                }
            } catch(Exception e) {
                logger.info(item + " - FAILED");
            }
        }
    }
}