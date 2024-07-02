package br.org.hogwarts.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "hogwarts")
public class Hogwarts {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String name;

    private String color;

    private String leaderName;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;
}
