package kr.co.uniess.kto.batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.uniess.kto.batch.service.ConvertService;
import kr.co.uniess.kto.batch.service.DeleteImageWithContentIdService;
import kr.co.uniess.kto.batch.service.ImageManipulateService;

@Configuration
public class AppConfiguration {

    @Value("#{new Boolean('${application.debug}')}")
    private Boolean DEBUG;

    @Bean
    public ConvertService convertService() {
        return new ConvertService();
    }

    @Bean
    public ImageManipulateService imageManipulateService() {
        return new ImageManipulateService(DEBUG);
    }

    @Bean
    public DeleteImageWithContentIdService deleteImageWithContentIdService() {
        return new DeleteImageWithContentIdService(DEBUG);
    }
}