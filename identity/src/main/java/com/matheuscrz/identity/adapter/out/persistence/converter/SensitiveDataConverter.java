package com.matheuscrz.identity.adapter.out.persistence.converter;

import com.matheuscrz.identity.application.port.out.SensitiveDataSecurityPort;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Converter
@Component
public class SensitiveDataConverter implements AttributeConverter<String, String> {

    private static SensitiveDataSecurityPort securityPort;

    @Autowired
    public void setSecurityPort(SensitiveDataSecurityPort port) {
        SensitiveDataConverter.securityPort = port;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        if (securityPort == null) {
            throw new IllegalStateException(
                    "SensitiveDataSecurityPort não foi inicializado");
        }

        return securityPort.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        if (securityPort == null) {
            throw new IllegalStateException(
                    "SensitiveDataSecurityPort não foi inicializado");
        }

        return securityPort.decrypt(dbData);
    }
}