package kr.co.uniess.kto.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.uniess.kto.batch.model.ExcelImage;
import kr.co.uniess.kto.batch.service.BatchService;
import kr.co.uniess.kto.batch.service.ImageManipulateService;
import kr.co.uniess.kto.batch.service.XlsToCsvConversionService;
import kr.co.uniess.kto.batch.component.OutputFilenameGenerator;
import kr.co.uniess.kto.batch.component.CsvOutputFilenameGenerator;

@Configuration
public class AppConfiguration {

    @Bean
    public BatchService xlsToCsvConversionService() {
        return new XlsToCsvConversionService();
    }

    @Bean
    public BatchService imageManipulateService() {
        return new ImageManipulateService().rehearsalMode();
    }

    @Bean
    public OutputFilenameGenerator csvOutputFilenameGenerator() {
        return new CsvOutputFilenameGenerator();
    }
}