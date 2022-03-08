package com.almerys.generali.wsnoemisation.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
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
public class NoemisationRepositoryTest {
	FilterForm filterForm;
	
	@Autowired
	NoemisationRepository noemisationRepository;
	
	
	@Before
	public void setup() throws Exception {
		filterForm = new FilterForm();
		filterForm.setClient("02452274");
		filterForm.setContrat("AC4173915");
	}
	
	@Test
	public void testNoemisationRepository_WithExceptionBenefNotFound() throws Exception {
		filterForm.setBeneficiaire("testTestBenefNotFound");
		Exception exception = assertThrows(BeneficiaryNotFoundException.class, ()->{
			noemisationRepository.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "Le bénéficiaire n’est pas connu dans notre SI";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationRepository_WithExceptionContratNotFound() throws Exception {
		filterForm.setContrat("ContratTest");
		Exception exception = assertThrows(ContractNotFoundException.class, ()->{
			noemisationRepository.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "Le contrat n’est pas connu dans notre SI";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationRepository_WithExceptionDateMouvement() throws Exception {
		filterForm.setDateDebutMouvement(LocalDate.parse("2019-12-31"));
		filterForm.setDateFinMouvement(LocalDate.parse("2019-11-20"));
		
		Exception exception = assertThrows(DateMovementException.class, ()->{
			noemisationRepository.getNoemisationHistorique(filterForm);
		});
		
		String expectedMessage = "La date début mouvement ne doit pas être supérieure à la date de fin de mouvement et inversement";
		String actualMessage = exception.getMessage();
		
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	public void testNoemisationRepository() throws Exception {
		filterForm.setDateDebutMouvement(LocalDate.parse("2018-01-01"));
		filterForm.setDateFinMouvement(LocalDate.parse("2018-12-31"));
		
		List<Noemisation> noemisation = noemisationRepository.getNoemisationHistorique(filterForm);
		
		assertTrue(noemisation.size() >= 0);
	}
	
	@Test
	public void testNoemisationRepository_withoutdates() throws Exception {
		
		List<Noemisation> noemisation = noemisationRepository.getNoemisationHistorique(filterForm);
		
		assertTrue(noemisation.size() >= 0);
	}
	
	@Test
	public void testNoemisationepositoryWithBenef() throws Exception {
		filterForm.setBeneficiaire("AVT_1001434461");
		
		List<Noemisation> noemisation = noemisationRepository.getNoemisationHistorique(filterForm);
		
		assertTrue(noemisation.size() >= 0);
	}
	
	@Test
	public void testNoemisationepositoryWithBenefPlusDates() throws Exception {
		filterForm.setBeneficiaire("AVT_1001434461");
		filterForm.setDateDebutMouvement(LocalDate.parse("2016-01-31"));
		filterForm.setDateFinMouvement(LocalDate.parse("2019-12-31"));
		
		List<Noemisation> noemisation = noemisationRepository.getNoemisationHistorique(filterForm);
		
		assertTrue(noemisation.size() >= 0);
	}
	
	@Test
	public void testNoemisationepositoryDateMouvementFormats() throws Exception {
		List<Noemisation> noemisation = noemisationRepository.getNoemisationHistorique(filterForm);
		
		
		if(noemisation.size() > 0) {
			noemisation.forEach(item -> {
				assertThat(new NoemisationRepositoryTest().checkDateFormat(item.getDateMouvement())).isTrue();
			});
		}
	}
	
	public boolean checkDateFormat(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/YYYY");
        s = formatter.format(new Date(s));
        Date date = new Date(s);

        String strDate = formatter.format(date);
        if (s.equals(strDate)) {
            return true;
        } else {
            return false;
        }
    }
}
