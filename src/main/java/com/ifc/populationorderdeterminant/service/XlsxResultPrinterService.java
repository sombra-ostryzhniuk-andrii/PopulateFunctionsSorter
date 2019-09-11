package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.dto.PopulationSequenceResult;
import com.ifc.populationorderdeterminant.dto.SourceSchemas;
import com.ifc.populationorderdeterminant.entity.Function;
import com.ifc.populationorderdeterminant.providers.ExcludedFunctionsProvider;
import com.ifc.populationorderdeterminant.providers.PropertiesProvider;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class XlsxResultPrinterService implements ResultPrinterService {

    private static final String FILE_PATH_PROPERTY = "result.file.path";
    private static final String FILE_NAME_PROPERTY = "result.file.name";
    private static final String FILE_TYPE = ".xlsx";

    private static final int FUNCTIONS_LIST_COLUMN = 0;
    private static final int POPULATION_ORDER_COLUMN = 1;
    private static final int EXCLUDED_FUNCTIONS_COLUMN = 5;

    private static final int FUNCTIONS_LIST_COLUMN_WIDTH = 12000;
    private static final int POPULATION_ORDER_COLUMN_WIDTH = 5000;
    private static final int EXCLUDED_FUNCTIONS_COLUMN_WIDTH = 12000;

    @Override
    public void print(List<PopulationSequenceResult> results) {

        Workbook workbook = new HSSFWorkbook();

        for (PopulationSequenceResult result : results) {
            final int startRow = 0;
            int currentRow = startRow;

            Sheet sheet = workbook.createSheet(result.getSchema().getName());
            sheet.setColumnWidth(FUNCTIONS_LIST_COLUMN, FUNCTIONS_LIST_COLUMN_WIDTH);
            sheet.setColumnWidth(POPULATION_ORDER_COLUMN, POPULATION_ORDER_COLUMN_WIDTH);
            sheet.setColumnWidth(EXCLUDED_FUNCTIONS_COLUMN, EXCLUDED_FUNCTIONS_COLUMN_WIDTH);

            String wholeSchemaHeaderValue = "The population order of the whole " + result.getSchema() + " schema";
            currentRow = addPopulationSequenceHeader(workbook, sheet, currentRow, wholeSchemaHeaderValue);
            currentRow = printPopulationSequence(result.getWholeSchemaSequenceSet(), sheet, currentRow);

            for (Map.Entry<SourceSchemas, TreeSet<PopulationSequence>> entry : result.getSourceSchemasSequenceMap().entrySet()) {
                SourceSchemas sourceSchemas = entry.getKey();
                TreeSet<PopulationSequence> populationSequenceSet = entry.getValue();

                currentRow = currentRow + 2;

                String sourceSchemaHeaderValue = "The population order of sources: " + sourceSchemas;
                currentRow = addPopulationSequenceHeader(workbook, sheet, currentRow, sourceSchemaHeaderValue);
                currentRow = printPopulationSequence(populationSequenceSet, sheet, currentRow);
            }


            if (ExcludedFunctionsProvider.isExcludedFunctionsExist()) {
                currentRow = startRow;

                String excludedFunctionsHeader = "Excluded functions";
                currentRow = addHeader(sheet, currentRow, EXCLUDED_FUNCTIONS_COLUMN, excludedFunctionsHeader, getFirstHeaderStyle(workbook));

                Set<Function> configExcludedFunctions =
                        ExcludedFunctionsProvider.getConfigExcludedFunctionsBySchema(result.getSchema().getName());
                String configExcludedFunctionsHeader = "Excluded by the configuration file";

                currentRow = printExcludedFunctions(configExcludedFunctions, currentRow, configExcludedFunctionsHeader, sheet, workbook);

                currentRow = currentRow + 2;

                Set<Function> runtimeExcludedFunctions =
                        ExcludedFunctionsProvider.getRuntimeExcludedFunctionsBySchema(result.getSchema().getName());
                String runtimeExcludedFunctionsHeader = "Unable to analyze";

                printExcludedFunctions(runtimeExcludedFunctions, currentRow, runtimeExcludedFunctionsHeader, sheet, workbook);
            }
        }

        write(workbook);
    }

    private int addPopulationSequenceHeader(Workbook workbook, Sheet sheet, int currentRow, String headerValue) {

        addHeader(sheet, currentRow, FUNCTIONS_LIST_COLUMN, headerValue, getFirstHeaderStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, FUNCTIONS_LIST_COLUMN, POPULATION_ORDER_COLUMN));

        currentRow++;

        addHeader(sheet, currentRow, FUNCTIONS_LIST_COLUMN, "Function", getSecondHeaderStyle(workbook));
        addHeader(sheet, currentRow, POPULATION_ORDER_COLUMN, "Population order", getSecondHeaderStyle(workbook));

        return ++currentRow;
    }

    private int printPopulationSequence(Set<PopulationSequence> populationSequenceSet, Sheet sheet, int currentRow) {

        for (PopulationSequence populationSequence : populationSequenceSet) {
            Row row = getOrCreateRow(sheet, currentRow);

            Cell functionCell = row.createCell(FUNCTIONS_LIST_COLUMN);
            functionCell.setCellValue(populationSequence.getTable().getFunction().toString());

            Cell orderCell = row.createCell(POPULATION_ORDER_COLUMN);
            orderCell.setCellValue(populationSequence.getSequenceNumber());

            currentRow++;
        }

        return currentRow;
    }

    private int printExcludedFunctions(Set<Function> excludedFunctions,
                                       int currentRow,
                                       String headerValue,
                                       Sheet sheet,
                                       Workbook workbook) {

        if (!CollectionUtils.isEmpty(excludedFunctions)) {
            currentRow = addHeader(sheet, currentRow, EXCLUDED_FUNCTIONS_COLUMN, headerValue, getSecondHeaderStyle(workbook));
            currentRow = printFunctionsList(excludedFunctions, sheet, currentRow, EXCLUDED_FUNCTIONS_COLUMN);
        }

        return currentRow;
    }

    private int printFunctionsList(Collection<Function> excludedFunctions, Sheet sheet, int currentRow, int column) {

        for (Function function : excludedFunctions) {
            Row row = getOrCreateRow(sheet, currentRow);

            Cell functionCell = row.createCell(column);
            functionCell.setCellValue(function.toString());

            currentRow++;
        }

        return currentRow;
    }

    private int addHeader(Sheet sheet, int currentRow, int column, String headerValue, CellStyle style) {

        Row headerRow = getOrCreateRow(sheet, currentRow);
        Cell headerCell = headerRow.createCell(column);
        headerCell.setCellStyle(style);
        headerCell.setCellValue(headerValue);

        return ++currentRow;
    }

    private Row getOrCreateRow(Sheet sheet, int rowNumber) {
        Row row = sheet.getRow(rowNumber);
        return row == null ? sheet.createRow(rowNumber) : row;
    }

    private CellStyle getFirstHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        HSSFFont font = ((HSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private CellStyle getSecondHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        HSSFFont font = ((HSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private void write(Workbook workbook) {
        Path path = getResultFilePath();

        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {

            workbook.write(outputStream);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find result file " + path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write result into the file " + path);
        }
    }

    public Path getResultFilePath() {
        final String filePath = PropertiesProvider.getRequiredProperty(FILE_PATH_PROPERTY);
        final String fileName = PropertiesProvider.getRequiredProperty(FILE_NAME_PROPERTY) + FILE_TYPE;
        return Paths.get(filePath, fileName);
    }

}
