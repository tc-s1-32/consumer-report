package br.com.fiap.soat1.t32.consumerreport.models.user.presenters;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspelhoPontoMensal {
    private List<EspelhoPontoDiario> espelhoPontoDiarios;
    private long totalHoras;
}
