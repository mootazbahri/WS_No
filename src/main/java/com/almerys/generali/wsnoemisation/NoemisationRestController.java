package com.almerys.generali.wsnoemisation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.almerys.generali.wsnoemisation.exception.BeneficiaryNotFoundException;
import com.almerys.generali.wsnoemisation.exception.ContractNotFoundException;
import com.almerys.generali.wsnoemisation.exception.DateMovementException;
import com.almerys.generali.wsnoemisation.message.request.FilterForm;
import com.almerys.generali.wsnoemisation.message.response.ResponseMessage;
import com.almerys.generali.wsnoemisation.model.Noemisation;
import com.almerys.generali.wsnoemisation.service.NoemisationService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;



@RestController
@RequestMapping(value = "/Noemisation", produces = { MediaType.APPLICATION_JSON_VALUE })
public class NoemisationRestController {
	
	private static final String DATE_TIME_EXAMPLE = "2019-12-31"; 
	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd";
	
	@Autowired
	private NoemisationService noemisationService;
	
	/* 
	 * +--------------------------------------------------------------------+
	 * | Input : FilterForm Object
	 * | Traitement : validate the information provided in the FilterForm 
	 * |              object, then if every thing is okay we can fetch the 
	 * |              noemisation.  
	 * | Output : List of Noemisations                   
	 * +--------------------------------------------------------------------+ 
	 */
	
	@RequestMapping(value = "/getNoemisationHistorique/{client}/{contrat}", method = RequestMethod.GET)
	@ApiOperation("Chercher l'historique de nomination qui s'applique aux critères spécifiés dans la demande GET")
	public ResponseEntity<ResponseMessage> getAllNoemisationHistorique(
			@ApiParam(value = "Numéro du client", required = true, example = "02452274") @PathVariable("client") String client,
			@ApiParam(value = "Numéro du contrat", required = true, example = "AC4173915") @PathVariable("contrat") String contrat,
			@ApiParam(value = "Numéro du beneficiaire", required = false) @RequestParam(value = "beneficiaire", required = false) String beneficiaire,
			@ApiParam(value = "Date debut mouvement. Example : "+ DATE_TIME_EXAMPLE, example = DATE_TIME_EXAMPLE, required = false) 
			@RequestParam(value = "dateDebutMouvement", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern= "yyyy-MM-dd") LocalDate dateDebutMouvement,
			@ApiParam(value = "Date fin mouvement. Example : "+ DATE_TIME_EXAMPLE, example = DATE_TIME_EXAMPLE, required = false) 
			@RequestParam(value = "dateFinMouvement", required = false) 
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern= "yyyy-MM-dd") LocalDate dateFinMouvement) throws DateMovementException,BeneficiaryNotFoundException,ContractNotFoundException, UnsupportedEncodingException{
		
		FilterForm filterForm = new FilterForm();
		filterForm.setClient(URLDecoder.decode(client, "UTF-8"));
		filterForm.setContrat(URLDecoder.decode(contrat, "UTF-8"));
		if(beneficiaire == null || beneficiaire.isEmpty()) {
			filterForm.setBeneficiaire(beneficiaire);
		} else {
			filterForm.setBeneficiaire(URLDecoder.decode(beneficiaire, "UTF-8"));
		}
		
		filterForm.setDateDebutMouvement(dateDebutMouvement);
		filterForm.setDateFinMouvement(dateFinMouvement);
		
		List<Noemisation> noemisation = noemisationService.getNoemisationHistorique(filterForm);
		if(noemisation.isEmpty())
			return new ResponseEntity<>(new ResponseMessage("Aucune donnée n'a été trouvé par rapport aux critères d'entrée",null),HttpStatus.OK);
		return new ResponseEntity<>(new ResponseMessage("",noemisation),HttpStatus.OK);
	}
	
}
