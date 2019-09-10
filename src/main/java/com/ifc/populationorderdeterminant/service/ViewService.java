package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.entity.View;
import com.ifc.populationorderdeterminant.exceptions.UnableAnalyzeFunctionException;
import com.ifc.populationorderdeterminant.repository.ViewDAO;
import com.ifc.populationorderdeterminant.utils.RegexEnum;
import com.ifc.populationorderdeterminant.utils.RegexUtil;
import com.ifc.populationorderdeterminant.utils.StringUtil;
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
