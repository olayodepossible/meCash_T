package com.possible.meCash.utils;


import com.possible.task.dto.req.EmailDto;
import com.possible.task.exceptiion.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceUtil {

    private final JavaMailSender mailSender;

    public void sendEmail(EmailDto emailDetails){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("noreply@gmail.com");
            mailMessage.setTo(String.join(",", emailDetails.getToAddress()));
            mailMessage.setText(emailDetails.getContent());
            mailMessage.setSubject(emailDetails.getSubject());

            mailSender.send(mailMessage);
            log.info("Message sent to: {}", emailDetails.getToAddress());

        } catch (MailException e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }


}
