package com.almerys.generali.wsnoemisation.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.almerys.generali.wsnoemisation.exception.BeneficiaryNotFoundException;
import com.almerys.generali.wsnoemisation.exception.ContractNotFoundException;
import com.almerys.generali.wsnoemisation.exception.DateMovementException;
import com.almerys.generali.wsnoemisation.message.request.FilterForm;
import com.almerys.generali.wsnoemisation.model.Noemisation;
import com.almerys.generali.wsnoemisation.repository.NoemisationRepository;
import com.almerys.generali.wsnoemisation.service.NoemisationService;


@Service
public class NoemisationServiceImpl implements NoemisationService{
	
	@Autowired
	private NoemisationRepository noemisationRepository;
	
	@Override
	public List<Noemisation> getNoemisationHistorique(FilterForm filterForm) throws DateMovementException,BeneficiaryNotFoundException,ContractNotFoundException{
		return noemisationRepository.getNoemisationHistorique(filterForm);
	}
}
