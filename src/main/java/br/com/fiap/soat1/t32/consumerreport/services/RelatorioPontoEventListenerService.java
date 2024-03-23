package br.com.fiap.soat1.t32.consumerreport.services;

import org.springframework.stereotype.Service;

import br.com.fiap.soat1.t32.consumerreport.models.user.Ponto;
import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.RelatorioPontoRequest;
import br.com.fiap.soat1.t32.consumerreport.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

            log.info("Pontos mes passado:", pontosMesPassado.size());

        }catch (Exception ex){
			log.error("Falha ao relatorio ponto", ex);
		}
    }

}
