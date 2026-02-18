package com.example.BacK.presentation.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.getConfiguration().setPropertyCondition(context -> {
            Object sourceValue = context.getSource();
            // On ignore le mapping des collections
            if (sourceValue instanceof Collection) {
                return false;
            }
            return sourceValue != null;
        });

        return mapper;
    }



}


