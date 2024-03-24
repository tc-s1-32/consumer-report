package br.com.fiap.soat1.t32.consumerreport.models.user.presenters;

import java.util.List;

import br.com.fiap.soat1.t32.consumerreport.models.user.Ponto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static br.com.fiap.soat1.t32.consumerreport.utils.DateTimeUtils.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EspelhoPontoMensal {
    
    private List<EspelhoPontoDiario> espelhoPontoDiarios;
    private long totalHoras;
    private long totalMinutos;
    private String nomeColaborador;

    public static String buildHtmlContent(EspelhoPontoMensal espelhoPontoMensal) {
        StringBuilder htmlContent = new StringBuilder();

        htmlContent.append("<html><body>");

        htmlContent.append("<h1>Relatório Mensal de Ponto</h1>");

        htmlContent.append("<p>Colaborador(a): <strong>").append(espelhoPontoMensal.getNomeColaborador()).append("</strong></p>");

        htmlContent.append("<hr/>");

        List<EspelhoPontoDiario> espelhoPontoDiarios = espelhoPontoMensal.getEspelhoPontoDiarios();
        for (EspelhoPontoDiario espelhoPontoDiario : espelhoPontoDiarios) {
            htmlContent.append("<h2>Data: ")
                    .append(formatLocalDate(espelhoPontoDiario.getData(), DEFAULT_DATE_FORMAT))
                    .append("</h2>");

            List<Ponto> pontos = espelhoPontoDiario.getPontos();
            for (Ponto ponto : pontos) {
                htmlContent.append("<p><strong>Marcação:</strong> ")
                        .append(ponto.getEventoPronto())
                        .append(" - ")
                        .append(formatDateTime(ponto.getData(), DEFAULT_TIME_FORMAT))
                        .append("</p>");
            }

            htmlContent.append("<p>Horas Trabalhadas: ")
                    .append(espelhoPontoDiario.getTotalHoras());

            if(espelhoPontoDiario.getTotalMinutos() > 0) {
                htmlContent.append(":")
                        .append(espelhoPontoDiario.getTotalMinutos());
            }

            htmlContent.append("</p>");
        }

        htmlContent.append("<hr/>");

        htmlContent.append("<p>Total de Horas Trabalhadas: ")
                .append(espelhoPontoMensal.getTotalHoras());

        if(espelhoPontoMensal.getTotalMinutos() > 0) {
            htmlContent.append(":")
                .append(espelhoPontoMensal.getTotalMinutos());
        }

        htmlContent.append("</p>");

        // Fim do documento HTML
        htmlContent.append("</body></html>");

        return htmlContent.toString();
    }
}
