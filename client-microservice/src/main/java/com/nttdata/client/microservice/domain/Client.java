package com.nttdata.client.microservice.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Getter
@Setter
@Document(collection  = "client")
public class Client {
    @Id
    private String id;
    @Indexed(unique = true)
    private String dni;
    private String names;
    private String surnames;
    @Indexed(unique = true)
    private String email;
    private String phone;
    private String type;

}
