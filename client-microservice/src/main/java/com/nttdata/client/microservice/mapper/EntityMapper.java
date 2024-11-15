package com.nttdata.client.microservice.mapper;

public interface EntityMapper<D,E>{
    E toDomain(D model);
    D toModel(E domain);
}
