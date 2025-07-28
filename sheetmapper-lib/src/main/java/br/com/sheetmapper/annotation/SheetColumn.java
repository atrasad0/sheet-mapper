package br.com.sheetmapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SheetColumn {

    /**
     * Nome da coluna no Excel.
     * Se não for informado, será usado o nome do campo da classe.
     */
    String columnName() default "";

    /**
     * Nome do método getter que será usado para extrair o valor.
     * Se não for informado, será usado o getter padrão da propriedade (ex: getNome).
     */
    String getter() default "";
}