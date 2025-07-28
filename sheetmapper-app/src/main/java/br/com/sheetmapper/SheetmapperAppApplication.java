package br.com.sheetmapper;

import br.com.sheetmapper.model.Person;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@SpringBootApplication
public class SheetmapperAppApplication  implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SheetmapperAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
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
