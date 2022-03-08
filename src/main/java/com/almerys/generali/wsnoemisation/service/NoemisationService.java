package com.almerys.generali.wsnoemisation.service;

import java.util.List;

import com.almerys.generali.wsnoemisation.exception.BeneficiaryNotFoundException;
import com.almerys.generali.wsnoemisation.exception.ContractNotFoundException;
import com.almerys.generali.wsnoemisation.exception.DateMovementException;
import com.almerys.generali.wsnoemisation.message.request.FilterForm;
import com.almerys.generali.wsnoemisation.model.Noemisation;


public interface NoemisationService {

	public List<Noemisation> getNoemisationHistorique(FilterForm filterForm) throws DateMovementException,BeneficiaryNotFoundException,ContractNotFoundException;	
}
