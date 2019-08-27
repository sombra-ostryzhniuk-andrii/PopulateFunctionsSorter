package com.ifc.populatefunctionssorter.repository;

import com.ifc.populatefunctionssorter.entity.View;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import static com.ifc.populatefunctionssorter.repository.providers.JdbcProvider.getJdbcTemplate;

public class ViewDAO {

    public static View getViewByName(String viewName, String schema) {
        return getJdbcTemplate()
                .queryForObject("select viewname as name, " +
                           "       definition as definition, " +
                           "       schemaname as schema " +
                           "from pg_catalog.pg_views " +
                           "where viewname = ? " +
                           "  and schemaname = ?",
                        BeanPropertyRowMapper.newInstance(View.class),
                        viewName, schema);
    }

}
