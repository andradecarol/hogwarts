package br.org.hogwarts.mock;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HouseCreateRequest;
import br.org.hogwarts.domain.entities.Hogwarts;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

public class HousesMock {

    private HousesMock() {
        throw new IllegalStateException("Utility class");
    }

    public static HouseCreateRequest validHouseCreateRequest() {
        return HouseCreateRequest.builder()
                .name("Gryffindor")
                .color("Red")
                .leaderName("Minerva")
                .build();
    }

    public static Hogwarts validHouse(LocalDateTime data) {
        return Hogwarts.builder()
                .id("635877b5377fd21cda7975c3")
                .name("Gryffindor")
                .color("Red")
                .leaderName("Minerva")
                .dataCriacao(data)
                .dataAtualizacao(data)
                .build();
    }

    public static Pageable validPageable() {
        return PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "name"));
    }

    public static Pageable invalidPageable() {
        return PageRequest.of(0, 101, Sort.by(Sort.Direction.ASC, "name"));
    }
}
