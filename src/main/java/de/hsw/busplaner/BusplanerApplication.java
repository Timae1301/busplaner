package de.hsw.busplaner;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BusplanerApplication {

	@Value("${format.date}")
	private String dateFormat;
	@Value("${format.datetime}")
	private String dateTimeFormat;

	public static void main(String[] args) {
		SpringApplication.run(BusplanerApplication.class, args);
	}

	/**
	 * Setzen der Date Formate für das konvertieren aus dem JSON für alle
	 * Zeitobjekte (inklusive Java 8 Date-Objekten)
	 * https://www.baeldung.com/spring-boot-formatting-json-dates "So, if we want to
	 * use Java 8 date types and set a default date format, then we need to look at
	 * creating a Jackson2ObjectMapperBuilderCustomizer bean"
	 * 
	 * @return
	 */
	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> {
			builder.simpleDateFormat(dateTimeFormat);
			builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
		};
	}

}
