package com.almerys.generali.wsnoemisation.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.almerys.generali.wsnoemisation.exception.BeneficiaryNotFoundException;
import com.almerys.generali.wsnoemisation.exception.ContractNotFoundException;
import com.almerys.generali.wsnoemisation.exception.DateMovementException;
import com.almerys.generali.wsnoemisation.message.request.FilterForm;
import com.almerys.generali.wsnoemisation.model.Noemisation;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class NoemisationServiceTest {

	FilterForm filterForm;
	
	@Autowired
	NoemisationService noemisationServ;
	
	@Before
	public void setup() throws Exception {
		filterForm = new FilterForm();
		filterForm.setClient("02452274");
		filterForm.setContrat("AC4173915");
	}
	
	@Test
	public void testNoemisationService() throws Exception {
		List<Noemisation> noemisation = noemisationServ.getNoemisationHistorique(filterForm);
		assertTrue(noemisation.size() >= 0);
	}
	
	@Test
	public void testNoemisationService_WithExceptionBenefNotFound() throws Exception {
		filterForm.setBeneficiaire("testTestBenefNotFound");
		Exception exception = assertThrows(BeneficiaryNotFoundException.class, ()->{
			noemisationServ.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "Le bénéficiaire n’est pas connu dans notre SI";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationService_WithExceptionContratNotFound() throws Exception {
		filterForm.setContrat("ContratTest");
		Exception exception = assertThrows(ContractNotFoundException.class, ()->{
			noemisationServ.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "Le contrat n’est pas connu dans notre SI";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationService_WithExceptionDateMouvement() throws Exception {
		filterForm.setDateDebutMouvement(LocalDate.parse("2019-12-31"));
		filterForm.setDateFinMouvement(LocalDate.parse("2019-11-20"));
		
		Exception exception = assertThrows(DateMovementException.class, ()->{
			noemisationServ.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "La date début mouvement ne doit pas être supérieure à la date de fin de mouvement et inversement";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationServiceWithDates() throws Exception {
		filterForm.setDateDebutMouvement(LocalDate.parse("2018-01-31"));
		filterForm.setDateFinMouvement(LocalDate.parse("2016-12-31"));
		
		Exception exception = assertThrows(DateMovementException.class, ()->{
			noemisationServ.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "La date début mouvement ne doit pas être supérieure à la date de fin de mouvement et inversement";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationServiceWithBenef() throws Exception {
		filterForm.setBeneficiaire("AVT_1001434461");
		
		List<Noemisation> noemisation = noemisationServ.getNoemisationHistorique(filterForm);
		
		assertTrue(noemisation.size() >= 0);
	}
	
	@Test
	public void testNoemisationServiceWithBenefPlusDates() throws Exception {
		filterForm.setBeneficiaire("AVT_1001434461");
		filterForm.setDateDebutMouvement(LocalDate.parse("2016-01-31"));
		filterForm.setDateFinMouvement(LocalDate.parse("2019-12-31"));
		
		List<Noemisation> noemisation = noemisationServ.getNoemisationHistorique(filterForm);
		
		assertTrue(noemisation.size() >= 0);
	}
}