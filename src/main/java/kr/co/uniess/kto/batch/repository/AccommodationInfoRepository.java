package kr.co.uniess.kto.batch.repository;

import kr.co.uniess.kto.batch.model.RoomImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;

@Repository
public class AccommodationInfoRepository {
    private final static Logger logger = LoggerFactory.getLogger(AccommodationInfoRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RowMapper<RoomImage> roomImageRowMapper;

    public List<RoomImage> getRoomImageIds(String cotId) {
        String query = "select COT_ID, ROOM_IMG1, ROOM_IMG2, ROOM_IMG3, ROOM_IMG4, ROOM_IMG5" +
                "from ACCOMMODATION_INFO where COT_ID = ?";
        try {
            return jdbcTemplate.query(
                    query,
                    new Object[] { cotId },
                    new int[] { Types.VARCHAR },
                    roomImageRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean containsRoomImage(String cotId, String imgId) {
        String query = "select count(*) from ACCOMMODATION_INFO where COT_ID = ? " +
                "and (ROOM_IMG1 = ? " +
                "or ROOM_IMG2 = ?" +
                "or ROOM_IMG3 = ?" +
                "or ROOM_IMG4 = ?" +
                "or ROOM_IMG5 = ?)";
        Integer count = jdbcTemplate.queryForObject(
                    query,
                    new Object[] { cotId, imgId, imgId, imgId, imgId, imgId },
                    new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR },
                    Integer.class);
        return count != null && count > 0;
    }
}
