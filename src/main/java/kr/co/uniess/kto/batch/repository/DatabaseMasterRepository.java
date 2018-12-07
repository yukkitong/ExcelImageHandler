package kr.co.uniess.kto.batch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class DatabaseMasterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean hasItem(String cotId) {
        String sql = "select count(*) CNT from DATABASE_MASTER where COT_ID = ?";
        return jdbcTemplate.queryForObject(sql,
                new Object[] { cotId },
                new int[] { Types.VARCHAR },
                Integer.class) > 0;
    }

    public int updateItemImageOnly(String cotId, String imageId1) {
        return updateItemImageOnly(cotId, imageId1, imageId1);
    }

    public int updateItemImageOnly(String cotId, String imageId1, String imageId2) {
        String sql = "update DATABASE_MASTER " +
                "set FIRST_IMAGE = ?, FIRST_IMAGE2 = ? " +
                "where COT_ID = ?";
        return jdbcTemplate.update(sql, imageId1, imageId2, cotId);
    }

    public int createItem(String cotId) {
        String sql = "insert into DATABASE_MASTER (COT_ID) values (?)";
        return jdbcTemplate.update(sql, cotId);
    }

    public int clearMainImages(String cotId) {
        String sql = "update DATABASE_MASTER " +
                "set FIRST_IMAGE = NULL, FIRST_IMAGE2 = NULL " +
                "where COT_ID = ?";
        return jdbcTemplate.update(sql, cotId);
    }

    @SuppressWarnings("unused")
    private String generateId() {
        return RepositoryUtils.generateRandomId();
    }
}