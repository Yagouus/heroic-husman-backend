package logEditor;



import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import org.springframework.web.bind.annotation.*;



@RestController
public class FileController {

    @Autowired
    private JavaMailSender sender;

    public FileController() {

    }

    //Send email
    @CrossOrigin
    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public void sendEmail(String name, String sender, String tlf, String msg) throws Exception {

        System.out.println(name + sender + tlf + msg);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("sample@dolszewski.com");
            messageHelper.setTo("yagofontenla95@gmail.com");
            messageHelper.setSubject("FORMULARIO CONTACTO WEB");
            messageHelper.setText("Nombre: " + name
            + "\nEmail: " + sender
            + "\nMovil: " + tlf
            + "\nMensaje: " + msg);


        };
        try {
            this.sender.send(messagePreparator);
        } catch (MailException e) {
            // runtime exception; compiler will not force you to handle it
        }


    }

}

