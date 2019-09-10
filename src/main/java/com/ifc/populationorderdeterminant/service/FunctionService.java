package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.providers.ExcludedFunctionsProvider;
import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.repository.FunctionDAO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FunctionService {

    public Set<Function> getAllPopulateFunctionsInSchema(String schema) {
        List<Function> functions = FunctionDAO.getAllPopulateFunctionsInSchema(schema);

        if (CollectionUtils.isEmpty(functions)) {
            throw new RuntimeException("Unable to load functions");
        }

        return functions.stream()
                .flatMap(function -> {

                    if (ExcludedFunctionsProvider.isFunctionExcluded(function)) {
                        return Stream.empty();
                    } else if (StringUtils.isEmpty(function.getDefinition())) {
                        ExcludedFunctionsProvider.addRuntimeExcludedFunction(function,
                                "Unable to load the script of the function. " +
                                        "You may don't have access on this function, change the function owner.");
                        return Stream.empty();
                    } else {
                        return Stream.of(function);
                    }
                })
                .collect(Collectors.toSet());
    }

}
