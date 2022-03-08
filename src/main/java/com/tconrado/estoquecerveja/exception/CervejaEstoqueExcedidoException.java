package com.tconrado.estoquecerveja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CervejaEstoqueExcedidoException extends Exception{

    public CervejaEstoqueExcedidoException(Long id, int estoqueMaximo) {
        super(String.format("Estoque excedido para cerjeva com ID %s, estoque máximo: %s", id, estoqueMaximo));
    }
}
