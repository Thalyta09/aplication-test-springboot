package com.tconrado.estoquecerveja.service;

import com.tconrado.estoquecerveja.dto.CervejaDTO;
import com.tconrado.estoquecerveja.entity.Cerveja;
import com.tconrado.estoquecerveja.exception.CervejaEstoqueExcedidoException;
import com.tconrado.estoquecerveja.exception.CervejaJaRegistradaException;
import com.tconrado.estoquecerveja.exception.CervejaNaoEncontradaException;
import com.tconrado.estoquecerveja.mapper.CervejaMapper;
import com.tconrado.estoquecerveja.repository.CervejaRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CervejaService {

    private final CervejaRepository cervejaRepository;
    private final CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    public CervejaDTO createCerveja(CervejaDTO cervejaDTO) throws CervejaJaRegistradaException {
        verificaSeExisteRegistro(cervejaDTO.getNome());
        Cerveja cerveja = cervejaMapper.toModel(cervejaDTO);
        Cerveja salvaCerveja = cervejaRepository.save(cerveja);
        return cervejaMapper.toDTO(salvaCerveja);
    }

    public CervejaDTO findByName(String nome) throws CervejaNaoEncontradaException {
        Cerveja encontreCerveja = cervejaRepository.findByNome(nome)
                .orElseThrow(() -> new CervejaNaoEncontradaException(nome));
        return cervejaMapper.toDTO(encontreCerveja);
    }

    public List<CervejaDTO> listAll() {
        return cervejaRepository.findAll()
                .stream()
                .map(cervejaMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws CervejaNaoEncontradaException {
        verificaSeExiste(id);
        cervejaRepository.deleteById(id);
    }

    private void verificaSeExisteRegistro(String nome) throws CervejaJaRegistradaException {
        Optional<Cerveja> optCerveja = cervejaRepository.findByNome(nome);
        if(optCerveja.isPresent()) {
            throw new CervejaJaRegistradaException(nome);
        }
    }

    private Cerveja verificaSeExiste(Long id) throws CervejaNaoEncontradaException {
        return cervejaRepository.findById(id)
                .orElseThrow(() -> new CervejaNaoEncontradaException(id));
    }

    public CervejaDTO increment(Long id, int quantidadeAdc) throws CervejaEstoqueExcedidoException, CervejaNaoEncontradaException {
        Cerveja cervejaAdcEstoque = verificaSeExiste(id);
        int quantidadeApos = quantidadeAdc + cervejaAdcEstoque.getQuantidade();
        if (quantidadeApos <= cervejaAdcEstoque.getMax()) {
            cervejaAdcEstoque.setQuantidade(cervejaAdcEstoque.getQuantidade() + quantidadeAdc);
            Cerveja cervejaEstoqueAtl = cervejaRepository.save(cervejaAdcEstoque);
            return cervejaMapper.toDTO(cervejaEstoqueAtl);
        }
        throw new CervejaEstoqueExcedidoException(id, cervejaAdcEstoque.getMax());
    }
}