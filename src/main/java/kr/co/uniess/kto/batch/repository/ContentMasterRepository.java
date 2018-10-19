package kr.co.uniess.kto.batch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ContentMasterRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public boolean hasItem(String contentId) {
    String sql = "select count(*) CNT from CONTENT_MASTER where CONTENT_ID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{ contentId }, Integer.class) > 0;
  }

  public String getCotId(String contentId) {
    String sql = "select COT_ID from CONTENT_MASTER where CONTENT_ID = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{ contentId }, String.class);
  }

  /**
   * @param cotId generated ID
   */
  public int insertContentWithOnlyRequiredField(String cotId, String contentId, String title) {
    String sql = "insert into CONTENT_MASTER (CONTENT_ID, COT_ID, TITLE) values (?, ?, ?)";
    return jdbcTemplate.update(sql, contentId, cotId, title);
  }

  @SuppressWarnings("unused")
  private String generateId() {
    return RepositoryUtils.generateRandomId();
  }
}