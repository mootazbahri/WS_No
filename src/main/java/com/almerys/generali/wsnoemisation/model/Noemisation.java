package com.almerys.generali.wsnoemisation.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Noemisation implements Serializable{

	private static final long serialVersionUID = 1L;

	private String referenceInterneClient; 
	
	private String referenceContrat; 
	
	private String numeroTeletransmission; 
	
	private String nomPrenom; 
	
	private String dateNaiss; 
	
	private long codeRegime; 
	
	private String codeCaisse; 
	
	private long codeCentre; 
	
    private String nni;
    
    private String debutNoemisation; 
    
    private String finnoemisation; 
    
    private String dateMouvement; 
    
    private String typeMouvement; 
    
    private String codeMouvement; 
    
    private String codeRejet; 
    
    private String libelleRejet; 
}
