package com.springboot.eduko.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "EDUKO",
                description = "Website Education",
                contact = @Contact(
                        name = "CEO",
                        email = " ahmednsra329@gmail.com"
                ),
                license = @License(
                        name = "All CopyRight Preseved"
                ),
                version = "V1"
        )
)
public class ShowSwagger {
}
