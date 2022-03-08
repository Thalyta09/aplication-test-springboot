package com.tconrado.estoquecerveja.controller;

import com.tconrado.estoquecerveja.builder.CervejaDTOBuilder;
import com.tconrado.estoquecerveja.dto.CervejaDTO;
import com.tconrado.estoquecerveja.dto.QuantidadeDTO;
import com.tconrado.estoquecerveja.exception.CervejaEstoqueExcedidoException;
import com.tconrado.estoquecerveja.exception.CervejaNaoEncontradaException;
import com.tconrado.estoquecerveja.service.CervejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.tconrado.estoquecerveja.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CervejaControllerTest {

    private static final String URL_CERVEJA_API = "/api/v1/cervejas";
    private static final long CERVEJA_ID_VALIDO = 1L;
    private static final long CERVEJA_ID_INVALIDO = 2l;
    private static final String INCREMENTO_URL = "/estoque";

    private MockMvc mockMvc;

    @Mock
    private CervejaService cervejaService;

    @InjectMocks
    private CervejaController cervejaController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cervejaController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void quandoPostForChamadoParaCriarCervejaRetornaOk() throws Exception {
        CervejaDTO cervejaDTO= CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.createCerveja(cervejaDTO)).thenReturn(cervejaDTO);

        mockMvc.perform(post(URL_CERVEJA_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cervejaDTO)))
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoPostForChamadoParaCriarCervejaRetornaErro() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setMarca(null);

        mockMvc.perform(post(URL_CERVEJA_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(cervejaDTO)))
                        .andExpect(status().isBadRequest());
    }

    @Test
    void quandoGetForChamadoComNomeValidoRetornaOk() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.findByName(cervejaDTO.getNome())).thenReturn(cervejaDTO);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_CERVEJA_API + "/" + cervejaDTO.getNome())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoGetForChamadoComNomeValidoRetornaErro() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.findByName(cervejaDTO.getNome())).thenThrow(CervejaNaoEncontradaException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(URL_CERVEJA_API + "/" + cervejaDTO.getNome())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoGetListaComCervejasRetornaOk() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.listAll()).thenReturn(Collections.singletonList(cervejaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(URL_CERVEJA_API)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$[0].marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$[0].tipo", is(cervejaDTO.getTipo().toString())));
    }

    @Test
    void quandoGetListaSemCervejasRetornaOk() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        when(cervejaService.listAll()).thenReturn(Collections.singletonList(cervejaDTO));

        mockMvc.perform(MockMvcRequestBuilders.get(URL_CERVEJA_API)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void quandoDeleteForChamadoComIdValidoRetornaSemStatus() throws Exception {
        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();

        doNothing().when(cervejaService).deleteById(cervejaDTO.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_CERVEJA_API + "/" + cervejaDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void quandoDeleteForChamadoComIdInvalidoRetornaErro() throws Exception {
        doThrow(CervejaNaoEncontradaException.class).when(cervejaService).deleteById(CERVEJA_ID_INVALIDO);

        mockMvc.perform(MockMvcRequestBuilders.delete(URL_CERVEJA_API + "/" + CERVEJA_ID_INVALIDO)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void quandoEncrementoForChamadoRetornaOk() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(10)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.increment(CERVEJA_ID_VALIDO, quantidadeDTO.getQuantidade())).thenReturn(cervejaDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(URL_CERVEJA_API + "/" + CERVEJA_ID_VALIDO + INCREMENTO_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is(cervejaDTO.getNome())))
                .andExpect(jsonPath("$.marca", is(cervejaDTO.getMarca())))
                .andExpect(jsonPath("$.tipo", is(cervejaDTO.getTipo().toString())))
                .andExpect(jsonPath("$.quantidade", is(cervejaDTO.getQuantidade())));
    }

    @Test
    void quandoEncrementoForChamadoRetornaErro() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(30)
                .build();

        CervejaDTO cervejaDTO = CervejaDTOBuilder.builder().build().toCervejaDTO();
        cervejaDTO.setQuantidade(cervejaDTO.getQuantidade() + quantidadeDTO.getQuantidade());

        when(cervejaService.increment(CERVEJA_ID_VALIDO, quantidadeDTO.getQuantidade())).thenThrow(CervejaEstoqueExcedidoException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(URL_CERVEJA_API + "/" + CERVEJA_ID_VALIDO + INCREMENTO_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantidadeDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void quandoEncrementoForChamadoComCervejaIdInvalidoRetornaNaoEncontrado() throws Exception {
        QuantidadeDTO quantidadeDTO = QuantidadeDTO.builder()
                .quantidade(30)
                .build();

        when(cervejaService.increment(CERVEJA_ID_INVALIDO, quantidadeDTO.getQuantidade())).thenThrow(CervejaNaoEncontradaException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(URL_CERVEJA_API + "/" + CERVEJA_ID_INVALIDO + INCREMENTO_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantidadeDTO))).andExpect(status().isNotFound());
    }
}