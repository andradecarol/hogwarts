package br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response;

import br.org.hogwarts.domain.entities.Hogwarts;
import br.org.hogwarts.utils.messages.MessageConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HouseResponse {

    private String id;

    private String name;

    private String color;

    private String leaderName;

    @JsonFormat(pattern = MessageConstants.DATE_FORMAT)
    private LocalDateTime dataCriacao;

    @JsonFormat(pattern = MessageConstants.DATE_FORMAT)
    private LocalDateTime dataAtualizacao;

    public static HouseResponse from(final Hogwarts hogwarts) {
        HouseResponse hogwartsDto = new HouseResponse();
        BeanUtils.copyProperties(hogwarts, hogwartsDto);
        return hogwartsDto;
    }
}