package com.styleshop.order;
import org.springframework.boot.SpringApplication; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.context.annotation.Bean; import org.springframework.web.client.RestClient;
@SpringBootApplication public class OrderServiceApplication {public static void main(String[]a){SpringApplication.run(OrderServiceApplication.class,a);}@Bean RestClient restClient(RestClient.Builder b){return b.baseUrl("http://localhost:8082").build();}}
