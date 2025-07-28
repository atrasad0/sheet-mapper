package br.com.sheetmapper.support;

import br.com.sheetmapper.annotation.SheetColumn;
import br.com.sheetmapper.exception.ModelValueResolverException;
import br.com.sheetmapper.support.model.ModelAccessorMetadata;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ModelValueResolver {
   private static final Logger log = LoggerFactory.getLogger(ModelValueResolver.class);

   private static final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, ModelAccessorMetadata>> cache = new ConcurrentHashMap<>();

   private ModelValueResolver() {
      throw new IllegalStateException(ModelValueResolver.class.getSimpleName() + " is a utility class and should not be instantiated.");
   }
   public static <T> Object getFieldValue(T model, String fieldName) {
      Objects.requireNonNull(model, "Target cannot be null");
      Objects.requireNonNull(fieldName, "fieldName cannot be null");

      ModelAccessorMetadata metadata = Objects.requireNonNull(getModelValueResolverMetadata(model.getClass(), fieldName));
      return invokeMethod(metadata.method(), model);
   }

   private static <T> ModelAccessorMetadata buildModelValueResolverMetadata(Class<T> clazz, String fieldName) {
      Field field = getField(clazz, fieldName);
      SheetColumn sheetColumn = field.getAnnotation(SheetColumn.class);
      String methodName = buildDefaultGetterName(field);

      if (sheetColumn != null && StringUtil.isNotBlank(sheetColumn.getter())) {
         methodName = sheetColumn.getter();
      }

      Method method = getMethod(clazz, methodName);

      return new ModelAccessorMetadata.Builder()
              .clazz(clazz)
              .field(field)
              .method(method)
              .build();
   }

   private static <T> ModelAccessorMetadata getModelValueResolverMetadata(Class<T> clazz, String fieldName) {
      return cache
           .computeIfAbsent(clazz, c -> new ConcurrentHashMap<>())
           .computeIfAbsent(fieldName, fn -> buildModelValueResolverMetadata(clazz, fn));
   }


   private static <T> Method getMethod(Class<T> clazz, String methodName) throws ModelValueResolverException {
      try {
         return clazz.getMethod(methodName);
      } catch (NoSuchMethodException e) {
         String errorMessage = String.format("Method: %s not found in class: %s -> %s", methodName, clazz.getName(), e.getMessage());
         log.error(errorMessage, e);
         throw new ModelValueResolverException(errorMessage);
      }
   }

   private static <T> Object invokeMethod(Method method, T model) throws ModelValueResolverException {
      try {
         return method.invoke(model);
      } catch (InvocationTargetException | IllegalAccessException | NullPointerException e) {
         String errorMessage = String.format("Error invoking method: %s in class: %s -> %s", method.getName(), model.getClass().getName(), e.getMessage());
         log.error(errorMessage, e);
         throw new ModelValueResolverException(errorMessage);
      }
   }

   private static <T> Field getField(Class<T> clazz, String fieldName) throws ModelValueResolverException {
      try {
         return clazz.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
         String errorMessage = String.format("Error getting field: %s in class: %s -> %s", fieldName, clazz.getName(), e.getMessage());
         log.error(errorMessage, e);
         throw new ModelValueResolverException(errorMessage);
      }

   }

   private static String capitalize(String str) {
      if (str == null || str.isEmpty()) return str;
      return str.substring(0, 1).toUpperCase() + str.substring(1);
   }
   private static String buildDefaultGetterName(Field field) {
      Objects.requireNonNull(field, "Field cannot be null");
      return "get" + capitalize(field.getName());
   }
}
