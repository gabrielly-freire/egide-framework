package br.imd.ufrn.core.mapper;

import br.imd.ufrn.core.domain.AuditEntry;
import br.imd.ufrn.core.dto.AuditEntryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditEntryMapper {

    AuditEntryResponse toResponse(AuditEntry entity);
}
