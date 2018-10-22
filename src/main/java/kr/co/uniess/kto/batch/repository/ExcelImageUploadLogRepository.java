package kr.co.uniess.kto.batch.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ExcelImageUploadLogRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    
}