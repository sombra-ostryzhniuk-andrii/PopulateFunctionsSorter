package com.ifc.populationorderdeterminant.service;

import com.ifc.populationorderdeterminant.dto.PopulationSequence;
import com.ifc.populationorderdeterminant.dto.PopulationSequenceResult;
import com.ifc.populationorderdeterminant.providers.PropertiesProvider;
import com.ifc.populationorderdeterminant.service.interfaces.ResultPrinterService;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class XlsxResultPrinterService implements ResultPrinterService {

    private static final String FILE_PATH_PROPERTY = "result.file.path";
    private static final String FILE_NAME_PROPERTY = "result.file.name";
    private static final String FILE_TYPE = ".xlsx";

    @Override
    public void print(List<PopulationSequenceResult> results) {

        Workbook workbook = new HSSFWorkbook();

        for (PopulationSequenceResult result : results) {
            int startRow = 0;
            int currentRow = startRow;

            Sheet sheet = workbook.createSheet(result.getSchema().getName());
            sheet.setColumnWidth(0, 12000);
            sheet.setColumnWidth(1, 5000);

            currentRow = buildWholeSchemaSequenceHeader(workbook, sheet, result, currentRow);
            currentRow = buildPopulationSequence(result.getWholeSchemaSequenceSet(), sheet, currentRow);

        }

        write(workbook);
    }

    private int buildWholeSchemaSequenceHeader(Workbook workbook,
                                               Sheet sheet,
                                               PopulationSequenceResult result,
                                               int currentRow) {

        Row firstHeader = sheet.createRow(currentRow);
        Cell firstHeaderCell = firstHeader.createCell(0);
        firstHeaderCell.setCellStyle(getFirstHeaderStyle(workbook));
        firstHeaderCell.setCellValue("The population order of the whole " + result.getSchema() + " schema");
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow,0,1));

        Row secondHeader = sheet.createRow(currentRow + 1);
        Cell secondHeaderCell0 = secondHeader.createCell(0);
        Cell secondHeaderCell1 = secondHeader.createCell(1);

        secondHeaderCell0.setCellStyle(getSecondHeaderStyle(workbook));
        secondHeaderCell1.setCellStyle(getSecondHeaderStyle(workbook));

        secondHeaderCell0.setCellValue("Function");
        secondHeaderCell1.setCellValue("Population order");

        return currentRow + 2;
    }

    private int buildPopulationSequence(Set<PopulationSequence> populationSequenceSet,
                                        Sheet sheet,
                                        int currentRow) {

        for (PopulationSequence populationSequence : populationSequenceSet) {
            Row row = sheet.createRow(currentRow);

            Cell functionCell = row.createCell(0);
            functionCell.setCellValue(populationSequence.getTable().getFunction().toString());

            Cell orderCell = row.createCell(1);
            orderCell.setCellValue(populationSequence.getSequenceNumber());

            currentRow++;
        }

        return currentRow;
    }

    private void write(Workbook workbook) {
        final String filePath = PropertiesProvider.getRequiredProperty(FILE_PATH_PROPERTY);
        final String fileName = PropertiesProvider.getRequiredProperty(FILE_NAME_PROPERTY) + FILE_TYPE;

        Path path = Paths.get(filePath, fileName);

        try (FileOutputStream outputStream = new FileOutputStream(path.toFile())) {

            workbook.write(outputStream);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find result file " + path);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write result into the file " + path);
        }
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

}
