package com.kdonova4.grabit.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class KnownGoodState {

    @Autowired
    JdbcTemplate jdbcTemplate;


    void set() {
        jdbcTemplate.execute("call grab_it_test.set_known_good_state();");
    }
}
