package com.almerys.generali.wsnoemisation.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.almerys.generali.wsnoemisation.exception.BeneficiaryNotFoundException;
import com.almerys.generali.wsnoemisation.exception.ContractNotFoundException;
import com.almerys.generali.wsnoemisation.exception.DateMovementException;
import com.almerys.generali.wsnoemisation.message.request.FilterForm;
import com.almerys.generali.wsnoemisation.model.Noemisation;

@Component
public class NoemisationRepository {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public List<Noemisation> getNoemisationHistorique(FilterForm filterForm)
			throws DateMovementException, BeneficiaryNotFoundException, ContractNotFoundException {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		String conditionBenef = "";
		String conditionDateMouvement = "";
		String conditionBenefUnion = "";
		String conditionDateMouvementUnion = "";
		String formingDateMouvement = "   case when (f.FEDTRT > 18000000 and substr(f.FEDTRT, 7, 2) between 1 and 31 and substr(f.FEDTRT, 5, 2) between 1 and 12) then substr(f.FEDTRT, 1, 4)||'/'||substr(f.FEDTRT, 5, 2)||'/'||substr(f.FEDTRT, 7, 2) else '0001/01/01' end as date_mouvement, ";
		String formingDateMouvementUnion = " case when (hg.FEDTRT > 18000000 and substr(hg.FEDTRT, 7, 2) between 1 and 31 and substr(hg.FEDTRT, 5, 2) between 1 and 12) then substr(hg.FEDTRT, 1, 4)||'/'||substr(hg.FEDTRT, 5, 2)||'/'||substr(hg.FEDTRT, 7, 2) else '0001/01/01' end as date_mouvement, ";

		String orderByDateMouement = " order by date_mouvement desc";
		parameters.addValue("CLIENT", filterForm.getClient());
		parameters.addValue("CONTRAT", filterForm.getContrat());
		this.verifierFilterForm(filterForm);
		if (filterForm.getBeneficiaire() != null) {
			parameters.addValue("BENEF", filterForm.getBeneficiaire());
			conditionBenef = "AND COALESCE(tvnba, ctrioc) = :BENEF ";
			conditionBenefUnion = "AND COALESCE(tvnba, FBRIOC) = :BENEF ";
		}
		if (filterForm.getDateDebutMouvement() != null && filterForm.getDateFinMouvement() != null) {
			// Add parameters
			parameters.addValue("DATE_DEBUT_MOUVEMENT",
					filterForm.getDateDebutMouvement().getYear() + ""
							+ String.format("%02d", filterForm.getDateDebutMouvement().getMonthValue()) + ""
							+ String.format("%02d", filterForm.getDateDebutMouvement().getDayOfMonth()));
			parameters.addValue("DATE_FIN_MOUVEMENT",
					filterForm.getDateFinMouvement().getYear() + ""
							+ String.format("%02d", filterForm.getDateFinMouvement().getMonthValue()) + ""
							+ String.format("%02d", filterForm.getDateFinMouvement().getDayOfMonth()));
			conditionDateMouvement = "AND f.FEDTRT between :DATE_DEBUT_MOUVEMENT and :DATE_FIN_MOUVEMENT ";
			conditionDateMouvementUnion = "AND hg.FEDTRT between :DATE_DEBUT_MOUVEMENT and :DATE_FIN_MOUVEMENT ";
		} else {
			if (filterForm.getDateDebutMouvement() != null) {
				parameters.addValue("DATE_DEBUT_MOUVEMENT",
						filterForm.getDateDebutMouvement().getYear() + ""
								+ String.format("%02d", filterForm.getDateDebutMouvement().getMonthValue()) + ""
								+ String.format("%02d", filterForm.getDateDebutMouvement().getDayOfMonth()));
				conditionDateMouvement = "AND f.FEDTRT > :DATE_DEBUT_MOUVEMENT ";
				conditionDateMouvementUnion = "AND hg.FEDTRT > :DATE_DEBUT_MOUVEMENT ";
			} else {
				if (filterForm.getDateFinMouvement() != null) {
					parameters.addValue("DATE_FIN_MOUVEMENT",
							filterForm.getDateFinMouvement().getYear() + ""
									+ String.format("%02d", filterForm.getDateFinMouvement().getMonthValue()) + ""
									+ String.format("%02d", filterForm.getDateFinMouvement().getDayOfMonth()));
					conditionDateMouvement = "AND f.FEDTRT < :DATE_FIN_MOUVEMENT ";
					conditionDateMouvementUnion = "AND hg.FEDTRT < :DATE_FIN_MOUVEMENT ";
				} else {
					// if both of date begin and end movement is not specified we need to get the
					// last 10 mouvements
					orderByDateMouement = " order by date_mouvement desc" + " LIMIT 10";
				}
			}
		}

		List<Noemisation> listeNoemisation = jdbcTemplate.query(
				this.getRequetteExtractionEtatNoemisation(conditionBenef, conditionDateMouvement, formingDateMouvement,
						orderByDateMouement, formingDateMouvementUnion, conditionBenefUnion,
						conditionDateMouvementUnion),
				parameters,
				(rs, rowNum) -> new Noemisation(rs.getString("REFERENCE_INTERNE_CLIENT"),
						rs.getString("REFERENCE_CONTRAT"), rs.getString("NUMERO_TELETRANSMISSION"),
						rs.getString("NOM_PRENOM"), rs.getString("DATE_NAISS"), rs.getLong("CODE_REGIME"),
						rs.getString("CODE_CAISSE"), rs.getLong("CODE_CENTRE"), rs.getString("NNI"),
						rs.getString("DEBUT_NOEMISATION"), rs.getString("FIN_NOEMISATION"),
						rs.getString("DATE_MOUVEMENT"), rs.getString("TYPE_MOUVEMENT"), rs.getString("CODE_MOUVEMENT"),
						rs.getString("CODE_REJET"), rs.getString("LIBELLE_REJET")));
		this.changeDateFormat(filterForm, listeNoemisation);
		return listeNoemisation;
	}

