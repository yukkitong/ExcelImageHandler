package kr.co.uniess.kto.batch.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.XlsReader;
import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.service.BatchService;

/**
 * XLS -> CSV
 */
@Component
public class ConversionManipulator implements Manipulator {

    @Autowired
    private BatchService xlsToCsvConversionService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.exit(1);
        }

        final String filePath = args[0];
        XlsReader reader = new XlsReader();
        List<SourceImage> list = reader.read(filePath);
        xlsToCsvConversionService.addParameter("list", list);
        xlsToCsvConversionService.addParameter("file", filePath);
        xlsToCsvConversionService.execute();
    }
}