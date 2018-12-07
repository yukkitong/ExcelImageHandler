package kr.co.uniess.kto.batch.repository;

import kr.co.uniess.kto.batch.model.DestImage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public RowMapper<DestImage> getImageRowMapper() {
        return (rs, rowNum) -> {
            DestImage image = new DestImage();
            image.setImgId(rs.getString("IMG_ID"));
            image.setCotId(rs.getString("COT_ID"));
            image.setImageDescription(rs.getString("IMAGE_DESCRIPTION"));
            image.setUrl(rs.getString("URL"));
            image.setThumbnail(rs.getInt("IS_THUBNAIL") == 1);
            image.setPath(rs.getString("IMAGE_PATH"));
            return image;
        };
    }

    @Bean
    public RowMapper<ContentMaster> getContentMasterRowMapper() {
        return (rs, rowNum) -> {
            ContentMaster content = new ContentMaster();
            return content;
        };
    }
}