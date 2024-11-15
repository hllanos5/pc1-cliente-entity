package com.nttdata.client.microservice.repository;

import com.nttdata.client.microservice.domain.Client;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
    Mono<Client> findByDni(String dni);
}
