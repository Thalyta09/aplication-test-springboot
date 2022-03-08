package com.tconrado.estoquecerveja.controller;

import com.tconrado.estoquecerveja.dto.CervejaDTO;
import com.tconrado.estoquecerveja.exception.CervejaJaRegistradaException;
import com.tconrado.estoquecerveja.exception.CervejaNaoEncontradaException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Controle de Estoque de Cerveja")
public interface CervejaControllerDocs {

    @ApiOperation(value = "Operação criar uma cerveja")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Sucesso ao criar o registro da cerveja"),
            @ApiResponse(code = 400, message = "Falta preencher campos obrigatórios ou dado informado incorretamente.")
    })
    CervejaDTO createCerveja(CervejaDTO cervejaDTO) throws CervejaJaRegistradaException;

    @ApiOperation(value = "Retorna o registro da cerveja pesquisado por nome")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sucesso ao buscar cerveja pelo nome"),
            @ApiResponse(code = 404, message = "Cerveja com nome informado não encontrada.")
    })
    CervejaDTO findByName(@PathVariable String nome) throws CervejaNaoEncontradaException;

    @ApiOperation(value = "Retorna uma lista com todas as cervejas cadastradas")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista com todas as cervejas registradas no sistema"),
    })
    List<CervejaDTO> listBeers();

    @ApiOperation(value = "Deleta uma cerveja com um ID válido")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Sucesso ao deletar a cerveja do sistema"),
            @ApiResponse(code = 404, message = "Cerveja com ID informado não encontrada.")
    })
    void deleteById(@PathVariable Long id) throws CervejaNaoEncontradaException;
}
