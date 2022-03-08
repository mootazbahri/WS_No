package com.almerys.generali.wsnoemisation.exception;

public class BeneficiaryNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;
	public BeneficiaryNotFoundException() {
		super("Le bénéficiaire n’est pas connu dans notre SI");
	}
}
