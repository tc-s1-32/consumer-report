package br.com.fiap.soat1.t32.consumerreport.models.user.presenters;

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
public class EspelhoPontoMensal {
    
    private List<EspelhoPontoDiario> espelhoPontoDiarios;
    private long totalMinutos;

    public static String buildHtmlContent(EspelhoPontoMensal espelhoPontoMensal) {
        StringBuilder htmlContent = new StringBuilder();

        // Início do documento HTML
        htmlContent.append("<html><body>");

        // Adiciona informações sobre o espelhoPontoMensal
        htmlContent.append("<h1>Relatório Mensal de Ponto</h1>");
        htmlContent.append("<p>Total de Minutos: ").append(espelhoPontoMensal.getTotalMinutos()).append("</p>");

        // Adiciona informações sobre os espelhoPontoDiarios
        List<EspelhoPontoDiario> espelhoPontoDiarios = espelhoPontoMensal.getEspelhoPontoDiarios();
        for (EspelhoPontoDiario espelhoPontoDiario : espelhoPontoDiarios) {
            htmlContent.append("<h2>Data: ").append(espelhoPontoDiario.getData()).append("</h2>");
            htmlContent.append("<p>Total de Minutos: ").append(espelhoPontoDiario.getTotalMinutos()).append("</p>");

            // Adiciona informações sobre os pontos
            List<Ponto> pontos = espelhoPontoDiario.getPontos();
            for (Ponto ponto : pontos) {
                htmlContent.append("<p>Data do Ponto: ").append(ponto.getData()).append("</p>");
                htmlContent.append("<p>Evento Pronto: ").append(ponto.getEventoPronto()).append("</p>");
            }
        }

        // Fim do documento HTML
        htmlContent.append("</body></html>");

        return htmlContent.toString();
    }
}
