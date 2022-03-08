package com.tconrado.estoquecerveja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CervejaNaoEncontradaException extends Exception{

    public CervejaNaoEncontradaException(String cerveja) {
        super(String.format("A cerveja com nome %s não existe no sistema.", cerveja));
    }

    public CervejaNaoEncontradaException(Long id) {
        super(String.format("A cerveja com id %s não existe no sistema.", id));
    }
}
