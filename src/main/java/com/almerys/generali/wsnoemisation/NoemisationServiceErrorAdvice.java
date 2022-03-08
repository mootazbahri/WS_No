package com.almerys.generali.wsnoemisation;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.almerys.generali.wsnoemisation.exception.BeneficiaryNotFoundException;
import com.almerys.generali.wsnoemisation.exception.ContractNotFoundException;
import com.almerys.generali.wsnoemisation.exception.DateMovementException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class NoemisationServiceErrorAdvice {	 	 
	 @ResponseStatus(value = HttpStatus.BAD_REQUEST , reason = "Erreur lors de l'analyse de votre demande, le format de la date n'est pas respecté, le modèle valide est: 'yyyy-MM-dd' ")
	 @ExceptionHandler({HttpMessageNotReadableException.class,InvalidFormatException.class,MethodArgumentTypeMismatchException.class})
	 public void handlerBadReq(Exception e) {
	 	log.error("Erreur lors de l'analyse de votre demande : " + e);
	 }
	 
	 @ResponseStatus(value = HttpStatus.NOT_FOUND , reason = "Les informations obligatoires ne sont pas renseignées, Le munéro du client et le Numéro de contrat de l'assuré")
	 @ExceptionHandler({NoHandlerFoundException.class})
	 public void handler(NoHandlerFoundException e) {
	 	log.error("Les informations obligatoires ne sont pas renseignées : "+ e);
	 }
	 
	 @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "La date début mouvement ne doit pas être supérieure à la date de fin de mouvement et inversement")
	 @ExceptionHandler({DateMovementException.class})
	 public void handler(DateMovementException e) {
	 	log.error("Date Début Mouvement ne doit pas être supérieure à la date de fin : "+ e);
	 }
	 
	 @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Le bénéficiaire n’est pas connu dans notre SI")
	 @ExceptionHandler({BeneficiaryNotFoundException.class})
	 public void handler(BeneficiaryNotFoundException e) {
	 	log.error("Benef Exception : "+ e);
	 }
	 
	 @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Le contrat n’est pas connu dans notre SI")
	 @ExceptionHandler({ContractNotFoundException.class})
	 public void handler(ContractNotFoundException e) {
	 	log.error("Contrat Exception : "+ e);
	 }
}
