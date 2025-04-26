package br.com.moneyTracker.service;

import br.com.moneyTracker.exceptions.SendMailException;
import br.com.moneyTracker.interfaces.EmailInterface;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailServiceSendgrid implements EmailInterface {

    @Value("${sendgrid.apiKey}")
    private String SENDGRID_API_KEY;

    @Value("${moneytracker.email.sender}")
    private String sender;

    @Override
    public void sendMail(Message message) {
        Email from = new Email(sender);
        Email to = new Email(message.getTo());
        String subject = message.getSubject();
        Content content = new Content("text/html", message.getBody());

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("SendGrid response code: " + response.getStatusCode());
            System.out.println("SendGrid response body: " + response.getBody());
            System.out.println("SendGrid response headers: " + response.getHeaders());

            if(response.getStatusCode() == 400) {
                throw new SendMailException("Failed to send email. Status code: " + response.getStatusCode() + ", body: " + response.getBody());
            }

        } catch (SendMailException | IOException ex) {
            throw new SendMailException("Failed to send email via SendGrid: " + ex.getMessage(), ex);
        }
    }
}
