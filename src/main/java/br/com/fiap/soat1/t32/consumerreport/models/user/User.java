package br.com.fiap.soat1.t32.consumerreport.models.user;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user")
public class User {

    @Id
    private String id;

    private String nome;

    private String email;

    private String senha;

    private List<Ponto> pontos;
 
}
