package kr.co.uniess.kto.batch.repository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import kr.co.uniess.kto.batch.repository.model.Image;

import org.springframework.jdbc.core.RowMapper;

@Repository
public class ImageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Lazy
    private RowMapper<Image> imageRowMapper;

    /**
     * 등록된 이미지가 있는지 확인한다.
     * 
     * @param cotId
     * @param url
     * @return
     */
    public boolean hasItem(String cotId, String url) {
        String query = "select count(*) CNT from IMAGE where COT_ID = ? and URL = ?";
        return jdbcTemplate.queryForObject(query, new Object[] { cotId, url }, Integer.class) > 0;
    }

    /**
     * COT_ID 가 일치하는 하나의 이미지를 반환한다.
     */
    public Image findFirst(String cotId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where COT_ID = ? limit 1";
        return jdbcTemplate.queryForObject(query, new Object[] { cotId }, imageRowMapper);
    }

    /**
     * IMG_ID 가 일치하는 하나의 이미지를 반환한다.
     */
    public Image findExact(String imgId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where IMG_ID = ? limit 1";
        return jdbcTemplate.queryForObject(query, new Object[] { imgId }, imageRowMapper);
    }

    /**
     * 이미지를 등록한다.
     * 
     * @param imgId
     * @param cotId
     * @param desc
     * @param url
     * @param isThumbnail
     * @return
     */
    public int insertImage(String imgId, String cotId, String desc, String url, boolean isThumbnail) {
        String sql = "insert into IMAGE (IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, imgId, cotId, desc, url, isThumbnail ? 1 : 0);
    }

    /**
     * 이미지를 등록한다. 이때 이미지ID(IMG_ID)는 자동생성한다.
     * 
     * @param cotId
     * @param desc
     * @param url
     * @param isThumbnail
     * @return
     */
    public int insertImageWithGenId(String cotId, String desc, String url, boolean isThumbnail) {
        return insertImage(generateId(), cotId, desc, url, isThumbnail);
    }

    /**
     * UUID 생성 (ID로 활용한다.)
     */
    private String generateId() {
        return RepositoryUtils.generateRandomId();
    }
}