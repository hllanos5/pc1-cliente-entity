package com.nttdata.client.microservice.service;

import com.nttdata.client.microservice.domain.Client;
import com.nttdata.client.microservice.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ClientServiceImp  implements ClientService{
    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Mono<Client> save(Mono<Client> client) {
        return client.flatMap(clientRepository::insert);
    }

    @Override
    public Mono<Client> findById(String id) {
        return clientRepository.findById(id);
    }

    @Override
    public Mono<Client> findByDni(String dni) {
        return clientRepository.findByDni(dni);
    }

    @Override
    public Mono<Client> update(String id, Mono<Client> client) {
        return clientRepository.findById(id)
                .flatMap(c -> client)
                .doOnNext(e -> e.setId(id))
                .flatMap(clientRepository::save)
                .defaultIfEmpty(null);

    }

    @Override
    public Mono<Void> delete(Client client) {
        return clientRepository.delete(client);
    }
}
