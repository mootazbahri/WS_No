package com.almerys.generali.wsnoemisation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.almerys.generali.wsnoemisation.message.request.FilterForm;
import com.almerys.generali.wsnoemisation.message.response.ResponseMessage;
import com.almerys.generali.wsnoemisation.model.Noemisation;
import com.almerys.generali.wsnoemisation.service.NoemisationService;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class NoemisationRestControllerTest {
	MockMvc mockMvc;

	@InjectMocks
	NoemisationRestController noemisationRestController;

	@Mock
	private NoemisationService noemisationService;

	FilterForm filterForm;

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.standaloneSetup(noemisationRestController).build();

		filterForm = new FilterForm();
		filterForm.setClient("02452274");
		filterForm.setContrat("AC4173915");
	}

	@Test
	public void testNoemisationRestController() throws Exception {
		when(noemisationService.getNoemisationHistorique(filterForm)).thenReturn(getListeNoemisation());
		String listeNoemisation = mockMvc
				.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200)).andReturn().getResponse().getContentAsString();
		// Convert the result of the test to Java object
		try {
			Gson gson = new Gson();
			ResponseMessage response = gson.fromJson(listeNoemisation, ResponseMessage.class);
			assertThat(response.getNoemisation(), hasSize(3));
		} catch (Exception err) {
			log.info(
					"Error converting the result of the test testNoemisationRestController to ResponseMessage Object : "
							+ err);
		}
	}

	@Test
	public void testNoemisationRestControllerQuiRetourneListeVide() throws Exception {
		List<Noemisation> listeNoemisationMock = this.getListeNoemisation();
		listeNoemisationMock.clear();
		when(noemisationService.getNoemisationHistorique(filterForm)).thenReturn(listeNoemisationMock);
		String listeNoemisation = mockMvc
				.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is(200)).andReturn().getResponse().getContentAsString();
		// Convert the result of the test to Java object
		try {
			Gson gson = new Gson();
			ResponseMessage response = gson.fromJson(listeNoemisation, ResponseMessage.class);
			assertTrue(
					response.getMessage().contains("Aucune donnée n'a été trouvé par rapport aux critères d'entrée"));
		} catch (Exception err) {
			log.info(
					"Error converting the result of the test testNoemisationRestController to ResponseMessage Object : "
							+ err);
		}
	}

	public List<Noemisation> getListeNoemisation() {
		List<Noemisation> noemisationList = new ArrayList<Noemisation>();
		Noemisation noemisation1 = new Noemisation("AVT_1001434461", "AC4173915", "02452274", "EL HADDAOUI RAYAN ",
				"30/07/2002", 1, "841", 0, "1762099350085", "01/01/2016", "29/07/2022", "07/11/2017", "408",
				"ANNULATION", "", "");
		Noemisation noemisation2 = new Noemisation("AVT_1001434457", "AC4173915", "02452274", "EL HADDAOUI SALIHA",
				"03/05/1977", 1, "841", 0, "2770584138012", "01/01/2016", "31/12/2024", "07/11/2017", "408",
				"ANNULATION", "", "");
		Noemisation noemisation3 = new Noemisation("AVT_1001434460", "AC4173915", "02452274", "EL HADDAOUI NOAM  ",
				"17/08/2012", 1, "841", 0, "2770584138012", "01/01/2016", "31/12/2024", "07/11/2017", "408",
				"ANNULATION", "", "");

		noemisationList.add(noemisation1);
		noemisationList.add(noemisation2);
		noemisationList.add(noemisation3);
		return noemisationList;
	}
}
