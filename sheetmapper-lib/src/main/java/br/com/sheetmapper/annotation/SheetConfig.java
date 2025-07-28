package br.com.sheetmapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SheetConfig {
    /**
     * Ordem das propriedades da classe que serão exportadas/importadas.
     * Se não informado, será usada a ordem natural dos campos
     */
    String[] order() default {};

    /**
     * Nome da planilha (sheet) no Excel.
     * Se não informado, será o nome da classe.
     */
    String sheetName() default "";

    /**
     * Indica se o cabeçalho com os nomes das colunas deve ser escrito no Excel.
     */
    boolean includeHeader() default true;

    /**
     * Define se os campos não mencionados na ordem devem ser ignorados.
     * Se true, apenas os campos listados em `order` serão usados.
     */
    boolean strictOrder() default false;

}
