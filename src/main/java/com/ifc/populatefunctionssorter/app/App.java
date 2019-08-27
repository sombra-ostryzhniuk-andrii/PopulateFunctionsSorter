package com.ifc.populatefunctionssorter.app;

import com.ifc.populatefunctionssorter.dao.JdbcProvider;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.LinkedList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        SqlRowSet rs = JdbcProvider.getJdbcTemplate().queryForRowSet(
                "select cat_definition\n" +
                "from datastaging.category");

        List<String> list = new LinkedList<>();
        while (rs.next()) {
            list.add(rs.getString(1));
        }

        list.forEach(System.out::println);
    }

}
