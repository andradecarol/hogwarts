package br.org.hogwarts.application.adapters.http.inbound.controllers;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HouseCreateRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HousePatchRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response.HouseResponse;
import br.org.hogwarts.application.adapters.http.inbound.controllers.swagger.HousesControllerSwagger;
import br.org.hogwarts.config.application.ApplicationConfig;
import br.org.hogwarts.domain.services.interfaces.HousesService;
import br.org.hogwarts.utils.pagination.CustomPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class HousesController implements HousesControllerSwagger {

    private final HousesService housesService;

    private final ApplicationConfig applicationConfig;

    @Override
    public ResponseEntity<HouseResponse> post(HouseCreateRequest houseCreateRequest) {
        HouseResponse houseResponse = housesService.createHouse(houseCreateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(houseResponse);
    }

    @Override
    public ResponseEntity<List<HouseResponse>> getList(Map<String, Object> params, Integer page, Integer limit, String name, String color, String leaderName) {
        Page<HouseResponse> result = housesService.getPagesHouses(name, color, leaderName, CustomPagination.createCustomPageable(params));
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(CustomPagination.createPaginationHeaders(String.valueOf(applicationConfig.getMaxResultsPerPage()), String.valueOf(result.getNumberOfElements()), String.valueOf(result.getTotalPages())))
                .body(result.get().toList());
    }


    @Override
    public ResponseEntity<HouseResponse> get(UUID motivoRecusaId) {
        HouseResponse houseResponse = housesService.getByIdHouse(motivoRecusaId.toString());
        return ResponseEntity.status(HttpStatus.OK).body(houseResponse);
    }

    @Override
    public ResponseEntity<Void> put(UUID motivoRecusaId, HouseCreateRequest houseCreateRequest) {
        housesService.updateHouse(motivoRecusaId.toString(), houseCreateRequest);
        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<Void> patch(UUID motivoRecusaId, HousePatchRequest housePatchRequest) {
        housesService.patchHouse(motivoRecusaId.toString(), housePatchRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
