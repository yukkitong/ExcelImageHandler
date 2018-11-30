package kr.co.uniess.kto.batch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.service.ConvertService;

/**
 * XLS -> CSV
 */
@Controller
public class ConversionController implements IController<List<SourceImage>> {

    @Autowired
    private ConvertService convertService;

    public void setOuputFilename(String filename) {
        convertService.setOutputFilename(filename);
    }

    @Override
    public void run(List<SourceImage> list) throws Exception {
        if (list == null) {
            throw new NullPointerException("source image list is null.");
        }
        list.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.contentId, o2.contentId));
        convertService.execute(list);
    }
}