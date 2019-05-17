package com.sso.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Ryan on 2019/5/15/0015
 */
@SpringBootApplication
public class CartApp {

    public static void main(String[] args) {
        SpringApplication.run(CartApp.class,args);
    }


    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
