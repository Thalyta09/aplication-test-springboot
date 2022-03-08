package com.tconrado.estoquecerveja.service;

import com.tconrado.estoquecerveja.builder.CervejaDTOBuilder;
import com.tconrado.estoquecerveja.dto.CervejaDTO;
import com.tconrado.estoquecerveja.entity.Cerveja;
import com.tconrado.estoquecerveja.exception.CervejaEstoqueExcedidoException;
import com.tconrado.estoquecerveja.exception.CervejaJaRegistradaException;
import com.tconrado.estoquecerveja.exception.CervejaNaoEncontradaException;
import com.tconrado.estoquecerveja.mapper.CervejaMapper;
import com.tconrado.estoquecerveja.repository.CervejaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CervejaServiceTest {

    private static final long CERVEJA_ID_INVALIDO = 1L;

    @Mock
    private CervejaRepository cervejaRepository;

    private CervejaMapper cervejaMapper = CervejaMapper.INSTANCE;

    @InjectMocks
    private CervejaService cervejaService;

    @Test
    void quandoCervejaInformadaForCriada() throws CervejaJaRegistradaException {

        CervejaDTO cervejaExperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja salvarCerveja = cervejaMapper.toModel(cervejaExperadaDTO);

        when(cervejaRepository.findByNome(cervejaExperadaDTO.getNome())).thenReturn(Optional.empty());
        when(cervejaRepository.save(salvarCerveja)).thenReturn(salvarCerveja);

        CervejaDTO criaCervejaDTO = cervejaService.createCerveja(cervejaExperadaDTO);

        assertThat(criaCervejaDTO.getId(), is(equalTo(cervejaExperadaDTO.getId())));
        assertThat(criaCervejaDTO.getNome(), is(equalTo(cervejaExperadaDTO.getNome())));
        assertThat(criaCervejaDTO.getQuantidade(), is(equalTo(cervejaExperadaDTO.getQuantidade())));
    }

    @Test
    void lancaExcecaoQuandoCervejaInformadaJaCriada() {

        CervejaDTO cervejaExperadaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaDuplicada = cervejaMapper.toModel(cervejaExperadaDTO);

        when(cervejaRepository.findByNome(cervejaExperadaDTO.getNome())).thenReturn(Optional.of(cervejaDuplicada));

        assertThrows(CervejaJaRegistradaException.class, () -> cervejaService.createCerveja(cervejaExperadaDTO));
    }

    @Test
    void quandoNomeCervejaValidoInformado() throws CervejaNaoEncontradaException {

        CervejaDTO cervejaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEncontrada = cervejaMapper.toModel(cervejaEncontradaDTO);

        when(cervejaRepository.findByNome(cervejaEncontrada.getNome())).thenReturn(Optional.of(cervejaEncontrada));

        CervejaDTO cervejaExperadaDTO = cervejaService.findByName(cervejaEncontradaDTO.getNome());

        assertThat(cervejaExperadaDTO, is(equalTo(cervejaExperadaDTO)));
    }

    @Test
    void lancaExcecaoQuandoNomeCervejaInformadoInvalido() {

        CervejaDTO cervejaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaRepository.findByNome(cervejaEncontradaDTO.getNome())).thenReturn(Optional.empty());

        assertThrows(CervejaNaoEncontradaException.class, () -> cervejaService.findByName(cervejaEncontradaDTO.getNome()));
    }

    @Test
    void quandoListaCervejaForChamadaRetornaPopulada() {
        CervejaDTO cervejaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEncontrada = cervejaMapper.toModel(cervejaEncontradaDTO);

        when(cervejaRepository.findAll()).thenReturn(Collections.singletonList(cervejaEncontrada));

        List<CervejaDTO> listaCervejaDTO = cervejaService.listAll();

        assertThat(listaCervejaDTO, is(not(empty())));
        assertThat(listaCervejaDTO.get(0), is(equalTo(cervejaEncontradaDTO)));
    }

    @Test
    void quandoListaCervejaForChamadaRetornaVazia() {
        when(cervejaRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<CervejaDTO> listaCervejaDTO = cervejaService.listAll();

        assertThat(listaCervejaDTO, is(empty()));
    }

    @Test
    void quandoDeletarCervejaForChamadoComIdValido() throws CervejaNaoEncontradaException {
        CervejaDTO cervejaParaDeletarDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaParaDeletar = cervejaMapper.toModel(cervejaParaDeletarDTO);

        when(cervejaRepository.findById(cervejaParaDeletarDTO.getId())).thenReturn(Optional.of(cervejaParaDeletar));
        doNothing().when(cervejaRepository).deleteById(cervejaParaDeletarDTO.getId());

        cervejaService.deleteById(cervejaParaDeletarDTO.getId());

        verify(cervejaRepository, times(1)).findById(cervejaParaDeletarDTO.getId());
        verify(cervejaRepository, times(1)).deleteById(cervejaParaDeletarDTO.getId());
    }

    @Test
    void quandoEncrementoForChamadoAumentaEstoqueCerveja() throws CervejaNaoEncontradaException, CervejaEstoqueExcedidoException {
        CervejaDTO cervejaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEncontrada = cervejaMapper.toModel(cervejaEncontradaDTO);

        when(cervejaRepository.findById(cervejaEncontradaDTO.getId())).thenReturn(Optional.of(cervejaEncontrada));
        when(cervejaRepository.save(cervejaEncontrada)).thenReturn(cervejaEncontrada);

        int quantidadeEncremento = 10;
        int quantidadeDepoisEncremento = cervejaEncontradaDTO.getQuantidade() + quantidadeEncremento;

        CervejaDTO cervejaEncrementadaDTO = cervejaService.increment(cervejaEncontradaDTO.getId(), quantidadeEncremento);

        assertThat(quantidadeDepoisEncremento, equalTo(cervejaEncrementadaDTO.getQuantidade()));
        assertThat(quantidadeDepoisEncremento, lessThan(cervejaEncontradaDTO.getMax()));
    }

    @Test
    void quandoEncrementoMaiorMaximoPermitido() {
        CervejaDTO cervejaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEncontrada = cervejaMapper.toModel(cervejaEncontradaDTO);

        when(cervejaRepository.findById(cervejaEncontradaDTO.getId())).thenReturn(Optional.of(cervejaEncontrada));

        int quantidadeEncremento = 80;
        assertThrows(CervejaEstoqueExcedidoException.class, () -> cervejaService.increment(cervejaEncontradaDTO.getId(), quantidadeEncremento));
    }

    @Test
    void quandoEncrementoAposSomaMaiorMaximoPermitido() {
        CervejaDTO cervejaEncontradaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        Cerveja cervejaEncontrada = cervejaMapper.toModel(cervejaEncontradaDTO);

        when(cervejaRepository.findById(cervejaEncontradaDTO.getId())).thenReturn(Optional.of(cervejaEncontrada));

        int quantidadeEncremento = 45;
        assertThrows(CervejaEstoqueExcedidoException.class, () -> cervejaService.increment(cervejaEncontradaDTO.getId(), quantidadeEncremento));
    }

    @Test
    void quandoEncrementoForChamadoComIdInvalido() {
        int quantidadeEncremento = 10;

        when(cervejaRepository.findById(CERVEJA_ID_INVALIDO)).thenReturn(Optional.empty());

        assertThrows(CervejaNaoEncontradaException.class, () -> cervejaService.increment(CERVEJA_ID_INVALIDO, quantidadeEncremento));
    }
}
