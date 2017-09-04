package logEditor;



import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import org.springframework.web.bind.annotation.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@RestController
public class FileController {

    @Autowired
    private JavaMailSender sender;

    public FileController() {

    }

    //Send email
    @CrossOrigin
    @RequestMapping(value = "/email")
    public void sendEmail(String name, String sender, String tlf, String msg) throws Exception {

        final String username = "esbaostienda@gmail.com";
        final String password = "aos12345";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new javax.mail.PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender, name));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("info@crossfitberkana.com"));
            message.setSubject("CONSULTA WEB BERKANA");
            message.setText("Nombre: " + name
                    + "\nEmail: " + sender
                    + "\nMovil: " + tlf
                    + "\nMensaje: " + msg);

            Transport.send(message);


        }catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}

