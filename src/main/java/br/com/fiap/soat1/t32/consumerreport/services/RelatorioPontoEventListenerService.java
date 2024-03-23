package br.com.fiap.soat1.t32.consumerreport.services;

import org.springframework.stereotype.Service;

import br.com.fiap.soat1.t32.consumerreport.enums.EventoPronto;
import br.com.fiap.soat1.t32.consumerreport.models.user.Ponto;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.EspelhoPontoDiario;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.EspelhoPontoMensal;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.RelatorioPontoRequest;
import br.com.fiap.soat1.t32.consumerreport.repositories.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioPontoEventListenerService {

    private final UserRepository userRepository;

    @RabbitListener(queues = {"${fila.relatorio.ponto}"})
    public void postRelatorioPonto(RelatorioPontoRequest relatorioPontoRequest) {
        try{

            log.info("Solicitado relatorio ponto: ", relatorioPontoRequest.getUserId());

            LocalDate primeiroDiaMesPassado = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDateTime dataInicial = LocalDateTime.of(primeiroDiaMesPassado, LocalTime.MIN);

            LocalDate ultimoDiaMesPassado = LocalDate.now().minusMonths(1).withDayOfMonth(
            LocalDate.now().minusMonths(1).lengthOfMonth());
            LocalDateTime dataFinal = LocalDateTime.of(ultimoDiaMesPassado, LocalTime.MAX);

            List<Ponto> pontosMesPassado = userRepository.findPontosByUserIdAndData(relatorioPontoRequest.getUserId(),dataInicial,dataFinal);

            EspelhoPontoMensal espelhoPontoMensal = new EspelhoPontoMensal();
            espelhoPontoMensal.setEspelhoPontoDiarios(new ArrayList<>());
            espelhoPontoMensal.setTotalHoras(0);

            for (LocalDate data = primeiroDiaMesPassado; !data.isAfter(ultimoDiaMesPassado); data = data.plusDays(1)) {

                LocalDateTime dataDiaInicial = LocalDateTime.of(data, LocalTime.MIN);
                LocalDateTime dataDiaFinal = LocalDateTime.of(data, LocalTime.MAX);

                List<Ponto> pontosDia = pontosMesPassado.stream()
                        .filter(ponto -> ponto.getData().isAfter(dataDiaInicial) && ponto.getData().isBefore(dataDiaFinal))
                        .collect(Collectors.toList());
        
                var espelhoPontoDiario = EspelhoPontoDiario.builder()
                    .data(dataDiaInicial)
                    .pontos(pontosDia)
                    .totalHoras(calcularTotalHorasDia(pontosDia))
                    .build();

                espelhoPontoMensal.getEspelhoPontoDiarios().add(espelhoPontoDiario);
                long totalHoras = espelhoPontoMensal.getTotalHoras();
                totalHoras += espelhoPontoDiario.getTotalHoras();
                espelhoPontoMensal.setTotalHoras(totalHoras);
            }

            log.info("Pontos mes passado:", pontosMesPassado.size());

        }catch (Exception ex){
			log.error("Falha ao relatorio ponto", ex);
		}
    }

    public long calcularTotalHorasDia(List<Ponto> pontosDia) {
        Long totalHoras = 0L;

        LocalDateTime tempo1 = null;
        LocalDateTime tempo2 = null;

        for (Ponto ponto : pontosDia) {
            EventoPronto eventoPronto = ponto.getEventoPronto();

            if (eventoPronto == EventoPronto.ENTRADA || eventoPronto == EventoPronto.INTERVALO_OFF) {
                tempo1 = ponto.getData();
            } else {
                tempo2 = ponto.getData();
            }
            if (tempo1 != null && tempo2 != null){
                if (tempo1.isBefore(tempo2)) {
                    Duration duracao = Duration.between(tempo1, tempo2);
                    totalHoras += duracao.toHours();;
                } 
            }
        }

        return totalHoras;
    }

}