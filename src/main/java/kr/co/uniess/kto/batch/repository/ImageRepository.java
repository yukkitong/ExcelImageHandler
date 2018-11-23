package kr.co.uniess.kto.batch.repository;

import java.util.Arrays;
import java.util.List;

import kr.co.uniess.kto.batch.XlsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kr.co.uniess.kto.batch.repository.model.Image;

@Repository
public class ImageRepository {
    private final static Logger logger = LoggerFactory.getLogger(ImageRepository.class);

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
    public String findOne(String cotId, String url) {
        String query = "select IMG_ID from IMAGE where COT_ID = ? and URL = ? limit 1";
        try {
            logger.trace(">>> " + query);
            logger.trace(">>> " + Arrays.toString(new Object[] {cotId, url}));
            return jdbcTemplate.queryForObject(query, new Object[]{ cotId, url }, String.class);
        } catch (EmptyResultDataAccessException e) {
          return null;
        }
    }

    /**
     * COT_ID 가 일치하는 하나의 이미지를 반환한다.
     * @param cotId
     * @return object
     */
    public Image findFirst(String cotId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where COT_ID = ? limit 1";
        try {
            logger.trace(">>> " + query);
            logger.trace(">>> " + cotId);
            return jdbcTemplate.queryForObject(query, new Object[]{ cotId }, imageRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Image> findAll(String cotId) {
        String query = "select IMG_ID, COT_ID, IMAGE_DESCRIPTION, URL from IMAGE where COT_ID = ?"; // thumbnail
        try {
            logger.trace(">>> " + query);
            logger.trace(">>> " + cotId);
            return jdbcTemplate.query(query, new Object[] { cotId }, imageRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * IMG_ID 가 일치하는 하나의 이미지를 반환한다.
     * @param imgId
     * @return Image object
     */
    public Image findExact(String imgId) {
        String sql = "select IMG_ID, COT_ID, IMAGE_PATH, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where IMG_ID = ?";
        try {
            logger.trace(">>> " + sql);
            logger.trace(">>> " + imgId);
            return jdbcTemplate.queryForObject(sql, new Object[] { imgId }, imageRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
        logger.trace(">>> " + sql);
        logger.trace(">>> " + Arrays.toString(new Object[] {imgId, cotId, desc, url, isThumbnail}));
        return jdbcTemplate.update(sql, imgId, cotId, desc, url, isThumbnail ? 1 : 0);
    }

    public int updateImageTitle(String imgId, String title) {
        String sql = "update IMAGE set IMAGE_DESCRIPTION = ? where IMG_ID = ?";
        logger.trace(">>> " + sql);
        logger.trace(">>> " + Arrays.toString(new Object[] {imgId, title}));
        return jdbcTemplate.update(sql, title, imgId);
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
            logger.trace(">>> " + sql);
            logger.trace(">>> " + Arrays.toString(new Object[] {imgId, url}));
            return jdbcTemplate.update(sql, imgId, url);
        }
        logger.trace(">>> " + sql);
        logger.trace(">>> " + imgId);
        return jdbcTemplate.update(sql, imgId);
    }

    public int deleteImage(String imgId) {
        return deleteImage(imgId, null);
    }

    public int deleteAllImageByCotId(String cotId, String url) {
        String sql = "delete from IMAGE where COT_ID=?";
        if (url != null) {
            sql = sql + " and URL=?";
            logger.trace(">>> " + sql);
            logger.trace(">>> " + Arrays.toString(new Object[] {cotId, url}));
            return jdbcTemplate.update(sql, cotId, url);
        }
        logger.trace(">>> " + sql);
        logger.trace(">>> " + cotId);
        return jdbcTemplate.update(sql, cotId);
    }

    public int deleteAllImageByCotId(String cotId) {
        return deleteAllImageByCotId(cotId, null);
    }

    public int deleteAllImageByContentId(String contentId) {
        String sql = "delete from IMAGE where COT_ID = (select COT_ID from CONTENT_MASTER where CONTENT_ID = ?)";
        logger.trace(">>> " + sql);
        logger.trace(">>> " + contentId);
        return jdbcTemplate.update(sql, contentId);
    }

    public List<Image> selectDeleteTarget(String contentId, String... urls) {
        if (urls == null || urls.length == 0) {
            return null;
        }

        String sql = "select IMG_ID, COT_ID, IMAGE_PATH, IMAGE_DESCRIPTION, URL, IS_THUBNAIL from IMAGE where COT_ID = COTID(?) " +
                "and URL not in (" +
                    getQuestionMark(urls.length) +
                ")";

        int i = 0;
        Object[] params = new Object[urls.length + 1];
        params[i ++] = contentId;
        for (String url : urls) {
            params[i ++] = url;
        }

        logger.trace(">>> " + sql);
        logger.trace(">>> " + Arrays.toString(params));
        try {
            return jdbcTemplate.query(sql, params, imageRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int deleteAllImageExcept(String contentId, String... urls) {
        String sql = "delete from IMAGE where IMG_ID IN (" +
                "select IMG_ID from IMAGE where COT_ID = COTID(?) " +
                "and URL not in (" +
                    getQuestionMark(urls.length) +
                "))";

        int i = 0;
        Object[] params = new Object[urls.length + 1];
        params[i ++] = contentId;
        for (String url : urls) {
            params[i ++] = url;
        }

        logger.trace(">>> " + sql);
        logger.trace(">>> " + Arrays.toString(params));
        return jdbcTemplate.update(sql, params);
    }

    public int deleteImages(String... imgIds) {
        String sql = "delete from IMAGE where IMG_ID in (" + getQuestionMark(imgIds.length) + ")";
        logger.trace(">>> " + sql);
        logger.trace(">>> " + Arrays.toString(imgIds));
        return jdbcTemplate.update(sql, imgIds);
    }

    private String getQuestionMark(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i ++) {
            builder.append("?");
            if (i < len - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }
}