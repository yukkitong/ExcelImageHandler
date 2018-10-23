package kr.co.uniess.kto.batch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExcelImageUploadHistRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int updateCount(String eihId, int saveCount, int skipCount, int failCount) {
        String sql = "update EXCEL_IMG_UPLOAD_HIST set SAVE_ROW=?, SKIP_ROW=?, FAIL_ROW=? where EIH_ID=?";
        return jdbcTemplate.update(sql, new Object[] { saveCount, skipCount, failCount, eihId });
    }
}