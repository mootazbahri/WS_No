package com.almerys.generali.wsnoemisation.exception;

public class DateMovementException extends Exception{

	private static final long serialVersionUID = 1L;
	public DateMovementException() {
		super("La date début mouvement ne doit pas être supérieure à la date de fin de mouvement et inversement");
	}
}
