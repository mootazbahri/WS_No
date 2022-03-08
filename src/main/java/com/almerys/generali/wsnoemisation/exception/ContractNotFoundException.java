package com.almerys.generali.wsnoemisation.exception;

public class ContractNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;
	public ContractNotFoundException() {
		super("Le contrat n’est pas connu dans notre SI");
	}
}
