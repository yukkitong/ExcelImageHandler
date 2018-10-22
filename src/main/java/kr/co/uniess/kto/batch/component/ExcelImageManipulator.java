package kr.co.uniess.kto.batch.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.XlsReader;
import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.service.BatchService;


@Component
public class ExcelImageManipulator implements Manipulator {

    @Autowired
    private BatchService imageManipulateService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.exit(1);
        }

        if (imageManipulateService == null) System.out.println(">>> imageManipulateService is null");

        final String filePath = args[0];
        XlsReader reader = new XlsReader();
        List<SourceImage> list = reader.read(filePath);
        imageManipulateService.addParameter("list", list);
        imageManipulateService.execute();
    }
}