	public void changeDateFormat(FilterForm filterForm, List<Noemisation> listeNoemisation) {
		// Change the format of the date mouvement to DD/MM/YYYY, when date begin and
		// end movement is not specified
		listeNoemisation.forEach(item -> {
			String[] splitOnDateMouvement = item.getDateMouvement().split("/");
			item.setDateMouvement(
					splitOnDateMouvement[2] + "/" + splitOnDateMouvement[1] + "/" + splitOnDateMouvement[0]);
		});
	}

	public void verifierFilterForm(FilterForm filterForm)
			throws DateMovementException, BeneficiaryNotFoundException, ContractNotFoundException {
		// Verifier is contrat existe dans notre SI
		if (!this.isContractExist(filterForm.getContrat())) {
			throw new ContractNotFoundException();
		}
		// verifier est ce que le beneficiare existe ou pas dans notre SI
		if (filterForm.getBeneficiaire() != null && (!this.isBeneficiaryExist(filterForm.getBeneficiaire()))) {
			throw new BeneficiaryNotFoundException();
		}
		if (filterForm.getDateDebutMouvement() != null && filterForm.getDateFinMouvement() != null
				&& filterForm.getDateDebutMouvement().isAfter(filterForm.getDateFinMouvement())) {
			// Verifier le date fin mouvement est aprés le date debut mouvement
			throw new DateMovementException();
		}
	}

