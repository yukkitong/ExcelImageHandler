package kr.co.uniess.kto.batch.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.co.uniess.kto.batch.CsvReader;
import kr.co.uniess.kto.batch.model.SourceImage;
import kr.co.uniess.kto.batch.service.BatchService;

@Component
public class CsvImageManipulator implements Manipulator {

    @Autowired
    private BatchService imageManipulateService;

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            System.exit(1);
        }

        final String filePath = args[0];
        List<SourceImage> list = CsvReader.read(filePath);
        if (list == null) {
            System.exit(1);
        }
        
        imageManipulateService.addParameter("list", list);
        imageManipulateService.execute();
    }
}