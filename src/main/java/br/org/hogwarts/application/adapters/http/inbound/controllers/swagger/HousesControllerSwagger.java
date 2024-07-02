package br.org.hogwarts.application.adapters.http.inbound.controllers.swagger;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HouseCreateRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.request.HousePatchRequest;
import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response.HouseResponse;
import br.org.hogwarts.domain.exception.MessageErrorCodeConstants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping(value = "/houses", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Hogwarts Houses", description = "Hogwarts Houses")
public interface HousesControllerSwagger {
    @ApiOperation(value = "Adiciona uma nova casa.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Objeto criado com sucesso", response = HouseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request - Valida os atributos obrigatórios de acordo com as definições do swagger.",
                    response = HouseResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized - O cliente não possui credenciais de autenticação válidas."),
            @ApiResponse(code = 403, message = "Forbidden - O servidor está se recusando a responder. Isso geralmente é causado por escopos de acesso incorretos."),
            @ApiResponse(code = 404, message = "Not Found - O recurso solicitado não foi encontrado."),
            @ApiResponse(code = 422, message = "Unprocessable Entity - O corpo da solicitação contém erros semânticos. Isso geralmente é causado por não atender à regra de negócios do domínio."),
            @ApiResponse(code = 500, message = "Internal Server Error - Ocorreu um erro interno."),
            @ApiResponse(code = 503, message = "Service Unavailable - O servidor não está disponível no momento."),
            @ApiResponse(code = 504, message = "Gateway Timeout - A solicitação não pôde ser concluída."),
    })
    @PostMapping
    ResponseEntity<HouseResponse> post(@RequestBody @Valid HouseCreateRequest houseCreateRequest);

    @ApiOperation(value = "Recupera uma lista de casas.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de casas", responseContainer = "Page", response = Page.class)

    })
    @GetMapping
    ResponseEntity<List<HouseResponse>> getList(@RequestParam Map<String, Object> params,
                                                @RequestParam(required = false) @Min(value = 1, message = MessageErrorCodeConstants.FIELD_MUST_BE_GREATER_THAN_VALUE) @Max(value = 10, message = MessageErrorCodeConstants.FIELD_MUST_BE_LESS_THAN_VALUE) Integer page,
                                                @RequestParam(required = false) @Min(value = 1, message = MessageErrorCodeConstants.FIELD_MUST_BE_GREATER_THAN_VALUE) Integer limit,
                                                @RequestParam(required = false) String name,
                                                @RequestParam(required = false) String color,
                                                @RequestParam(required = false) String leaderName);

    @ApiOperation(value = "Recupera os dados de uma casa específica.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Objeto localizado com sucesso.", response = HouseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request - Valida os atributos obrigatórios de acordo com as definições do swagger."),
            @ApiResponse(code = 401, message = "Unauthorized - O cliente não possui credenciais de autenticação válidas."),
            @ApiResponse(code = 403, message = "Forbidden - O servidor está se recusando a responder. Isso geralmente é causado por escopos de acesso incorretos"),
            @ApiResponse(code = 404, message = "Not Found - O recurso solicitado não foi encontrado"),
            @ApiResponse(code = 422, message = "Unprocessable Entity - O corpo da solicitação contém erros semânticos. Isso geralmente é causado por não atender à regra de negócios do domínio."),
            @ApiResponse(code = 500, message = "Internal Server Error - Ocorreu um erro interno."),
            @ApiResponse(code = 503, message = "Service Unavailable - O servidor não está disponível no momento."),
            @ApiResponse(code = 504, message = "Gateway Timeout - A solicitação não pôde ser concluída.")
    })
    @GetMapping("/{houseId}")
    ResponseEntity<HouseResponse> get(@PathVariable("houseId") UUID houseId);


    @ApiOperation(value = "Atualiza os dados de uma casa.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Objeto atualizado com sucesso."),
            @ApiResponse(code = 404, message = "Not Found - O recurso solicitado não foi encontrado."),
            @ApiResponse(code = 422, message = "Entidade não processada"),
            @ApiResponse(code = 500, message = "Service Unavailable - O servidor não está disponível no momento."),
    })
    @PutMapping("/{houseId}")
    ResponseEntity<Void> put(@PathVariable("houseId") UUID houseId, @RequestBody(required = true) @Valid HouseCreateRequest houseCreateRequest);

    @ApiOperation(value = "Atualiza o nome do líder da casa.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Objeto atualizado com sucesso.", response = HouseResponse.class),
            @ApiResponse(code = 400, message = "Bad Request - Valida os atributos obrigatórios de acordo com as definições do swagger.",
                    response = HouseResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized - O cliente não possui credenciais de autenticação válidas."),
            @ApiResponse(code = 403, message = "Forbidden - O servidor está se recusando a responder. Isso geralmente é causado por escopos de acesso incorretos."),
            @ApiResponse(code = 404, message = "Not Found - O recurso solicitado não foi encontrado."),
            @ApiResponse(code = 422, message = "Entidade não processada"),
            @ApiResponse(code = 500, message = "Erro interno do servidor ou serviço."),
            @ApiResponse(code = 503, message = "Service Unavailable - O servidor não está disponível no momento."),
            @ApiResponse(code = 504, message = "Gateway Timeout - A solicitação não pôde ser concluída."),
    })
    @PatchMapping("/{houseId}/leader")
    ResponseEntity<Void> patch(@PathVariable("houseId") UUID houseId, @RequestBody @Valid HousePatchRequest housePatchRequest);


}
