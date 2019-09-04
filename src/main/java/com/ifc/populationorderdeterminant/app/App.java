package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.Result;
import com.ifc.populationorderdeterminant.dto.Schema;
import com.ifc.populationorderdeterminant.service.ConsoleResultPrinterService;
import com.ifc.populationorderdeterminant.service.SequenceService;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class App {

    private static String[] arguments;

    public static void main(String[] args) {
        try {
            arguments = args;

            SequenceService sequenceService = new SequenceService();
            ResultPrinterService printerService = new ConsoleResultPrinterService();

            System.out.println("Process is running...");

            List<Result> results = new ArrayList<>();

            PropertiesProvider.getSchemas().stream()
                    .sorted(Comparator.comparing(Schema::getPopulationOrder))
                    .forEach(schema -> {

                        System.out.println("Processing of the " + schema + " schema...");

                        results.add(sequenceService.getPopulationSequenceResult(schema, results));
                    });

            printerService.print(results);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