	public boolean isBeneficiaryExist(String numBeneficiary) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("BENEF", numBeneficiary);
		// Verifier dans le table TABINDP COLUMN_NAME tvnba
		String sql = "SELECT count(tvnba) AS NBR_BENEF FROM BENEF_FIC.TABINDP WHERE tvnba = :BENEF";
		int nbrBenef = jdbcTemplate.queryForObject(sql, parameters, Integer.class);
		if (nbrBenef == 0) {
			// Verifier dans le table CPLASSP COLUMN_NAME ctrioc
			sql = "SELECT count(ctrioc) AS NBR_BENEF FROM BENALMFIC.CPLASSP WHERE ctrioc = :BENEF";
			nbrBenef = jdbcTemplate.queryForObject(sql, parameters, Integer.class);
		}
		return nbrBenef > 0;
	}

	public boolean isContractExist(String numContrat) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("CONTRAT", numContrat);

		String sql = "SELECT count(C.CPNCON) AS NBR_CONTRAT FROM BENALMFIC.COUVERP C WHERE C.CPNCON = :CONTRAT";
		int nbrContrat = jdbcTemplate.queryForObject(sql, parameters, Integer.class);

		return nbrContrat > 0;
	}

	public String getRequetteExtractionEtatNoemisation(String conditionBenef, String conditionDateMouvement,
			String formingDateMouvement, String orderByDateMouement, String formingDateMouvementUnion,
			String conditionBenefUnion, String conditionDateMouvementUnion) {
		// forming the sql request
		String sql = "With tabindp_tmp as (select a.*, digits(a.tvrind) as tvrind_alph from benef_fic.tabindp a where a.tvncli = :CLIENT) "
				+ "select distinct " + " COALESCE(tvnba, ctrioc) as REFERENCE_INTERNE_CLIENT, "
				+ " c.cpncon as REFERENCE_CONTRAT,  " + " f.fenmut as NUMERO_TELETRANSMISSION,  "
				+ " TRANSLATE(trim(FENBEN), 'AAAACEEEEIIIINOOOUUUUaaaceeeeiiooouu', 'ÀÁÂÆÇÈÉÊËÌÍÎÏÑÒÓÔÙÚÛÜàáâçéèëêïîòóôùü?:-/\\.,_''''()')||' '||TRANSLATE(trim(FEPBEN), 'AAAACEEEEIIIINOOOUUUUaaaceeeeiiooouu', 'ÀÁÂÆÇÈÉÊËÌÍÎÏÑÒÓÔÙÚÛÜàáâçéèëêïîòóôùü?:-/\\.,_''''()') as NOM_PRENOM, "
				+ "case when (f.fednai > 18000000 and substr(f.fednai, 7, 2) between 1 and 31 and substr(f.fednai, 5, 2) between 1 and 12) then substr(f.fednai, 7, 2)||'/'||substr(f.fednai, 5, 2)||'/'||substr(f.fednai, 1, 4) else '01/01/0001' end as DATE_NAISS,  "
				+ " f.fegreg as Code_Regime,  " + " f.FECCPA as Code_Caisse,  " + " f.fecges as Code_Centre,  "
				+ " f.fenins as NNI,  "
				+ " case when (f.FEDDEB > 18000000 and substr(f.FEDDEB, 7, 2) between 1 and 31 and substr(f.FEDDEB, 5, 2) between 1 and 12) then substr(f.FEDDEB, 7, 2)||'/'||substr(f.FEDDEB, 5, 2)||'/'||substr(f.FEDDEB, 1, 4) else '01/01/0001' end as DEBUT_NOEMISATION,  "
				+ " case when (f.FEDFIN > 18000000 and substr(f.FEDFIN, 7, 2) between 1 and 31 and substr(f.FEDFIN, 5, 2) between 1 and 12) then substr(f.FEDFIN, 7, 2)||'/'||substr(f.FEDFIN, 5, 2)||'/'||substr(f.FEDFIN, 1, 4) else '01/01/0001' end as FIN_NOEMISATION, "
				+ formingDateMouvement + " f.fenorm as TYPE_MOUVEMENT,  " + " case when f.fenorm = '408' then   "
				+ "                                  case when f.fecmou = 'C' then 'CREATION'  "
				+ "                                  else  "
				+ "                                      case when f.fecmou = 'M' then 'MODIFICATION'  "
				+ "                                      else  "
				+ "                                          case when f.fecmou = 'A' then 'ANNULATION'  "
				+ "                                          else '?'  "
				+ "                                          END  " + "                                      END  "
				+ "                                   END  " + "                             else  "
				+ "                                  case when right(trim(l.fscdrj), 1) = 'C' then 'CERTIFICATION'  "
				+ "                                  else "
				+ "                                      case when right(trim(l.fscdrj), 1) = 'D' then 'DEMANDE DE RESTITUTION DU FICHIER ADHERENTS'  "
				+ "                                      else "
				+ "                                          case when right(trim(l.fscdrj), 1) = 'I' then 'INTERROGATION'  "
				+ "                                          else "
				+ "                                              case when right(trim(l.fscdrj), 1) = 'R' then 'REJET'  "
				+ "                                              else "
				+ "                                                  case when right(trim(l.fscdrj), 1) = 'S' then 'SIGNALEMENT'  "
				+ "                                                  else '?'  "
				+ "                                                  end  "
				+ "                                              end "
				+ "                                          end " + "                                      end "
				+ "                                  end " + "                             end as CODE_MOUVEMENT, "
				+ " case when f.fenorm = '408' then f.feceta  else trim(l.fscdrj) end as CODE_REJET ,  "
				+ " case when f.fenorm = '408' then  "
				+ "        case when f.feceta in ('3','?') then 'ACCORD NON OUVERT'  " + "        else P1LDET "
				+ "        end  " + " else  "
				+ " translate(trim(fsrjet), 'CEEaaceeeeiioouuu', 'ÇËÉâàçéèëêîïôöùûü?:-/\\.,_''''()') end as LIBELLE_REJET "
				+ "from  " + " almerys.funnoep f  "
				+ "left outer join benalmfic.personp p on f.fenins = p.penins and f.fednai = p.pednai and f.fernai = p.pernai "
				+ "inner join benalmfic.couverp c on c.cprint = p.perper  "
				+ " and c.cpddef = (select max(c2.cpddef) from benalmfic.couverp c2 where c.cprint = c2.cprint and c.cpnmut = c2.cpnmut and c.cpncon = c2.cpncon ) "
				+ "and C.cpnmut in ('98549298','98540396') " + "inner join benalmfic.cplassp on c.cprint = ctrint "
				+ "left outer join almsecuprd.ptabdep on p1ctab = 'ETANOE' and p1carg = f.feceta "
				+ "left outer join almerys.funlssp l on l.fsnenr = fenenr "
				+ "left outer join almerys.arsrapp on sacdet in ('A', 'S' )and sanum929=f.fenenr "
				+ "left outer join tabindp_tmp on tvrind_alph = ctrioc and tvncli = :CLIENT "
				+ "WHERE  f.fenmut in (select distinct trnutr from almerys.tranclip where trnucli = :CLIENT) "
				+ "and c.cpncon = :CONTRAT " + conditionBenef + conditionDateMouvement + " UNION " + "SELECT DISTINCT "
				+ "	 COALESCE(tvnba, FBRIOC) as REFERENCE_INTERNE_CLIENT, " + "	FBNCON AS REFERENCE_CONTRAT, "
				+ "	Hg.FENMUT AS NUMERO_TELETRANSMISSION, "
				+ "	TRANSLATE( trim( hg.FENBEN ), 'aaaaceeeeiiiinooouuuuaaaceeeeiiooouu', 'Ã Ã¡Ã¢Ã¦Ã§Ã¨Ã©ÃªÃ«Ã¬Ã­Ã®Ã¯Ã±Ã²Ã³Ã´Ã¹ÃºÃ»Ã¼Ã Ã¡Ã¢Ã§Ã©Ã¨Ã«ÃªÃ¯Ã®Ã²Ã³Ã´Ã¹Ã¼?:-/\\.;_''''()' ) || ' ' || TRANSLATE( trim( hg.FEPBEN ), 'aaaaceeeeiiiinooouuuuaaaceeeeiiooouu', 'Ã Ã¡Ã¢Ã¦Ã§Ã¨Ã©ÃªÃ«Ã¬Ã­Ã®Ã¯Ã±Ã²Ã³Ã´Ã¹ÃºÃ»Ã¼Ã Ã¡Ã¢Ã§Ã©Ã¨Ã«ÃªÃ¯Ã®Ã²Ã³Ã´Ã¹Ã¼?:-/\\.;_''''()' ) AS NOM_PRENOM, "
				+ "	CASE  WHEN ( hg.FEDNAI > 18000000 and substr ( hg.FEDNAI, 7, 2 ) between 1 and 31 and substr ( hg.FEDNAI, 5, 2 ) between 1 and 12 ) THEN substr ( hg.FEDNAI, 7, 2 ) || '/' || substr ( hg.FEDNAI, 5, 2 ) || '/' || substr ( hg.FEDNAI, 1, 4 )  ELSE '01/01/0001'  "
				+ "	END AS DATE_NAISS, " + "	Hg.FEGREG AS Code_Regime, " + "	Hg.FECCPA AS Code_Caisse, "
				+ "	Hg.FECGES AS Code_Centre, " + "	Hg.FENINS, "
				+ "	CASE WHEN ( hg.FEDDEB > 18000000 and substr ( hg.FEDDEB, 7, 2 ) between 1 and 31 and substr ( hg.FEDDEB, 5, 2 ) between 1 and 12 ) THEN substr ( hg.FEDDEB, 7, 2 ) || '/' || substr ( hg.FEDDEB, 5, 2 ) || '/' || substr ( hg.FEDDEB, 1, 4 )  ELSE '01/01/0001'  "
				+ "	END AS DEBUT_NOEMISATION, "
				+ "	CASE WHEN ( hg.FEDFIN > 18000000 and substr ( hg.FEDFIN, 7, 2 ) between 1 and 31 and substr ( hg.FEDFIN, 5, 2 ) between 1 and 12 ) THEN substr ( hg.FEDFIN, 7, 2 ) || '/' || substr ( hg.FEDFIN, 5, 2 ) || '/' || substr ( hg.FEDFIN, 1, 4 )  ELSE '01/01/0001'  "
				+ "	END AS FIN_NOEMISATION, " + formingDateMouvementUnion + "	'408' AS TYPE_MOUVEMENT, "
				+ "	'/' AS CODE_MOUVEMENT, " + "	'/' AS CODE_REJET, " + "	L4MSGC AS LIBELLE_REJET "
				+ "FROM ALMERYS.ARG408Q HG " + "LEFT OUTER JOIN ALMERYS.ARL408Q C ON HG.FENENR = C.L4IARG "
				+ "LEFT OUTER JOIN ALMERYS.ARNBENP A ON A.RBIDEN = HG.RBIDEN "
				+ "left outer join tabindp_tmp on tvrind_alph = FBRIOC and tvncli = NUMCLI " + "WHERE  "
				+ "	FEUTMO = 'KO' " + "	AND FEDSAI > 20191204 " + "	AND FENORM = '408' "
				+ "	AND FENMUT in (select distinct trnutr from almerys.tranclip where trnucli = :CLIENT) "
				+ " AND FBNCON = :CONTRAT " + conditionBenefUnion + conditionDateMouvementUnion + orderByDateMouement;
		return sql;
	}
}
