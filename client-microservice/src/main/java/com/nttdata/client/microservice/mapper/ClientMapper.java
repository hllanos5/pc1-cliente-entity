package com.nttdata.client.microservice.mapper;

import com.nttdata.client.model.Client; //Autogenerado
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper implements EntityMapper<Client, com.nttdata.client.microservice.domain.Client>{

    @Override
    public com.nttdata.client.microservice.domain.Client ToDocument(Client model) {
        com.nttdata.client.microservice.domain.Client client= new com.nttdata.client.microservice.domain.Client();
        BeanUtils.copyProperties(model, client);
        return client;
    }

    @Override
    public Client toModel(com.nttdata.client.microservice.domain.Client domain) {
        Client client= new Client();
        BeanUtils.copyProperties(domain, client);
        return client;
    }
}
