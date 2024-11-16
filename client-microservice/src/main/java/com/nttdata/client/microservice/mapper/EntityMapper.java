package com.nttdata.client.microservice.mapper;

public interface EntityMapper<D,E>{
    E ToDocument(D model);
    D toModel(E domain);
}
