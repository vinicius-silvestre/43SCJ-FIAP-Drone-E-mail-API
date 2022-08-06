package com.br.dronetrack.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.dronetrack.config.RabbitConfig;
import com.br.dronetrack.model.DroneDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("drone-track")
public class DroneController {

    @GetMapping
    public Object readMessageQueue() throws JsonMappingException, JsonProcessingException{
    	RabbitTemplate template = new RabbitTemplate(RabbitConfig.getConnection());
    	byte[] body = template.receive("drone-tracker-receptor").getBody();
    	Object drone = SerializationUtils.deserialize(body);
    	ObjectMapper mapper = new ObjectMapper();
    	DroneDTO d = mapper.convertValue(drone, DroneDTO.class);    	
    	System.out.println(d);
       return drone;
   }
}
