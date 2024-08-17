package com.example.glowtales.converter;


import com.example.glowtales.domain.YesOrNo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YesOrNoConverter implements AttributeConverter<YesOrNo, Integer> {

    @Override
    public Integer convertToDatabaseColumn(YesOrNo attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public YesOrNo convertToEntityAttribute(Integer dbData) {
        return dbData != null ? YesOrNo.fromValue(dbData) : null;
    }
}
