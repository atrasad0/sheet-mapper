package br.com.sheetmapper.mapping.impl;

import br.com.sheetmapper.exception.SheetModelMapperException;
import br.com.sheetmapper.mapping.SheetModelMapper;
import br.com.sheetmapper.support.CellValueWriter;
import br.com.sheetmapper.support.ModelValueResolver;
import br.com.sheetmapper.support.SheetConfigUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;

public class SheetModelMapperImpl implements SheetModelMapper {
    private static final Logger log = LoggerFactory.getLogger(SheetModelMapperImpl.class);
    @Override
    public <T> byte[] createSheet(Collection<T> models) {
        if (models == null || models.isEmpty()) {
            log.error("The collection of models is null or empty.");
            throw new IllegalArgumentException("The collection of models cannot be null or empty.");
        }

        Class<?> clazz = models.stream().findFirst().map(Object::getClass).orElseThrow();

        log.info("Starting to create Excel sheet for class {}", clazz.getSimpleName());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(SheetConfigUtils.resolveSheetName(clazz));

            if (SheetConfigUtils.includeHeader(clazz)) {
                createHeader(sheet, clazz);
            }

            for (T model : models) {
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                convertModelToRow(model, row);
            }

            log.info("Added {} rows to the sheet", models.size());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            log.info("Workbook written to byte array output stream, size {} bytes", baos.size());

            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Error while creating Excel sheet: {}", e.getMessage(), e);
            throw new SheetModelMapperException(e.getMessage());
        }
    }

    private void createHeader(Sheet sheet, Class<?> clazz) {
        Row headerRow = sheet.createRow(0);
        List<String> headers = SheetConfigUtils.getHeaders(clazz);

        log.debug("Creating header with columns: {}", headers);

        for (int col = 0; col < headers.size(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(headers.get(col));
        }
    }

    private <T> void convertModelToRow(T model, Row row) {
        List<String> order = SheetConfigUtils.getOrders(model.getClass());

        log.debug("Converting model of type {} to row with fields: {}", model.getClass().getSimpleName(), order);

        for (int col = 0; col < order.size(); col++) {
            Cell cell = row.createCell(col);
            Object value = ModelValueResolver.getFieldValue(model, order.get(col));
            CellValueWriter.write(cell, value);
        }
    }
}
