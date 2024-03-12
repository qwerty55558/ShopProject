package com.toyproject.shop;

import com.toyproject.shop.domain.Member;
import com.toyproject.shop.repository.MemoryMemberRepository;
import org.jboss.logging.Messages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class ShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}

	@Bean
	public MessageSource messageSource(){
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("properties/messages", "properties/errors");
		messageSource.setDefaultEncoding("utf-8");
		return messageSource;
	}

}
