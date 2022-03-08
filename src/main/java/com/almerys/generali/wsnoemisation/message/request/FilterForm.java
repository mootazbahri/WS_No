package com.almerys.generali.wsnoemisation.message.request;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterForm {
	
	@NotEmpty(message = "Numéro du client est obligatoire")
	@ApiModelProperty(value = "Numéro du client", required = true, example = "02452274")
	private String client;
	
	@NotEmpty(message = "Numéro de contrat de l’assuré est obligatoire")
	@ApiModelProperty(value = "Numéro du contrat", required = true, example = "AC4173915")
	private String contrat;
	
	@ApiModelProperty(value = "Numéro du beneficiaire")
	private String beneficiaire;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	@ApiModelProperty(value = "Date debut mouvement")
	private LocalDate dateDebutMouvement;
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@JsonFormat(pattern="dd/MM/yyyy")
	@ApiModelProperty(value = "Date fin mouvement")
	private LocalDate dateFinMouvement;
}