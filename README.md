# SheetMapper

Biblioteca Java para conversão de objetos (`models`) em planilhas (`.xlsx`) de forma customizável utilizando **Apache POI**.

---

## 📦 Tecnologias

- Java 17
- Maven
- Apache POI

---

## ✨ Funcionalidades

- Exportação automática de objetos Java para planilhas
- Configuração personalizada de colunas com anotações
- Controle de ordem de colunas
- Inclusão opcional de cabeçalho
- Suporte a métodos getters customizados

---

## 🔖 Anotações

### `@SheetConfig`

Define configurações da planilha a ser gerada.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SheetConfig {
    String[] order() default {};
    String sheetName() default "";
    boolean includeHeader() default true;
}
```

- `order`: Define a ordem das propriedades que serão exportadas. Se não informado, todas as propriedades da classe serão exportadas na ordem natural.
- `sheetName`: Nome da planilha no Excel. Se não informado, será usado o nome da classe.
- `includeHeader`: Define se o cabeçalho (nomes das colunas) será incluído.

---

### `@SheetColumn`

Define informações específicas de cada coluna.

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SheetColumn {
    String columnName() default "";
    String getter() default "";
}
```

- `columnName`: Nome da coluna no Excel. Se não informado, será usado o nome do campo.
- `getter`: Permite definir um getter customizado para extração do valor (caso não deseje usar o getter padrão).

### 🔍 Como os valores são obtidos

Para que os valores dos campos sejam extraídos e exportados para o Excel, **é obrigatório que a classe possua métodos públicos `get` compatíveis com os campos exportados**.

- Se **nenhum getter for informado** via `@SheetColumn(getter = "getAlgumaCoisa")`, a biblioteca tentará utilizar o **getter padrão**, seguindo o nome do campo (por exemplo, `getNome()` para o campo `nome`).
- Se o campo **não tiver um getter público correspondente**, a conversão irá **lançar um erro** em tempo de execução, indicando que o método getter público da classe especificada não foi encontrado.
---

## 🧑‍💻 Exemplo de uso)

### Modelo (Java Bean com lombok)

```java
import br.com.sheetmapper.annotation.SheetColumn;
import br.com.sheetmapper.annotation.SheetConfig;

@Getter
@Setter
@AllArgsConstructor
@FieldNameConstants
@SheetConfig(
    order = {Person.Fields.birthDate, Person.Fields.firstName, Person.Fields.lastName},
    sheetName = "Lista de Pessoas"
)
public class Person {

    @SheetColumn(columnName = "NOME")
    private String firstName;

    @SheetColumn(columnName = "SOBRENOME")
    private String lastName;

    @SheetColumn(columnName = "DATA_NASCIMENTO")
    private LocalDate birthDate;
}
```

**Importante:**  
Caso você defina a propriedade `order` no `@SheetConfig`, apenas os campos listados em `order` serão exportados.  
Se não definir `order`, todos os campos da classe serão exportados, na ordem natural.

Você também pode omitir as anotações `@SheetColumn` e `@SheetConfig` , pois elas não são obrigatórias para a conversão funcionar. 
Nesse caso, a biblioteca irá gerar a planilha com base na estrutura padrão da classe Java, 
utilizando o nome dos campos como títulos das colunas e a ordem natural de declaração, sem nenhuma personalização.

```java
@Getter
@Setter
@AllArgsConstructor
@FieldNameConstants
public class Person {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
}
```

---

### Execução

```java
import br.com.sheetmapper.SheetMapper;
public class Example {
    public void createSheet() {
        SheetMapper mapper = new SheetMapper();

        var p1 = new Person("Javalison", "Oracle", LocalDate.of(1996, Month.JANUARY, 23));
        var p2 = new Person("Ccharpson", ".NET", LocalDate.of(2002, Month.JANUARY, 14));

        byte[] sheetFile = mapper.createSheet(List.of(p1, p2));

        try (FileOutputStream fos = new FileOutputStream("sheet.xlsx")) {
            fos.write(sheetFile);
            System.out.println("Arquivo Excel gerado com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

---

## ✅ Requisitos

- Java 17 ou superior
- Maven

---