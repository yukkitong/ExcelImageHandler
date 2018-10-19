package kr.co.uniess.kto.batch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseMasterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean hasItem(String cotId) {
        String sql = "select count(*) CNT from DATABASE_MASTER where COT_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] { cotId }, Integer.class) > 0;
    }

    public int updateItemOnlyImage(String cotId, String imageId1) {
        return updateItemOnlyImage(cotId, imageId1, imageId1);
    }

    public int updateItemOnlyImage(String cotId, String imageId1, String imageId2) {
        String sql = "update DATABASE_MASTER set FIRST_IMAGE = ?, FIRST_IMAGE2 = ? where COT_ID = ?";
        return jdbcTemplate.update(sql, imageId1, imageId2, cotId);
    }

    public int createItem(String cotId) {
        String sql = "insert into DATABASE_MASTER (COT_ID) values (?)";
        return jdbcTemplate.update(sql, cotId);
    }

    @SuppressWarnings("unused")
    private String generateId() {
        return RepositoryUtils.generateRandomId();
    }
}