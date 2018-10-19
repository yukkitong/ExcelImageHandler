package kr.co.uniess.kto.batch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.uniess.kto.batch.model.ExcelImage;
import kr.co.uniess.kto.batch.service.ImageManipulateService;
import kr.co.uniess.kto.batch.service.ImageManipulateServiceImpl;

@Configuration
public class AppConfiguration {

  @Bean
  public ImageManipulateService<ExcelImage> imageManipulateService() {
    return new ImageManipulateServiceImpl().rehearsalMode();
  }
}