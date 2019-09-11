package com.ifc.myelinflow.service;

import com.ifc.myelinflow.entity.Function;
import com.ifc.myelinflow.entity.View;
import com.ifc.myelinflow.exceptions.UnableAnalyzeFunctionException;
import com.ifc.myelinflow.repository.ViewDAO;
import com.ifc.myelinflow.utils.RegexEnum;
import com.ifc.myelinflow.utils.RegexUtil;
import com.ifc.myelinflow.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class ViewService {

    public View getViewByFunction(Function function) throws UnableAnalyzeFunctionException {
        final String viewName = getViewNameByFunction(function);
        return getViewByName(viewName, function.getSchema());
    }

    private View getViewByName(String viewName, String schema) throws UnableAnalyzeFunctionException {

        View view = null;

        try {
            view = ViewDAO.getViewByName(viewName, schema);
        } catch (Exception e) {
            throw new UnableAnalyzeFunctionException("Unable to load view " + schema + "." + viewName + ".", e);
        }

        if (view == null) {
            throw new UnableAnalyzeFunctionException("Unable to load view " + schema + "." + viewName + ".");
        } else if (StringUtils.isEmpty(view.getDefinition())) {
            throw new UnableAnalyzeFunctionException("Unable to load the definition of the view " + view + ".");
        }

        return view;
    }

    private String getViewNameByFunction(Function function) throws UnableAnalyzeFunctionException {
        final String pattern = String.format(RegexEnum.FIND_VIEW_NAME_PATTERN.value(), function.getSchema());

        Optional<String> viewNameOptional = RegexUtil.substring(function.getDefinition(), pattern);

        if (!viewNameOptional.isPresent() || StringUtils.isEmpty(viewNameOptional.get())) {
            throw new UnableAnalyzeFunctionException("Function " + function + " doesn't match any "
                    + function.getSchema() + " views.");
        }
        return StringUtil.validateString(viewNameOptional.get());
    }

}
