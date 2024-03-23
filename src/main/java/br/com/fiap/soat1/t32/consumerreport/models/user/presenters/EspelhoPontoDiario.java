package br.com.fiap.soat1.t32.consumerreport.models.user.presenters;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.soat1.t32.consumerreport.models.user.Ponto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspelhoPontoDiario {

    private LocalDateTime data;

    private List<Ponto> pontos;

    private LocalDateTime entrada;

    private LocalDateTime saida;

    private long totalHoras;
}
