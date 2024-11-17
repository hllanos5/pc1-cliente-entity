package com.nttdata.client.microservice.service;

import com.nttdata.client.microservice.domain.Client;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientService {
    Mono<Client> save(Mono<Client> client);
    Mono<Client> findById(String id);
    Flux<Client> findAll();
    Mono<Client> findByDni(String dni);
    Mono<Client> update(String id, Mono<Client> client);
    Mono<Void> delete(Client client);
}
