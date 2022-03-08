package com.tconrado.estoquecerveja.controller;

import com.tconrado.estoquecerveja.dto.CervejaDTO;
import com.tconrado.estoquecerveja.dto.QuantidadeDTO;
import com.tconrado.estoquecerveja.exception.CervejaEstoqueExcedidoException;
import com.tconrado.estoquecerveja.exception.CervejaJaRegistradaException;
import com.tconrado.estoquecerveja.exception.CervejaNaoEncontradaException;
import com.tconrado.estoquecerveja.service.CervejaService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cervejas")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaController {

    private final CervejaService cervejaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CervejaDTO createBeer(@RequestBody @Valid CervejaDTO cervejaDTO) throws CervejaJaRegistradaException {
        return cervejaService.createCerveja(cervejaDTO);
    }

    @GetMapping("/{nome}")
    public CervejaDTO findByName(@PathVariable String nome) throws CervejaNaoEncontradaException {
        return cervejaService.findByName(nome);
    }

    @GetMapping
    public List<CervejaDTO> listBeers() {
        return cervejaService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws CervejaNaoEncontradaException {
        cervejaService.deleteById(id);
    }

    @PatchMapping("/{id}/estoque")
    public CervejaDTO increment(@PathVariable Long id, @RequestBody @Valid QuantidadeDTO quantidadeDTO) throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        return cervejaService.increment(id, quantidadeDTO.getQuantidade());
    }

}
