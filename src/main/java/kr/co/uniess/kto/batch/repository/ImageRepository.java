package kr.co.uniess.kto.batch.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kr.co.uniess.kto.batch.repository.model.Image;

@Repository
public class ImageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Lazy
    private RowMapper<Image> imageRowMapper;

    /**
     * 등록된 이미지가 있는지 확인한다.
     * @param cotId
     * @param url
     * @return boolean
     */
    public boolean hasItem(String cotId, String url) {
        String query = "select count(*) CNT from IMAGE where COT_ID = ? and URL = ?";
        return jdbcTemplate.queryForObject(query, new Object[] { cotId, url }, Integer.class) > 0;
    }

    /**
     * COT_ID 가 일치하는 하나의 이미지를 반환한다.
     * @param cotId
     * @return object
     */
    public Image findFirst(String cotId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where COT_ID = ? limit 1";
        return jdbcTemplate.queryForObject(query, new Object[] { cotId }, imageRowMapper);
    }

    public List<Image> findAll(String cotId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL from IMAGE where COT_ID = ?"; // thumbnail
        return jdbcTemplate.queryForList(query, new Object[] { cotId }, Image.class);
    }

    /**
     * IMG_ID 가 일치하는 하나의 이미지를 반환한다.
     * @param imgId
     * @return Image object
     */
    public Image findExact(String imgId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where IMG_ID = ?";
        return jdbcTemplate.queryForObject(query, new Object[] { imgId }, imageRowMapper);
    }

    /**
     * 이미지를 등록한다.
     * @param imgId
     * @param cotId
     * @param desc
     * @param url
     * @param isThumbnail
     * @return integer
     */
    public int insertImage(String imgId, String cotId, String desc, String url, boolean isThumbnail) {
        String sql = "insert into IMAGE (IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, imgId, cotId, desc, url, isThumbnail ? 1 : 0);
    }

    /**
     * 등록된 이미지 삭제
     * @param imgId image uuid
     * @param url nullable url string
     * @return integer
     */
    public int deleteImage(String imgId, String url) {
        String sql = "delete from IMAGE where IMG_ID=?";
        if (url != null) {
            sql = sql + " and URL=?";
            return jdbcTemplate.update(sql, imgId, url);
        }
        return jdbcTemplate.update(sql, imgId);
    }

    public int deleteImage(String imgId) {
        return deleteImage(imgId, null);
    }

    public int deleteAllImageByCotId(String cotId, String url) {
        String sql = "delete from IMAGE where COT_ID=?";
        if (url != null) {
            sql = sql + " and URL=?";
            return jdbcTemplate.update(sql, cotId, url);
        }
        return jdbcTemplate.update(sql, cotId, url);
    }

    public int deleteAllImageByCotId(String cotId) {
        return deleteAllImageByCotId(cotId, null);
    }

    public int deleteAllImageByContentId(String contentId) {
        String sql = "delete from IMAGE where COT_ID = (select COT_ID from CONTENT_MASTER where CONTENT_ID = ?)";
        return jdbcTemplate.update(sql, contentId);
    }
}