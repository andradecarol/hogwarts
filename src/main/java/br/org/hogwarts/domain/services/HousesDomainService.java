package br.org.hogwarts.domain.services;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HouseCreateRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HousePatchRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response.HouseResponse;
import br.org.hogwarts.config.application.ApplicationConfig;
import br.org.hogwarts.config.application.MessageConfig;
import br.org.hogwarts.domain.entities.Hogwarts;
import br.org.hogwarts.domain.exception.InternalServerErrorException;
import br.org.hogwarts.domain.exception.MaxResultsPerPageException;
import br.org.hogwarts.domain.exception.MessageErrorCodeConstants;
import br.org.hogwarts.domain.exception.NotFoundException;
import br.org.hogwarts.domain.exception.UnprocessableEntityException;
import br.org.hogwarts.domain.services.interfaces.HousesService;
import br.org.hogwarts.infrastructure.database.mongo.repositories.HousesQueryRepository;
import br.org.hogwarts.infrastructure.database.mongo.repositories.HousesRepository;
import br.org.hogwarts.utils.messages.MessageConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.org.hogwarts.domain.exception.MessageErrorCodeConstants.FIELD_MUST_BE_LESS_THAN_VALUE;
import static br.org.hogwarts.domain.exception.MessageErrorCodeConstants.INTERNAL_SERVER_ERROR;
import static br.org.hogwarts.domain.exception.MessageErrorCodeConstants.MOTIVO_ID_NOT_FOUND;
import static br.org.hogwarts.domain.exception.MessageErrorCodeConstants.NAME_HOUSE_ALREADY_EXISTS;
import static br.org.hogwarts.domain.exception.MessageErrorCodeConstants.UNEXPECTED_SERVER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class HousesDomainService implements HousesService {

    private final MessageConfig messageConfig;

    private final ApplicationConfig applicationConfig;

    private final HousesRepository housesRepository;

    private final HousesQueryRepository housesQueryRepository;

    @Override
    public HouseResponse createHouse(HouseCreateRequest houseCreateRequest) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            log.info(messageConfig.getMessage(MessageConstants.CREATING), methodName);

            if (housesRepository.existsByName(houseCreateRequest.getName())) {
                throw new UnprocessableEntityException(NAME_HOUSE_ALREADY_EXISTS,
                        messageConfig.getMessage(MessageErrorCodeConstants.NAME_HOUSE_ALREADY_EXISTS,
                                houseCreateRequest.getName()));
            }

            log.info(messageConfig.getMessage(MessageConstants.SAVING), methodName);
            Hogwarts response = housesRepository.save(houseCreateRequest.toHogwarts());
            return HouseResponse.from(response);
        } catch (UnprocessableEntityException ex) {
            log.error(messageConfig.getMessage(MessageConstants.UNPROCESSABLE_ENTITY_LOG), methodName, ex.getMessage(), ex.toString());
            throw ex;
        } catch (Exception ex) {
            log.error(messageConfig.getMessage(MessageConstants.INTERNAL_ERROR_LOG), methodName, ex.getMessage(), ex.toString());
            throw new InternalServerErrorException(UNEXPECTED_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    public Page<HouseResponse> getPagesHouses(String name, String color, String leaderName, Pageable pageable) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        try {
            log.info(messageConfig.getMessage(MessageConstants.EXECUTING_QUERY), methodName);

            if (!validateMaxResultsPerPage(pageable)) {
                throw new MaxResultsPerPageException(FIELD_MUST_BE_LESS_THAN_VALUE,
                        messageConfig.getMessage(FIELD_MUST_BE_LESS_THAN_VALUE, "limit",
                                applicationConfig.getMaxResultsPerPage()));
            }

            final var response = housesQueryRepository.findWithFilter(name, color, leaderName, pageable);

            return response.map(HouseResponse::from);
        } catch (MaxResultsPerPageException ex) {
            log.error(messageConfig.getMessage(MessageConstants.MAX_RESULT_PER_PAGE_LOG), methodName, ex.getMessage(), ex.toString());
            throw ex;
        } catch (Exception ex) {
            log.error(messageConfig.getMessage(MessageConstants.INTERNAL_ERROR_LOG), methodName, ex.getMessage(), ex.toString());
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    public HouseResponse getByIdHouse(String id) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            log.info(messageConfig.getMessage(MessageConstants.RESOURCE_GET_BY_ID), methodName, Hogwarts.class.getName(), id);
            return housesRepository.findById(id).map(mr -> {
                log.info(messageConfig.getMessage(MessageConstants.RESOURCE_FOUND), methodName, Hogwarts.class.getName(), id);
                return HouseResponse.from(mr);
            }).orElseThrow(() -> {
                log.info(messageConfig.getMessage(MessageConstants.RESOURCE_NOT_FOUND), methodName, Hogwarts.class.getName(), id);
                return new NotFoundException(MOTIVO_ID_NOT_FOUND, messageConfig.getMessage(MessageErrorCodeConstants.MOTIVO_ID_NOT_FOUND, id));
            });
        } catch (NotFoundException nfe) {
            throw nfe;
        } catch (Exception ex) {
            log.error(messageConfig.getMessage(MessageConstants.INTERNAL_ERROR_LOG), methodName, ex.getMessage(), ex.toString());
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    public void updateHouse(String id, HouseCreateRequest houseCreateRequest) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        try {
            log.info(messageConfig.getMessage(MessageConstants.EXECUTING_QUERY), methodName);

            Hogwarts hogwarts = housesRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(MOTIVO_ID_NOT_FOUND,
                            messageConfig.getMessage(MOTIVO_ID_NOT_FOUND, id)));

            log.info(messageConfig.getMessage(MessageConstants.RESOURCE_GET_BY_ID), methodName, Hogwarts.class.getName(), id);

            if (housesRepository.existsByIdNotAndName(id, houseCreateRequest.getName())) {
                throw new UnprocessableEntityException(NAME_HOUSE_ALREADY_EXISTS,
                        messageConfig.getMessage(MessageErrorCodeConstants.NAME_HOUSE_ALREADY_EXISTS,
                                houseCreateRequest.getName()));
            }

            hogwarts.setName(houseCreateRequest.getName());
            hogwarts.setColor(houseCreateRequest.getColor());
            hogwarts.setLeaderName(houseCreateRequest.getLeaderName());
            hogwarts.setDataAtualizacao(LocalDateTime.now());

            housesRepository.save(hogwarts);
            log.info(messageConfig.getMessage(MessageConstants.RESOURCE_UPDATE_LOG), methodName, Hogwarts.class.getName(), id);
        } catch (UnprocessableEntityException ex) {
            log.error(messageConfig.getMessage(MessageConstants.UNPROCESSABLE_ENTITY_LOG), methodName, ex.getMessage(), ex.toString());
            throw ex;
        } catch (NotFoundException ex) {
            log.error(messageConfig.getMessage(MessageConstants.RESOURCE_NOT_FOUND), methodName, ex.getMessage(), ex.toString());
            throw ex;
        } catch (Exception ex) {
            log.error(messageConfig.getMessage(MessageConstants.INTERNAL_ERROR_LOG), methodName, ex.getMessage(), ex.toString());
            throw new InternalServerErrorException(UNEXPECTED_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    public void patchHouse(String id, HousePatchRequest housePatchRequest) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        try {
            log.info(messageConfig.getMessage(MessageConstants.RESOURCE_UPDATE_LOG), methodName, Hogwarts.class.getName(), id);

            Hogwarts hogwarts = housesRepository.findById(id).orElseThrow(() ->
                    new NotFoundException(MOTIVO_ID_NOT_FOUND, messageConfig.getMessage(MOTIVO_ID_NOT_FOUND, id)));

            hogwarts.setLeaderName(housePatchRequest.getLeaderName());
            hogwarts.setDataAtualizacao(LocalDateTime.now());

            housesRepository.save(hogwarts);
        } catch (NotFoundException ex) {
            log.error(messageConfig.getMessage(MessageConstants.RESOURCE_NOT_FOUND), methodName, ex.getMessage(), ex.toString());
            throw ex;
        } catch (Exception ex) {
            log.error(messageConfig.getMessage(MessageConstants.INTERNAL_ERROR_LOG), methodName, ex.getMessage(), ex.toString());
            throw new InternalServerErrorException(INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private boolean validateMaxResultsPerPage(Pageable pageable) {
        return pageable.getPageSize() <= applicationConfig.getMaxResultsPerPage();
    }
}
