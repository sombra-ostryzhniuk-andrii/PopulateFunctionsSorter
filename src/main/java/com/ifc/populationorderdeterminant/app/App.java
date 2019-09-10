package com.ifc.populationorderdeterminant.app;

import com.ifc.populationorderdeterminant.dto.PopulationSequenceResult;
import com.ifc.populationorderdeterminant.dto.Schema;
import com.ifc.populationorderdeterminant.providers.PropertiesProvider;
import com.ifc.populationorderdeterminant.service.ConsoleResultPrinterService;
import com.ifc.populationorderdeterminant.service.SequenceService;
import com.ifc.populationorderdeterminant.service.XlsxResultPrinterService;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;

import java.util.*;

public class App {

    private static String[] arguments;

    public static void main(String[] args) {
        try {
            arguments = args;

            SequenceService sequenceService = new SequenceService();
            ResultPrinterService consolePrinterService = new ConsoleResultPrinterService();
            XlsxResultPrinterService xlsxPrinterService = new XlsxResultPrinterService();

            System.out.println("Process is running...");

            List<PopulationSequenceResult> results = new ArrayList<>();

            PropertiesProvider.getSchemas().stream()
                    .sorted(Comparator.comparing(Schema::getPopulationOrder))
                    .forEach(schema -> {

                        System.out.println("Processing of the " + schema + " schema...");

                        results.add(sequenceService.getPopulationSequenceResult(schema, results));
                    });

            consolePrinterService.print(results);

            try {
                xlsxPrinterService.print(results);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.out.println("Printing result to console...");

                consolePrinterService.print(results);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("Finished successfully!");
    }

    public static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
