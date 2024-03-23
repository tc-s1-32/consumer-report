package br.com.fiap.soat1.t32.consumerreport;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "br.com.fiap.soat1.t32")
@EnableRabbit
@EnableFeignClients
public class ConsumerReportApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerReportApplication.class, args);
	}

}
