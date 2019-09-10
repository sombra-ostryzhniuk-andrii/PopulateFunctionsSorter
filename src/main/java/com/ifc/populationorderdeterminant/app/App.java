package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.PopulationSequenceResult;
import com.ifc.populationorderdeterminant.dto.Schema;
import com.ifc.populationorderdeterminant.providers.PropertiesProvider;
import com.ifc.populationorderdeterminant.service.ConsoleResultPrinterService;
import com.ifc.populationorderdeterminant.service.SequenceService;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;

import java.util.*;

public class App {

    private static String[] arguments;

    public static void main(String[] args) {
        try {
            arguments = args;

            SequenceService sequenceService = new SequenceService();
            ResultPrinterService printerService = new ConsoleResultPrinterService();

            System.out.println("Process is running...");

            List<PopulationSequenceResult> results = new ArrayList<>();

            PropertiesProvider.getSchemas().stream()
                    .sorted(Comparator.comparing(Schema::getPopulationOrder))
                    .forEach(schema -> {

                        System.out.println("Processing of the " + schema + " schema...");

                        results.add(sequenceService.getPopulationSequenceResult(schema, results));
                    });

            printerService.print(results);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
