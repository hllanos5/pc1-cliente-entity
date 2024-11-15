package com.nttdata.client.microservice.mapper;

import com.nttdata.client.microservice.domain.Client;
import com.nttdata.client.model.ClientRequest;
import org.springframework.beans.BeanUtils;

public class ClientMapper implements EntityMapper<ClientRequest, Client>{

    @Override
    public Client toDomain(ClientRequest model) {
        Client client= new Client();
        BeanUtils.copyProperties(model, client);
        return client;
    }

    @Override
    public ClientRequest toModel(Client domain) {
        ClientRequest clientRequest= new ClientRequest();
        BeanUtils.copyProperties(domain, clientRequest);
        return clientRequest;
    }
}
