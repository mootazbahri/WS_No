package com.almerys.generali.wsnoemisation;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.almerys.generali.wsnoemisation.message.response.ResponseMessage;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class WsGeneraliNoemisationApplicationTests {
	@Inject
	private WebApplicationContext webApplicationContext;

	MockMvc mockMvc;

	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void applicationContextTest() {
		WsGeneraliNoemisationApplication.main(new String[] {});
	}
	
	@Test
	public void testRouteWithObligParams() throws Exception {
		String listeNoemisation = mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915")
			.accept(MediaType.APPLICATION_JSON)
			.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().is(200))
			.andReturn().getResponse().getContentAsString();
		//Convert the result of the test to Java object
		try {
		     Gson gson = new Gson();
		     ResponseMessage response = gson.fromJson(listeNoemisation, ResponseMessage.class);
		     if(response.getNoemisation() != null )
		    	 assertTrue(response.getNoemisation().size() > 0);
		     else 
		    	 assertTrue(response.getMessage().contains("Aucune donnée n'a été trouvé par rapport aux critères d'entrée"));
		}catch (Exception err){
			log.info("Error converting the result of the test testNoemisationRestController to ResponseMessage Object : " + err);
		}
	}
	
	@Test
	public void testRouteWithDates() throws Exception {
		String listeNoemisation = mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915?dateDebutMouvement=2016-12-31&dateFinMouvement=2019-12-31")
			.accept(MediaType.APPLICATION_JSON)
			.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().is(200))
			.andReturn().getResponse().getContentAsString();
		//Convert the result of the test to Java object
		try {
		     Gson gson = new Gson();
		     ResponseMessage response = gson.fromJson(listeNoemisation, ResponseMessage.class);
		     if(response.getNoemisation() != null )
		    	 assertTrue(response.getNoemisation().size() > 0);
		     else 
		    	 assertTrue(response.getMessage().contains("Aucune donnée n'a été trouvé par rapport aux critères d'entrée"));
		}catch (Exception err){
			log.info("Error converting the result of the test testNoemisationRestController to ResponseMessage Object : " + err);
		}
	}
	
	@Test
	public void testRouteWithDates_ExpectError() throws Exception {
		String response = mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915?dateDebutMouvement=2019-12-31&dateFinMouvement=2018-12-31")
			.accept(MediaType.APPLICATION_JSON)
			.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().is(500))
			.andReturn().getResponse().getErrorMessage();
		assertTrue(response.contains("La date début mouvement ne doit pas être supérieure à la date de fin de mouvement et inversement"));
	}
	@Test
	public void testRouteWithBenef_ExpectError() throws Exception {
		String response = mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915?beneficiaire=testbenefnotfound")
			.accept(MediaType.APPLICATION_JSON)
			.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().is(500))
			.andReturn().getResponse().getErrorMessage();
		assertTrue(response.contains("Le bénéficiaire n’est pas connu dans notre SI"));
	}
	
	@Test
	public void testRouteWithContrat_ExpectError() throws Exception {
		String response = mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915xx")
			.accept(MediaType.APPLICATION_JSON)
			.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().is(500))
			.andReturn().getResponse().getErrorMessage();
		assertTrue(response.contains("Le contrat n’est pas connu dans notre SI"));
	}
	
	@Test
	public void testRouteWithObligParamsMissing_ExpectError() throws Exception {
		mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274")
					.accept(MediaType.APPLICATION_JSON)
					.header("Access-Control-Request-Method", "GET"))
					.andExpect(status().is(404));
	}
	
	@Test
	public void testRouteWithWrongDateFormat_ExpectError() throws Exception {
		String response = mockMvc.perform(get("/Noemisation/getNoemisationHistorique/02452274/AC4173915?dateDebutMouvement=2019-15-31&dateFinMouvement=2018-15-31")
			.accept(MediaType.APPLICATION_JSON)
			.header("Access-Control-Request-Method", "GET"))
			.andExpect(status().is(400))
			.andReturn().getResponse().getErrorMessage();
		assertTrue(response.contains("Erreur lors de l'analyse de votre demande, le format de la date n'est pas respecté, le modèle valide est: 'yyyy-MM-dd'"));
	}
}
