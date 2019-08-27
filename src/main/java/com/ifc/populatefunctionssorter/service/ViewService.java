package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.View;
import com.ifc.populatefunctionssorter.repository.ViewDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ViewService {

    public View getViewByName(String viewName, String schema) {
        View view = ViewDAO.getViewByName(viewName, schema);

        if (StringUtils.isEmpty(view.getDefinition())) {
            log.warn("Unable to get the definition of view " + view + ". The result may be invalid");
        }
        return view;
    }

}
