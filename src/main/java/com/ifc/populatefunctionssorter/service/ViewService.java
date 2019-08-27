package com.ifc.populatefunctionssorter.service;

import com.ifc.populatefunctionssorter.entity.View;
import com.ifc.populatefunctionssorter.repository.ViewDAO;
import org.apache.commons.lang3.StringUtils;

public class ViewService {

    private static final String HINT = " Add the function of the view to a list of excluded functions in the configuration file.";

    public View getViewByName(String viewName, String schema) {

        View view = null;

        try {
            view = ViewDAO.getViewByName(viewName, schema);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load view " + schema + "." + viewName + "." + HINT, e);
        }

        if (view == null) {
            throw new RuntimeException("Unable to load view " + schema + "." + viewName + "." + HINT);
        } else if (StringUtils.isEmpty(view.getDefinition())) {
            throw new RuntimeException("Unable to get the definition of the view " + view + "." + HINT);
        }

        return view;
    }

}
