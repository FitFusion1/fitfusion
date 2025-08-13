package com.fitfusion.config;


import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        Converter<LocalDate, Date> dateConverter = new Converter<LocalDate, Date>() {
            public Date convert(MappingContext<LocalDate, Date> context) {
                LocalDate localDate = context.getSource();
                return localDate == null ? null :
                        Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }
        };

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.LOOSE)
                .setSkipNullEnabled(true);
        modelMapper.addConverter(dateConverter);

        return modelMapper;
    }
}
