package br.com.fiap.soat1.t32.consumerreport.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import br.com.fiap.soat1.t32.consumerreport.models.user.presenters.EspelhoPontoMensal;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnvioEmailRelatorioPonto {

    @Autowired
    private JavaMailSender emailSender;


    public void sendReport(EspelhoPontoMensal espelhoPontoMensal, @NonNull String destinatario) throws MessagingException, jakarta.mail.MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(destinatario);
        helper.setSubject("Relatório Mensal de Ponto");
        helper.setText(EspelhoPontoMensal.buildHtmlContent(espelhoPontoMensal), true);

        emailSender.send(message);
    }

}
