package kr.co.uniess.kto.batch.controller;

import java.util.List;

import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.service.ImageManipulateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import kr.co.uniess.kto.batch.service.ImageManipulateService2;

@Controller
public class ExcelImageController implements IController<List<SourceImage>> {

    @Autowired
    private ImageManipulateService2 imageManipulateService;

	public void setEihId(String eihId) {
        imageManipulateService.setEihId(eihId);
	}

    @Override
    public void run(List<SourceImage> list) {
        if (list == null) {
            throw new NullPointerException("source image list is null.");
        }
        list.sort((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.contentId, o2.contentId));
        imageManipulateService.execute(list);
    }
}