package com.nttdata.client.microservice.controller;

import com.mongodb.DuplicateKeyException;
import com.nttdata.client.microservice.api.ClientApi;
import com.nttdata.client.microservice.mapper.ClientMapper;
import com.nttdata.client.microservice.service.ClientService;
import com.nttdata.client.model.Client;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
public class ClientController implements ClientApi {

    private static final String TIMESTAMP = "timestamp";
    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientMapper clientMapper;


    @Override
    public Mono<ResponseEntity<Map<String, Object>>> createClient(Mono<Client> client, ServerWebExchange exchange) {
        Map<String, Object> response =  new HashMap<>();
        return clientService.save(client.map(clientMapper::toDocument))
                .map(clientMapper::toModel)
                .map(c -> {
                    response.put("client",c);
                    response.put("message", "Successful client saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }).onErrorResume(getThrowableMonoFunction(response));


    }

    @Override
    public Mono<ResponseEntity<Void>> deleteClient(String id, ServerWebExchange exchange) {
        return clientService.findById(id)
                .flatMap(c -> clientService.delete(c)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    public Mono<ResponseEntity<Map<String, Object>>> updateClient(String id, Mono<Client> client, ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        return clientService.update(id, client.map(clientMapper::toDocument))
                .map(clientMapper::toModel)
                .map(c -> {
                    response.put("client", c);
                    response.put("message", "Successful client saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.OK).body(response);
                })
                .onErrorResume(WebExchangeBindException.class, getThrowableMonoFunction(response))
                .onErrorResume(DuplicateKeyException.class, getThrowableDuplicate(response))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public  Mono<ResponseEntity<Flux<Map<String, Object>>>> findAllClient(ServerWebExchange exchange) {
        Flux<com.nttdata.client.microservice.domain.Client> clientsFlux = clientService.findAll();

        Flux<Map<String, Object>> clientsMapFlux = clientsFlux.map(client -> {
            Map<String, Object> clientMap = new HashMap<>();
            clientMap.put("id", client.getId());
            clientMap.put("dni", client.getDni());
            clientMap.put("names", client.getNames());
            clientMap.put("surnames", client.getSurnames());
            clientMap.put("email", client.getEmail());
            clientMap.put("phone", client.getPhone());
            clientMap.put("type", client.getType());
            return clientMap;
        });

        return Mono.just(ResponseEntity
                .status(HttpStatus.OK)
                .body(clientsMapFlux));

    }

    @Override
    public Mono<ResponseEntity<Client>> getClientByDNI(String dni, ServerWebExchange exchange) {

        return clientService.findByDni(dni)
                .map(clientMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    @Override
    public Mono<ResponseEntity<Client>> getClientById(String id, ServerWebExchange exchange) {
        return clientService.findById(id)
                .map(clientMapper::toModel)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableMonoFunction(Map<String, Object> response){
        return t -> Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap(e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(Flux::fromIterable)
                .map(fieldError -> "Campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l);
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }

    private static Function<Throwable, Mono<? extends ResponseEntity<Map<String, Object>>>> getThrowableDuplicate(Map<String, Object> response){
        return t -> Mono.just(t).cast(DuplicateKeyException.class)
                .flatMap(l -> {
                    response.put(TIMESTAMP, new Date());
                    response.put("status", HttpStatus.BAD_REQUEST.value());
                    response.put("errors", l.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(response));
                });
    }


}
