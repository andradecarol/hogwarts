package br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request;

import br.org.hogwarts.domain.entities.Hogwarts;
import br.org.hogwarts.domain.exception.MessageErrorCodeConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseCreateRequest {

    @NotBlank(message = MessageErrorCodeConstants.FIELD_NOT_BE_NULL)
    @Size(min = 1, max = 500, message = MessageErrorCodeConstants.FIELD_MUST_BE_MIN_MAX_CHARACTER)
    private String name;

    private String color;

    private String leaderName;

    public Hogwarts toHogwarts() {
        var data = LocalDateTime.now();

        return Hogwarts.builder()
                .name(this.name)
                .color(this.color)
                .leaderName(this.leaderName)
                .dataCriacao(data)
                .dataAtualizacao(data)
                .build();
    }
}
