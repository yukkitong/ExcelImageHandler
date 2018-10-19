package kr.co.uniess.kto.batch.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.uniess.kto.batch.model.ExcelImage;

@Configuration
public class ServiceConfiguration {

  @Bean
  public ImageManipulateService<ExcelImage> imageManipulateService() {
    return new ImageManipulateServiceImpl().rehearsalMode();
  }
}