package com.ifc.myelinflow.repository;

import com.ifc.myelinflow.entity.Function;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.List;

import static com.ifc.myelinflow.providers.JdbcProvider.getJdbcTemplate;

public class FunctionDAO {

    public static List<Function> getAllPopulateFunctionsInSchema(String schema) {
        return getJdbcTemplate()
                .query("select routine_name as name, " +
                           "       routine_definition as definition, " +
                           "       specific_schema as schema " +
                           "from information_schema.routines " +
                           "where specific_schema = ? " +
                           "  and routine_name like 'populate%' " +
                           "  and routine_name not like 'populateuser%'",
                        BeanPropertyRowMapper.newInstance(Function.class),
                        schema);
    }

}
