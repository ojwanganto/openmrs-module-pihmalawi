package org.openmrs.module.pihmalawi.reporting.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.reporting.Helper;
import org.openmrs.module.pihmalawi.reporting.extension.InStateAtLocationCohortDefinition;
import org.openmrs.module.pihmalawi.reporting.extension.PatientStateAtLocationCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.InStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.PatientStateCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

public class ArtReportElements {

	static Helper h = new Helper();

	public static List<List<Location>> locations() {
		return Arrays.asList(
				Arrays.asList(h.location("Neno District Hospital"),
						h.location("Ligowe HC"), h.location("Outpatient"),
						h.location("Registration"), h.location("Vitals"),
						h.location("Matandani Rural Health Center")),
				Arrays.asList(h.location("Magaleta HC")),
				Arrays.asList(h.location("Nsambe HC")),
				Arrays.asList(h.location("Lisungwi Community Hospital"),
						h.location("Midzemba HC")),
				Arrays.asList(h.location("Chifunga HC")),
				Arrays.asList(h.location("Matope HC")),
				Arrays.asList(h.location("Neno Mission HC")),
				Arrays.asList(h.location("Nkhula Falls RHC")),
				Arrays.asList(h.location("Zalewa HC")));
	}

	public static List<Location> hivLocations() {
		return Arrays.asList(h.location("Neno District Hospital"),
				h.location("Magaleta HC"), h.location("Nsambe HC"),
				h.location("Neno Mission HC"), h.location("Ligowe HC"),
				h.location("Matandani Rural Health Center"),
				h.location("Lisungwi Community Hospital"),
				h.location("Matope HC"), h.location("Chifunga HC"),
				h.location("Zalewa HC"), h.location("Midzemba HC"),
				h.location("Nkhula Falls RHC"));
	}

	public static List<Location> hivStaticLocations() {
		return Arrays.asList(h.location("Neno District Hospital"),
				h.location("Magaleta HC"), h.location("Nsambe HC"),
				h.location("Neno Mission HC"), 
				h.location("Lisungwi Community Hospital"),
				h.location("Matope HC"), h.location("Chifunga HC"),
				h.location("Zalewa HC"),
				h.location("Nkhula Falls RHC"));
	}

	public static String hivSiteCode(Location l) {
		if ("Neno District Hospital".equals(l.getName()))
			return "ndh";
		if ("Magaleta HC".equals(l.getName()))
			return "mgt";
		if ("Nsambe HC".equals(l.getName()))
			return "nsm";
		if ("Neno Mission HC".equals(l.getName()))
			return "nop";
		if ("Ligowe HC".equals(l.getName()))
			return "lig";
		if ("Matandani Rural Health Center".equals(l.getName()))
			return "mat";
		if ("Lisungwi Community Hospital".equals(l.getName()))
			return "lsi";
		if ("Matope HC".equals(l.getName()))
			return "mte";
		if ("Chifunga HC".equals(l.getName()))
			return "cfa";
		if ("Zalewa HC".equals(l.getName()))
			return "zla";
		if ("Midzemba HC".equals(l.getName()))
			return "mid";
		if ("Nkhula Falls RHC".equals(l.getName()))
			return "nka";
		return null;
	}

	public static List<EncounterType> hivEncounterTypes() {
		return Arrays
				.asList(h.encounterType("ART_INITIAL"),
						h.encounterType("ART_FOLLOWUP"),
						h.encounterType("PART_INITIAL"),
						h.encounterType("PART_FOLLOWUP"),
						h.encounterType("EID_INITIAL"),
						h.encounterType("EID_FOLLOWUP"));
	}

