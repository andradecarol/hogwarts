package br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request;

import br.org.hogwarts.domain.exception.MessageErrorCodeConstants;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HousePatchRequest {

    @NotNull(message = MessageErrorCodeConstants.FIELD_NOT_BE_NULL)
    private String leaderName;
}
