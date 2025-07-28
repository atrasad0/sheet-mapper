package br.com.sheetmapper.support;

import br.com.sheetmapper.annotation.SheetColumn;
import br.com.sheetmapper.annotation.SheetConfig;
import br.com.sheetmapper.support.model.SheetConfigMetadata;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SheetConfigUtils {
    private static final Logger log = LoggerFactory.getLogger(SheetConfigUtils.class);
    private static final ConcurrentHashMap<Class<?>, SheetConfigMetadata> cache = new ConcurrentHashMap<>();

    private SheetConfigUtils() {
        throw new IllegalStateException(SheetConfigUtils.class.getSimpleName() + " is a utility class and should not be instantiated.");
    }

    public static String resolveSheetName(Class<?> clazz) {
        requireNonNullClass(clazz);
        return getSheetConfigMetadata(clazz).sheetName();
    }

    public static boolean includeHeader(Class<?> clazz) {
        requireNonNullClass(clazz);
        return getSheetConfigMetadata(clazz).includeHeader();
    }

    public static List<String> getHeaders(Class<?> clazz) {
        requireNonNullClass(clazz);
        return getSheetConfigMetadata(clazz).headers();
    }

    public static List<String> getOrders(Class<?> clazz) {
        requireNonNullClass(clazz);
        return getSheetConfigMetadata(clazz).order();
    }

    private static SheetConfigMetadata getSheetConfigMetadata(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, SheetConfigUtils::buildSheetConfigMetadata);
    }

    private static SheetConfigMetadata buildSheetConfigMetadata(Class<?> clazz) {
        SheetConfig configAnnotation = clazz.getAnnotation(SheetConfig.class);

        if (configAnnotation == null) {
            List<String> order =  getDeclaredFields(clazz);
            return SheetConfigMetadata.builder()
                .order(order)
                .headers(getHeadersFromConfigOrder(clazz, order))
                .sheetName(clazz.getSimpleName())
                .build();
        }

        String configName = Objects.requireNonNullElse(configAnnotation.sheetName(), "");
        String sheetName = configName.isBlank() ? clazz.getSimpleName() : configName;

        String[] rawOrder = configAnnotation.order();
        boolean isEmptyOrder = rawOrder.length == 0 || Arrays.stream(rawOrder).allMatch(StringUtil::isBlank);

        List<String> orders = isEmptyOrder ? getDeclaredFields(clazz) : Arrays.asList(rawOrder);

        return SheetConfigMetadata.builder()
            .order(orders)
            .headers(getHeadersFromConfigOrder(clazz, orders))
            .sheetName(sheetName)
            .includeHeader(configAnnotation.includeHeader())
            .strictOrder(configAnnotation.strictOrder())
            .build();
    }

    private static List<String> getDeclaredFields(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .toList();
    }
    private static List<String> getHeadersFromConfigOrder(Class<?> clazz, List<String> orders) {
        List<String> headers = new ArrayList<>();

        for (String fieldName : orders) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                SheetColumn sheetColumn = field.getAnnotation(SheetColumn.class);

                if (sheetColumn == null || StringUtil.isBlank(sheetColumn.columnName())) {
                    headers.add(field.getName());
                } else {
                    headers.add(sheetColumn.columnName());
                }
            } catch (NoSuchFieldException ex) {
                log.error("Invalid field '{}' in @SheetConfig order for class {}", fieldName, clazz.getSimpleName(), ex);
                throw new IllegalArgumentException("Field not found in class '" + clazz.getSimpleName() + "': " + fieldName, ex);
            }
        }
        return headers;
    }

    private static void requireNonNullClass(Class<?> clazz) {
        if (clazz == null) {
            log.error("Class type (clazz) must not be null.");
            throw new IllegalArgumentException("Class type (clazz) must not be null.");
        }
    }
}
