package br.org.hogwarts.domain.services;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HouseCreateRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HousePatchRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response.HouseResponse;
import br.org.hogwarts.config.application.ApplicationConfig;
import br.org.hogwarts.config.application.MessageConfig;
import br.org.hogwarts.domain.entities.Hogwarts;
import br.org.hogwarts.domain.exception.InternalServerErrorException;
import br.org.hogwarts.domain.exception.MaxResultsPerPageException;
import br.org.hogwarts.domain.exception.NotFoundException;
import br.org.hogwarts.domain.exception.UnprocessableEntityException;
import br.org.hogwarts.infrastructure.database.mongo.repositories.HousesQueryRepository;
import br.org.hogwarts.infrastructure.database.mongo.repositories.HousesRepository;
import br.org.hogwarts.mock.HousesMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class HogwartsDomainServiceTests {

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private MessageConfig messageConfig;

    @Mock
    private HousesRepository housesRepository;

    @Mock
    private HousesQueryRepository housesQueryRepository;

    @InjectMocks
    private HousesDomainService service;

    @Mock
    private HouseCreateRequest houseCreateRequest;

    private HousePatchRequest housePatchRequest;

    private Hogwarts hogwarts;

    private HouseResponse houseResponse;

    private Page<HouseResponse> houseResponsePage;

    private String name;

    private String color;

    private String leaderName;

    private String houseId;

    private LocalDateTime now;

    private Pageable pageable;


    @Test
    void shouldHouseDomainServiceUpdateThrowsUnprocessableEntityException() {
        givenAValidHouseCreateRequest();
        givenHouseRepositoryFindByIdReturnAValidEntity();
        givenHouseRepositoryExistsByIdNotAndNomeReturnsTrue();
        givenMessageConfigGetMessageReturnString();
        whenHouseDomainServiceUpdateThrowsAnUnprocessableEntityException();
        thenExpectHouseRepositoryExistsByIdNotAndNomeCalledOnce();
        thenExpectHouseRepositoryFindByIdCalledOnce();
    }

    @Test
    void shouldHouseDomainServiceUpdateThrowsNotFoundException() {
        givenAValidHouseCreateRequest();
        givenHouseRepositoryFindByIdReturnEmpty();
        givenMessageConfigGetMessageReturnString();
        whenHouseDomainServiceUpdateThrowsANotFoundException();
        thenExpectHouseRepositoryFindByIdCalledOnce();
    }

    @Test
    void shouldHouseDomainServiceUpdateThrowsInternalServerErrorException() {
        givenAValidHouseCreateRequest();
        givenHouseRepositoryFindByIdReturnAValidEntity();
        givenHouseRepositoryexistsByIdNotAndNomeReturnsFalse();
        givenHouseRepositorySaveHouseThrowsInternalServerErrorException();
        whenHouseDomainServiceUpdateThrowsAInternalServerErrorException();
        thenExpectHouseRepositoryExistsByIdNotAndNomeCalledOnce();
        thenExpectHouseRepositoryFindByIdCalledOnce();
        thenExpectHouseRepositorySaveCalledOnce();
    }

    @Test
    void shouldHouseDomainServiceCreateSuccessfully() {
        givenAValidHouseCreateRequest();
        givenHouseRepositoryExistsByNomeReturnsFalse();
        givenHouseRepositorySaveHouseReturnsHouseCreated();
        whenHouseDomainServiceCreateSuccessfully();
        thenExpectHouseRepositorySaveCalledOnce();
        thenExpectAValidHouseResponse();
    }

    @Test
    void shouldHouseDomainServiceCreateThrowsUnprocessableEntityException() {
        givenAValidHouseCreateRequest();
        givenHouseRepositoryExistsByNomeReturnsTrue();
        givenMessageConfigGetMessageReturnString();
        whenHouseDomainServiceCreateThrowsAnUnprocessableEntityException();
        thenExpectHouseResponseToBeNull();
    }

    @Test
    void shouldHouseDomainServiceCreateThrowsInternalServerErrorException() {
        givenAValidHouseCreateRequest();
        givenHouseRepositoryExistsByNomeReturnsFalse();
        givenHouseRepositorySaveHouseThrowsInternalServerErrorException();
        whenHouseDomainServiceCreateThrowsAnException();
        thenExpectHouseRepositoryExistsByMotivoCalledOnce();
        thenExpectHouseRepositorySaveCalledOnce();
        thenExpectHouseResponseToBeNull();
    }

    @Test
    void shouldHouseDomainServiceUpdateSuccessfully() {
        givenAValidHousePatchRequest();
        givenHouseRepositoryFindByIdReturnsTrue();
        givenHouseRepositorySaveHousePatchRequest();
        whenHouseDomainServicePatchSuccessfully();
        thenExpectHouseRepositorySaveCalledOnce();
        thenExpectHouseRepositoryFindByIdCalledOnce();
    }

    @Test
    void shouldHouseDomainServicePatchThrowsNotFoundException() {
        givenAValidHousePatchRequest();
        givenHouseRepositoryFindByIdReturnsEmpty();
        whenHouseDomainServicePatchThrowsNotFoundException();
        thenExpectHouseRepositoryFindByIdCalledOnce();
        thenExpectHouseRepositoryNotCalled();
    }

    @Test
    void shouldHouseDomainServicePatchThrowsInternalServerError() {
        givenAValidHousePatchRequest();
        givenHouseRepositoryFindByIdReturnsTrue();
        givenHouseRepositorySaveHouseThrowsInternalServerErrorException();
        whenHouseDomainServicePatchThrowsInternalServerErrorException();
        thenExpectHouseRepositoryFindByIdCalledOnce();
        thenExpectHouseRepositorySaveCalledOnce();
    }

    @Test
    void shouldHouseDomainServiceGetByIdSuccessfully() {
        givenAValidHouseId();
        givenAValidHouse();
        givenRepositoryFindByIdReturnsAValidHouse();
        whenCallHouseServiceGetById();
        thenExpectHouseRepositoryFindByIdCalled();
        thenExpectAValidHouseGetByIdResponse();
    }

    @Test
    void shouldHouseDomainServiceGetByIdThrowsNotFound() {
        givenAValidHouseId();
        givenRepositoryFindByIdReturnsAEmptyHouse();
        whenCallHouseServiceGetByIdThrowsANotFoundException();
        thenExpectHouseRepositoryFindByIdCalled();
        thenExpectHouseResponseToBeNull();
    }

    @Test
    void shouldHouseDomainServiceGetByIdThrowsInternalServerErrorException() {
        givenAValidHouseId();
        givenHouseRepositoryGetByIdHouseThrowsInternalServerErrorException();
        whenHouseDomainServiceGetByIdThrowsAnException();
        thenExpectHouseRepositoryFindByIdCalled();
        thenExpectHouseResponseToBeNull();
    }


    @Test
    void shouldHouseDomainServiceGetPagesSuccessfully() {
        givenAValidFilter();
        givenAValidPageable();
        givenApplicationConfigGetMaxResultsPerPageReturnsAnInteger();
        givenHouseRepositoryGindByAtivoAndMotivoMatch();
        whenHouseDomainServiceGetPagesSuccessfully();
        thenExpectAValidPageHouseResponse();
    }

    @Test
    void shouldHouseDomainServiceGetPagesThrowsMaxResultsPerPageException() {
        givenAValidFilter();
        givenAnInvalidPageable();
        givenApplicationConfigGetMaxResultsPerPageReturnsAnInteger();
        whenHouseDomainServiceGetPagesThrowsMaxResultsPerPageException();
        thenExpectPageHouseResponseToBeNull();
    }

    @Test
    void shouldHouseDomainServiceGetPagesThrowsInternalServerErrorException() {
        givenAValidFilter();
        givenAValidPageable();
        givenApplicationConfigGetMaxResultsPerPageReturnsAnInteger();
        givenHouseRepositoryFindByNomeAndCorMatchThrowsException();
        whenHouseDomainServiceGetPagesThrowsInternalServerErrorException();
        thenExpectPageHouseResponseToBeNull();
    }

    //GIVEN METHODS



    private void givenMessageConfigGetMessageReturnString() {
        when(messageConfig.getMessage(anyString(), any())).thenReturn("Error message");
    }

    private void givenAValidHouseCreateRequest() {
        houseCreateRequest = HousesMock.validHouseCreateRequest();
    }

    private void givenHouseRepositoryExistsByNomeReturnsFalse() {
        when(housesRepository.existsByName(anyString()))
                .thenReturn(false);
    }

    private void givenHouseRepositorySaveHouseReturnsHouseCreated() {
        now = LocalDateTime.now();
        when(housesRepository.save(any(Hogwarts.class)))
                .thenReturn(HousesMock.validHouse(now));
    }

    private void givenHouseRepositoryExistsByNomeReturnsTrue() {
        when(housesRepository.existsByName(anyString()))
                .thenReturn(true);
    }

    private void givenHouseRepositorySaveHouseThrowsInternalServerErrorException() {
        when(housesRepository.save(any(Hogwarts.class)))
                .thenThrow(RuntimeException.class);
    }

    private void givenAValidFilter() {
        name = "Gryffindor";
        color = "Red";
        leaderName = "Minerva";
    }

    private void givenHouseRepositoryGindByAtivoAndMotivoMatch() {
        when(housesQueryRepository.findWithFilter(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(HousesMock.validHouse(now))));
    }

    private void givenApplicationConfigGetMaxResultsPerPageReturnsAnInteger() {
        when(applicationConfig.getMaxResultsPerPage())
                .thenReturn(100);
    }

    private void givenAValidPageable() {
        pageable = HousesMock.validPageable();
    }

    private void givenAnInvalidPageable() {
        pageable = HousesMock.invalidPageable();
    }

    private void givenHouseRepositoryFindByNomeAndCorMatchThrowsException() {
        when(housesQueryRepository.findWithFilter(any(), any(), any(), any()))
                .thenThrow(InternalServerErrorException.class);
    }

    private void givenHouseRepositoryFindByIdReturnsEmpty() {
        when(housesRepository.findById(any())).thenReturn(Optional.empty());
    }

    private void givenHouseRepositorySaveHousePatchRequest() {
        when(housesRepository.save(any(Hogwarts.class)))
                .thenReturn(null);
    }

    private void givenHouseRepositoryFindByIdReturnsTrue() {
        when(housesRepository.findById(anyString()))
                .thenReturn(Optional.of(new Hogwarts())).thenReturn(Optional.empty());
    }

    private void givenAValidHousePatchRequest() {
        housePatchRequest = HousePatchRequest.builder()
                .leaderName("Snape")
                .build();
    }

    private void givenRepositoryFindByIdReturnsAValidHouse() {
        when(housesRepository.findById(any())).thenReturn(Optional.ofNullable(HousesMock.validHouse(now)));
    }

    private void givenAValidHouse() {
        hogwarts = HousesMock.validHouse(now);
    }

    private void givenAValidHouseId() {
        houseId = HousesMock.validHouse(now).getId();
    }

    private void givenHouseRepositoryGetByIdHouseThrowsInternalServerErrorException() {
        when(housesRepository.findById(anyString()))
                .thenThrow(InternalServerErrorException.class);
    }

    private void givenRepositoryFindByIdReturnsAEmptyHouse() {
        when(housesRepository.findById(anyString()))
                .thenReturn(Optional.empty());
    }

    private void givenHouseRepositoryexistsByIdNotAndNomeReturnsFalse() {
        when(housesRepository.existsByIdNotAndName(any(), anyString()))
                .thenReturn(false);
    }

    private void givenHouseRepositoryExistsByIdNotAndNomeReturnsTrue() {
        when(housesRepository.existsByIdNotAndName(any(), anyString()))
                .thenReturn(true);
    }

    private void givenHouseRepositoryFindByIdReturnAValidEntity() {
        hogwarts = houseCreateRequest.toHogwarts();
        when(housesRepository.findById(any())).thenReturn(Optional.ofNullable(hogwarts));
    }

    private void givenHouseRepositoryFindByIdReturnEmpty() {
        when(housesRepository.findById(any())).thenReturn(Optional.empty());
    }

    // WHEN METHODS

    private void whenHouseDomainServicePatchThrowsInternalServerErrorException() {
        assertThrows(InternalServerErrorException.class, () -> service.patchHouse(anyString(), housePatchRequest));
    }

    private void whenHouseDomainServicePatchThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> service.patchHouse(anyString(), housePatchRequest));
    }

    private void whenHouseDomainServicePatchSuccessfully() {
        service.patchHouse(anyString(), housePatchRequest);
    }

    private void whenHouseDomainServiceCreateSuccessfully() {
        houseResponse = service.createHouse(houseCreateRequest);
    }

    private void whenHouseDomainServiceCreateThrowsAnUnprocessableEntityException() {
        assertThrows(UnprocessableEntityException.class, () -> houseResponse = service.createHouse(houseCreateRequest));
    }

    private void whenHouseDomainServiceCreateThrowsAnException() {
        assertThrows(Exception.class, () -> houseResponse = service.createHouse(houseCreateRequest));
    }

    private void whenHouseDomainServiceGetPagesSuccessfully() {
        houseResponsePage = service.getPagesHouses(name, color, leaderName, pageable);
    }

    private void whenHouseDomainServiceGetPagesThrowsMaxResultsPerPageException() {
        assertThrows(MaxResultsPerPageException.class,
                () -> houseResponsePage = service.getPagesHouses(name, color, leaderName, pageable));
    }

    private void whenCallHouseServiceGetById() {
        houseResponse = service.getByIdHouse(houseId);
    }

    private void whenHouseDomainServiceGetByIdThrowsAnException() {
        assertThrows(Exception.class, () -> houseResponse = service.getByIdHouse(houseId));
    }

    private void whenCallHouseServiceGetByIdThrowsANotFoundException() {
        assertThrows(NotFoundException.class, () -> houseResponse = service.getByIdHouse(houseId));
    }

    private void whenHouseDomainServiceGetPagesThrowsInternalServerErrorException() {
        assertThrows(InternalServerErrorException.class,
                () -> houseResponsePage = service.getPagesHouses(name, color, leaderName, pageable));
    }

    private void whenHouseDomainServiceUpdateThrowsAInternalServerErrorException() {
        assertThrows(InternalServerErrorException.class, () -> service.updateHouse(houseCreateRequest.toHogwarts().getId(), houseCreateRequest));
    }

    private void whenHouseDomainServiceUpdateThrowsANotFoundException() {
        assertThrows(NotFoundException.class, () -> service.updateHouse(houseCreateRequest.toHogwarts().getId(), houseCreateRequest));
    }

    private void whenHouseDomainServiceUpdateThrowsAnUnprocessableEntityException() {
        assertThrows(UnprocessableEntityException.class, () -> service.updateHouse(houseCreateRequest.toHogwarts().getId(), houseCreateRequest));
    }

    //THEN METHODS

    private void thenExpectHouseRepositoryExistsByMotivoCalledOnce() {
        verify(housesRepository).existsByName(anyString());
    }

    private void thenExpectHouseRepositorySaveCalledOnce() {
        verify(housesRepository).save(any(Hogwarts.class));
    }

    private void thenExpectAValidHouseResponse() {
        assertNotNull(houseResponse);
        assertEquals("635877b5377fd21cda7975c3", houseResponse.getId());
        assertEquals("Gryffindor", houseResponse.getName());
        assertEquals("Red", houseResponse.getColor());
        assertEquals("Minerva", houseResponse.getLeaderName());
        assertEquals(now, houseResponse.getDataCriacao());
        assertEquals(now, houseResponse.getDataAtualizacao());
    }

    private void thenExpectHouseResponseToBeNull() {
        assertNull(houseResponse);
    }


    private void thenExpectHouseRepositoryExistsByIdNotAndNomeCalledOnce() {
        verify(housesRepository).existsByIdNotAndName(any(), anyString());
    }


    private void thenExpectHouseRepositoryFindByIdCalledOnce() {
        verify(housesRepository).findById(anyString());
    }

    private void thenExpectHouseRepositoryNotCalled() {
        verify(housesRepository, times(0)).save(any());
    }

    private void thenExpectAValidPageHouseResponse() {
        assertNotNull(houseResponsePage);
    }

    private void thenExpectPageHouseResponseToBeNull() {
        assertNull(houseResponsePage);
    }

    private void thenExpectAValidHouseGetByIdResponse() {
        assertEquals(hogwarts.getName(), houseResponse.getName());
        assertEquals(hogwarts.getColor(), houseResponse.getColor());
        assertEquals(hogwarts.getLeaderName(), houseResponse.getLeaderName());
        assertEquals(hogwarts.getDataCriacao(), houseResponse.getDataCriacao());
        assertEquals(hogwarts.getDataAtualizacao(), houseResponse.getDataAtualizacao());
    }

    private void thenExpectHouseRepositoryFindByIdCalled() {
        verify(housesRepository).findById(anyString());
    }
}
