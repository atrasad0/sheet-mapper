package br.com.sheetmapper;

import br.com.sheetmapper.mapping.SheetModelMapper;
import br.com.sheetmapper.mapping.impl.SheetModelMapperImpl;

import java.util.Collection;

public class SheetMapper {
    private final SheetModelMapper sheetModelMapper;

    public SheetMapper(SheetModelMapper sheetModelMapper) {
        this.sheetModelMapper = sheetModelMapper;
    }

    public SheetMapper() {
        this.sheetModelMapper = new SheetModelMapperImpl();
    }

    public <T> byte[] createSheet(Collection<T> models) {
        return this.sheetModelMapper.createSheet(models);
    }
}
