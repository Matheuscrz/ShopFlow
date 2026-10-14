package com.matheuscrz.identity.application.port.out;

import com.matheuscrz.identity.domain.model.Cpf;

public interface CpfSecurityPort {

    String encrypt(Cpf cpf);

    String lookupHash(Cpf cpf);

    Cpf decrypt(String encryptedCpf);
}