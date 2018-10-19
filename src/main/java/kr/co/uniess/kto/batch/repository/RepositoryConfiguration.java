package kr.co.uniess.kto.batch.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import kr.co.uniess.kto.batch.repository.Image;

@Configuration
public class RepositoryConfiguration {

  @Bean
  public RowMapper<Image> getImageRowMapper() {
    return new RowMapper<Image>() {
      public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        Image image = new Image();
        image.setImgId(rs.getString("IMG_ID"));
        image.setCotId(rs.getString("COT_ID"));
        image.setImageDescription(rs.getString("IMAGE_DESCRIPTION"));
        image.setUrl(rs.getString("URL"));
        image.setThumbnail(rs.getInt("IS_THUBNAIL") == 1);
        return image;
      }
    };
  }

  @Bean
  public RowMapper<ContentMaster> getContentMasterRowMapper() {
    return new RowMapper<ContentMaster>() {
      public ContentMaster mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContentMaster content = new ContentMaster();
        return content;
      }
    };
  }
}