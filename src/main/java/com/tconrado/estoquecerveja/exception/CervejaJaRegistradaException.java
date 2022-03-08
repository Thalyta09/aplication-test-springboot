package com.tconrado.estoquecerveja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CervejaJaRegistradaException extends Exception {

    public CervejaJaRegistradaException(String cerveja) {
        super(String.format("A cerveja com nome %s já está resgistrada no sistema.", cerveja));
    }
}