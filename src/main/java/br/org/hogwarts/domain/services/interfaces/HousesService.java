package br.org.hogwarts.domain.services.interfaces;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HouseCreateRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HousePatchRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response.HouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface HousesService {
    HouseResponse createHouse(HouseCreateRequest houseCreateRequest);

    Page<HouseResponse> getPagesHouses(String name, String color, String leaderName, Pageable pageable);

    HouseResponse getByIdHouse(String id);

    void updateHouse(String id, HouseCreateRequest houseCreateRequest);

    void patchHouse(String id, HousePatchRequest housePatchRequest);

}
