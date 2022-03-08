package com.tconrado.estoquecerveja.mapper;

import com.tconrado.estoquecerveja.dto.CervejaDTO;
import com.tconrado.estoquecerveja.entity.Cerveja;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CervejaMapper {

    CervejaMapper INSTANCE = Mappers.getMapper(CervejaMapper.class);

    Cerveja toModel(CervejaDTO cervejaDTO);

    CervejaDTO toDTO(Cerveja cerveja);
}
