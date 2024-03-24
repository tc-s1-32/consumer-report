package br.com.fiap.soat1.t32.consumerreport.models.user.presenters;

import java.time.LocalDate;
import java.util.List;

import br.com.fiap.soat1.t32.consumerreport.models.user.Ponto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspelhoPontoDiario {

    @JsonFormat()
    private LocalDate data;

    private List<Ponto> pontos;

    private long totalHoras;
    private long totalMinutos;
}
