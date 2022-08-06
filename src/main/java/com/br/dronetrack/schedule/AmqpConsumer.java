package com.br.dronetrack.schedule;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.br.dronetrack.config.RabbitConfig;
import com.br.dronetrack.model.DroneDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableScheduling
public class AmqpConsumer {

	@Scheduled(fixedDelay = 60000)
	public void schedule() throws javax.mail.MessagingException {
		RabbitTemplate template = new RabbitTemplate(RabbitConfig.getConnection());
		try {
			byte[] body = template.receive("drone-tracker-alert").getBody();
			Object resp = SerializationUtils.deserialize(body);
			ObjectMapper mapper = new ObjectMapper();
	    	DroneDTO drone = mapper.convertValue(resp, DroneDTO.class);
	    	sendMail(drone);
			System.out.println(resp);
		}catch (NullPointerException ex){
            System.out.println("fila email vazia!");            
        }
	}
	
	public void sendMail(DroneDTO drone) throws javax.mail.MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getenv("E-MAIL"), System.getenv("SENHA"));
			}
		});
		/** Ativa Debug para sessão */
		session.setDebug(true);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(System.getenv("E-MAIL")));

			// Remetente

			Address[] toUser = InternetAddress
					.parse(System.getenv("E-MAIL-TO"));

			message.setRecipients(Message.RecipientType.TO, toUser);
			message.setSubject("Rastreio do Drone");// Assunto
			
			String msgBody = "Atenção! \n\n o drone " + drone.getId() + " está com a temperatura: " + drone.getTemperatura() + " e a umidade: " + drone.getUmidade();
			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
	        messageBodyPart.setText(msgBody);
			
			Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(messageBodyPart);
	        
	        messageBodyPart = new MimeBodyPart();
			multipart.addBodyPart(messageBodyPart);
	        
	        message.setContent(multipart);
	        
	        /** Método para enviar a mensagem criada */
			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
