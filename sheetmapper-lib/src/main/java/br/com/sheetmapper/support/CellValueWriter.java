package br.com.sheetmapper.support;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CellValueWriter {

    private CellValueWriter() {
        throw new IllegalStateException(CellValueWriter.class.getSimpleName() + " is a utility class and should not be instantiated.");
    }

    private static final Logger log = LoggerFactory.getLogger(CellValueWriter.class);
    private static final Map<Class<?>, BiConsumer<Cell, Object>> cellSetters = new HashMap<>();

    static {
        cellSetters.put(String.class, (cell, value) -> cell.setCellValue((String) value));
        cellSetters.put(Integer.class, (cell, value) -> cell.setCellValue(((Integer) value).doubleValue()));
        cellSetters.put(Long.class, (cell, value) -> cell.setCellValue(((Long) value).doubleValue()));
        cellSetters.put(Double.class, (cell, value) -> cell.setCellValue((Double) value));
        cellSetters.put(Boolean.class, (cell, value) -> cell.setCellValue((Boolean) value));
        cellSetters.put(Date.class, (cell, value) -> cell.setCellValue((Date) value));
        cellSetters.put(LocalDate.class, (cell, value) -> cell.setCellValue((LocalDate) value));
        cellSetters.put(LocalDateTime.class, (cell, value) -> cell.setCellValue((LocalDateTime) value));
    }

    public static void write(Cell cell, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }

        BiConsumer<Cell, Object> writer = cellSetters.get(value.getClass());

        if (writer != null) {
            writer.accept(cell, value);
            return;
        }

        log.warn("Unexpected type in CellValueWriter: {}", value.getClass());
        cell.setCellValue(value.toString());

    }
}
