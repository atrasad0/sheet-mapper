package br.com.sheetmapper.mapping;

import java.util.Collection;

public interface SheetModelMapper {
    /**
     * Gera um arquivo .xlsx a partir da coleção de modelos.
     * A ordem dos elementos será mantida se a coleção suportar ordenação.
     *
     * @param models coleção de objetos a serem convertidos em planilha
     * @param <T> tipo do modelo
     * @return arquivo .xlsx em formato binário
     */
    <T> byte[] createSheet(Collection<T> models);
}
