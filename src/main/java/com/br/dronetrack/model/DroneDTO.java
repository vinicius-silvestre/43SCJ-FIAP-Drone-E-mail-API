package com.br.dronetrack.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DroneDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("id")
	private Long id;
	@JsonProperty("latitude")
	private String latitude;
	@JsonProperty("longitude")
	private String longitude;
	@JsonProperty("temperatura")
	private Integer temperatura;
	@JsonProperty("umidade")
	private Integer umidade;
	@JsonProperty("rastreamento")
	private Boolean rastreamento;
}
