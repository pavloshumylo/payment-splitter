package com.eleks.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ListToStringWithDelimetersConverter implements AttributeConverter<List<Long>, String> {

    @Override
    public String convertToDatabaseColumn(List<Long> coPayers) {
        return Objects.isNull(coPayers) || coPayers.isEmpty() ? null : coPayers.stream()
                .distinct()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public List<Long> convertToEntityAttribute(String coPayersWithDelimeters) {
        return Objects.isNull(coPayersWithDelimeters) || coPayersWithDelimeters.trim().isEmpty() ?
                new ArrayList<>() : Stream.of(coPayersWithDelimeters.split(","))
                .map(String::trim)
                .distinct()
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
