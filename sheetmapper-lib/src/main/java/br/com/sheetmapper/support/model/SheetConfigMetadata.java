package br.com.sheetmapper.support.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public record SheetConfigMetadata(List<String> order, List<String> headers, String sheetName, boolean includeHeader, boolean strictOrder) {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<String> order = new ArrayList<>();
        private List<String> headers = new ArrayList<>();
        private String sheetName = "";
        private boolean includeHeader = true;
        private boolean strictOrder = false;

        public Builder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Builder includeHeader(boolean includeHeader) {
            this.includeHeader = includeHeader;
            return this;
        }

        public Builder strictOrder(boolean strictOrder) {
            this.strictOrder = strictOrder;
            return this;
        }

        public Builder order(String... order) {
            this.order = Arrays.asList(order);
            return this;
        }

        public Builder order(List<String> order) {
            this.order = order;
            return this;
        }

        public Builder headers(List<String> headers) {
            this.headers = headers;
            return this;
        }

        public SheetConfigMetadata build() {
            return new SheetConfigMetadata(order, headers, sheetName, includeHeader, strictOrder);
        }
    }
}
