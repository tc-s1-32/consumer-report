package br.com.fiap.soat1.t32.consumerreport.services;

import br.com.fiap.soat1.t32.consumerreport.enums.EventoPronto;
import br.com.fiap.soat1.t32.consumerreport.models.user.Ponto;
import br.com.fiap.soat1.t32.consumerreport.models.user.User;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.EspelhoPontoDiario;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.EspelhoPontoMensal;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.RelatorioPontoRequest;
import br.com.fiap.soat1.t32.consumerreport.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioPontoEventListenerService {

    private final EnvioEmailRelatorioPonto envioEmailRelatorioPonto;

    private final UserRepository userRepository;

    @RabbitListener(queues = {"${fila.relatorio.ponto}"})
    public void postRelatorioPonto(RelatorioPontoRequest relatorioPontoRequest) {
        try{
            
            log.info("Solicitado relatorio ponto: " + relatorioPontoRequest.getUserId());

            User user = userRepository.findById(relatorioPontoRequest.getUserId()).orElseThrow();
            
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

                var minutosDia = calcularTotalMinutosDia(pontosDia);

                var espelhoPontoDiario = EspelhoPontoDiario.builder()
                    .data(data)
                    .pontos(pontosDia)
                    .totalHoras(minutosDia/60)
                    .totalMinutos(minutosDia%60)
                    .build();

                espelhoPontoMensal.getEspelhoPontoDiarios().add(espelhoPontoDiario);
                long minutosMes = espelhoPontoMensal.getTotalMinutos();
                minutosMes += espelhoPontoDiario.getTotalMinutos();
                espelhoPontoMensal.setTotalHoras(minutosMes / 60);
                espelhoPontoMensal.setTotalMinutos(minutosMes % 60);
                espelhoPontoMensal.setNomeColaborador(user.getNome());
            }

            envioEmailRelatorioPonto.sendReport(espelhoPontoMensal, user.getEmail());

            log.info("Relatório de ponto enviado: " + relatorioPontoRequest.getUserId());
            
        }catch (Exception ex){
			log.error("Falha ao relatorio ponto", ex);
		}
    }

    public long calcularTotalMinutosDia(List<Ponto> pontosDia) {
        var totalMinutos = 0L;

        LocalDateTime tempo1 = null;
        LocalDateTime tempo2 = null;

        for (Ponto ponto : pontosDia) {
            EventoPronto eventoPronto = ponto.getEventoPronto();

            if (eventoPronto == EventoPronto.ENTRADA || eventoPronto == EventoPronto.INTERVALO_OFF) {
                tempo1 = ponto.getData().withSecond(0);
            } else {
                tempo2 = ponto.getData().withSecond(0);
            }
            if (tempo1 != null && tempo2 != null){
                if (tempo1.isBefore(tempo2)) {
                    Duration duracao = Duration.between(tempo1, tempo2);
                    totalMinutos += duracao.toMinutes();
                } 
            }
        }

        return totalMinutos;
    }

}