	public static CohortDefinition onArtAtLocationOnDate(String prefix) {
		// On ART at end of period
		InStateAtLocationCohortDefinition iscd = new InStateAtLocationCohortDefinition();
		iscd.setName(prefix + ": On ART with location & date_");
		// internal transfers are still under responsibility of original clinic
		// states.add(Context.getProgramWorkflowService().getProgramByName("HIV program").getWorkflowByName("Treatment status")
		// .getStateByName("Transferred internally"));
		iscd.setState(h.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		iscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition everOnArtStartedOnOrBefore(
			String prefix) {
		PatientStateCohortDefinition pscd = new PatientStateCohortDefinition();
		pscd.setName(prefix + ": Ever on ART_");
		pscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status",
				"On antiretrovirals")));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		h.replaceCohortDefinition(pscd);
		return pscd;
	}

	public static CohortDefinition everOnArtAtLocationStartedOnOrBefore(
			String prefix) {
		PatientStateAtLocationCohortDefinition pscd = new PatientStateAtLocationCohortDefinition();
		pscd.setName(prefix + ": Having state at location_");
		pscd.setState(h.workflowState("HIV program", "Treatment status",
				"On antiretrovirals"));
		pscd.addParameter(new Parameter("startedOnOrBefore",
				"startedOnOrBefore", Date.class));
		pscd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(pscd);
		return pscd;
	}

	public static CohortDefinition inStateAtLocationOnDate(String prefix) {
		InStateAtLocationCohortDefinition islcd = new InStateAtLocationCohortDefinition();
		islcd.setName(prefix + ": In state at location_");
		islcd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		islcd.addParameter(new Parameter("onDate", "endDate", Date.class));
		islcd.addParameter(new Parameter("location", "location", Location.class));
		h.replaceCohortDefinition(islcd);
		return islcd;
	}

	public static CohortDefinition transferredInternallyOnDate(String reportTag) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": transferred internally_");
		iscd.setStates(Arrays.asList(h.workflowState("HIV program", "Treatment status",
				"Transferred internally")));
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition everInStateAtLocation(String prefix) {
		SqlCohortDefinition scd = new SqlCohortDefinition();
		scd.setName(prefix
				+ ": Ever enrolled in program at location with state_");
		String sql = ""
				+ "SELECT pp.patient_id"
				+ " FROM patient_program pp, program_workflow pw, program_workflow_state pws, patient_state ps"
				+ " WHERE pp.program_id = pw.program_id AND pw.program_workflow_id = pws.program_workflow_id"
				+ "   AND pws.program_workflow_state_id = ps.state AND ps.patient_program_id = pp.patient_program_id"
				+ "   AND pws.program_workflow_id = 1 AND ps.state = :state "
				+ "   AND pp.location_id = :location AND (ps.end_date <= :endDate OR ps.end_date IS NULL)"
				+ "   AND pw.retired = 0 AND pp.voided = 0 AND pws.retired = 0 AND ps.voided = 0"
				+ " GROUP BY pp.patient_id;";
		scd.setQuery(sql);
		scd.addParameter(new Parameter("endDate", "End Date", Date.class));
		scd.addParameter(new Parameter("location", "Location", Location.class));
		scd.addParameter(new Parameter("state", "State",
				ProgramWorkflowState.class));
		h.replaceCohortDefinition(scd);
		return scd;
	}

	public static CohortDefinition onArtOnDate(String reportTag) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": On ART_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("On antiretrovirals"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}

	public static CohortDefinition inHccOnDate(String reportTag) {
		InStateCohortDefinition iscd = new InStateCohortDefinition();
		iscd.setName(reportTag + ": In HCC_");
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		states = new ArrayList<ProgramWorkflowState>();
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("Pre-ART (Continue)") );
		states.add(Context.getProgramWorkflowService()
				.getProgramByName("HIV program")
				.getWorkflowByName("Treatment status")
				.getStateByName("Exposed Child (Continue)"));
		iscd.setStates(states);
		iscd.addParameter(new Parameter("onDate", "onDate", Date.class));
		h.replaceCohortDefinition(iscd);
		return iscd;
	}
}
