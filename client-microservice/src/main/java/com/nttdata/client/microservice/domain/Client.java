package com.nttdata.client.microservice.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document(collation = "client")
public class Client {
    @Id
    private String Id;
    private String dni;
    private String names;
    private String surnames;
    private String email;
    private String phone;

}
