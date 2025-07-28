package br.com.sheetmapper.support.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public record ModelAccessorMetadata(Class<?> clazz, Field field, Method method) {
    public static class Builder {
        private Class<?> clazz;
        private Field field;
        private Method method;

        public Builder clazz(Class<?> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder field(Field field) {
            this.field = field;
            return this;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public ModelAccessorMetadata build() {
            return new ModelAccessorMetadata(clazz, field, method);
        }
    }
}
