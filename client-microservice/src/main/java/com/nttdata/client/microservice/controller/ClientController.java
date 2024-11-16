package com.nttdata.client.microservice.controller;

import com.nttdata.client.microservice.api.ClientApi;
import com.nttdata.client.microservice.mapper.ClientMapper;
import com.nttdata.client.microservice.service.ClientService;
import com.nttdata.client.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<Map<String, Object>>> addClient(Mono<Client> client, ServerWebExchange exchange) {
        Map<String, Object> response =  new HashMap<>();
        return clientService.save(client.map(clientMapper::ToDocument))
                .map(clientMapper::toModel)
                .map(c -> {
                    response.put("client",c);
                    response.put("message", "Successful client saved");
                    response.put(TIMESTAMP, new Date());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                }).onErrorResume(getThrowableMonoFunction(response));


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
}
