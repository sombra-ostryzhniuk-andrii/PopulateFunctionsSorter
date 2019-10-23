package com.ifc.myelinflow.app;

import com.ifc.myelinflow.dto.PopulationSequenceResult;
import com.ifc.myelinflow.dto.Schema;
import com.ifc.myelinflow.providers.PropertiesProvider;
import com.ifc.myelinflow.service.ConsoleResultPrinterService;
import com.ifc.myelinflow.service.SequenceService;
import com.ifc.myelinflow.service.XlsxResultPrinterService;
import com.ifc.myelinflow.service.interfaces.ResultPrinterService;

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

            try {
                xlsxPrinterService.print(results);
                System.out.println("Look for the result in the file: " + xlsxPrinterService.getResultFilePath());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.out.println("Printing result to console...");
                consolePrinterService.print(results);
            }

            System.out.println("Finished successfully!");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Finished unsuccessfully.");
        }
    }

    public static String[] getArguments() {
        if (arguments == null) {
            throw new RuntimeException("Arguments have not been initialized");
        }
        return arguments;
    }
}
