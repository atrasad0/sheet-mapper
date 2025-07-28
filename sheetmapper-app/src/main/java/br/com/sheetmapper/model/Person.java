package br.com.sheetmapper.model;


import br.com.sheetmapper.annotation.SheetColumn;
import br.com.sheetmapper.annotation.SheetConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@FieldNameConstants

@SheetConfig(order = {Person.Fields.birthDate, Person.Fields.firstName, Person.Fields.lastName}, sheetName = "Lista de Pessoas")
public class Person {
    @SheetColumn(columnName = "NOME")
    private String firstName;
    @SheetColumn(columnName = "SOBRENOME")
    private String lastName;
    @SheetColumn(columnName = "DATA_NASCIMENTO")
    private LocalDate birthDate;

}
