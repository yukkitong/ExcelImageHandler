package kr.co.uniess.kto.batch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class ContentMasterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean hasItem(String contentId) {
        String sql = "select count(*) CNT from CONTENT_MASTER where CONTENT_ID = ?";
        return jdbcTemplate.queryForObject(sql,
                new Object[] { contentId },
                new int[] { Types.VARCHAR },
                Integer.class) > 0;
    }

    public String getCotId(String contentId) {
        try {
            String sql = "select COT_ID from CONTENT_MASTER where CONTENT_ID = ?";
            return jdbcTemplate.queryForObject(sql,
                    new Object[] { contentId },
                    new int[] { Types.VARCHAR },
                    String.class);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * @param cotId generated ID
     */
    public int insertContentWithOnlyRequiredField(String cotId, String contentId, String title) {
        String sql = "insert into CONTENT_MASTER (" +
                "CONTENT_ID, COT_ID, TITLE" +
                ") values (?, ?, ?)";
        return jdbcTemplate.update(sql, contentId, cotId, title);
    }

    public Integer getContentType(String cotId) {
        try {
            String sql = "select CONTENT_TYPE from CONTENT_MASTER where COT_ID = ?";
            return jdbcTemplate.queryForObject(sql,
                    new Object[] { cotId },
                    new int[] { Types.INTEGER },
                    Integer.class);
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }
}