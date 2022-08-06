package com.br.dronetrack.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

public class RabbitConfig {
    private static CachingConnectionFactory connectionFactory;

    public static CachingConnectionFactory getConnection(){

        if(connectionFactory == null){
            connectionFactory = new CachingConnectionFactory("cougar-01.rmq.cloudamqp.com");//TODO add hostname
            connectionFactory.setUsername("efijgrte");//TODO add username
            connectionFactory.setPassword("EYPhJGGPXCkbLfx-QYvgZ4rS5aK6a-RU");//TODO add password
            connectionFactory.setVirtualHost("efijgrte");//TODO add virtualhost

            //Recommended settings
            connectionFactory.setRequestedHeartBeat(30);
            connectionFactory.setConnectionTimeout(30000);
        }

        return connectionFactory;
    }
}
