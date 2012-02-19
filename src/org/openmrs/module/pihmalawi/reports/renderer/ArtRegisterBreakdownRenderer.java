package org.openmrs.module.pihmalawi.reports.renderer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;

public class ArtRegisterBreakdownRenderer extends BreakdownRowRenderer {

	public DataSetRow renderRow(Patient p,
			PatientIdentifierType patientIdentifierType,
			Location locationParameter, Date startDateParameter,
			Date endDateParameter) {
		DataSetRow row = new DataSetRow();

		// exception handling looks really ugly, but its necessary...
		try {
			addCol(row, "ARV #",
					patientLink(p, patientIdentifierType, locationParameter));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All HCC #s",
					identifiers(p, lookupPatientIdentifierType("HCC Number")));
		} catch (Exception e) {
			log.error(e);
		}
		try {
			addCol(row, "All ARV #s",
					identifiers(p, lookupPatientIdentifierType("ARV Number")));
		} catch (Exception e) {
			log.error(e);
		}
		addFirstEncounterCols(row, p, lookupEncounterType("ART_INITIAL"),
				"ART initial");
		addDemographicCols(row, p, endDateParameter);
		addOutcomeCols(row, p, locationParameter,
				lookupProgramWorkflow("HIV program", "Treatment status"));
		addCol(row, "Missing 2+ months since", "(tbd)");
		addMostRecentOutcomeWithinDatabaseCols(row, p,
				lookupProgramWorkflow("HIV program", "Treatment status"),
				endDateParameter);
		addEnrollmentDateCols(row, 
				p,
				locationParameter,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"On antiretrovirals"), "Enrollment date at location (ART or HCC)");
		addFirstTimeEnrollmentCols(row,
				p,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"On antiretrovirals"), "1st time enrollment (ART or HCC)");
		addFirstTimeChangeToStateDateCols(
				row,
				p,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"On antiretrovirals"), "1st time in ART");
		addFirstTimeChangeToStateDateCols(
				row,
				p,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"Pre-ART (Continue)"), "1st time in Pre-ART");
		addFirstTimeChangeToStateDateCols(
				row,
				p,
				lookupProgramWorkflowState("HIV program", "Treatment status",
						"Exposed Child (Continue)"),
				"1st time in Exposed Child");
		// reason starting arvs
		addReasonStartingArvsCols(row, p, endDateParameter);
		// date 1st time arvs
		addMostRecentDatetimeObsCols(row, p, lookupConcept("Start date 1st line ARV"), endDateParameter);
		// current regimen
		addMostRecentObsCols(row, p, lookupConcept("Malawi Antiretroviral drugs received"), endDateParameter);

		addVhwCol(row, p);
		addLastVisitCols(row, p, Arrays.asList(
				lookupEncounterType("ART_FOLLOWUP"),
				lookupEncounterType("PART_FOLLOWUP"),
				lookupEncounterType("EXPOSED_CHILD_FOLLOWUP")));
		addAllEnrollmentsCol(row, p);
		return row;
	}
	
	private void addReasonStartingArvsCols(DataSetRow row, Patient p, Date endDate) {
		try {
		List<Encounter> es = Context.getEncounterService().getEncounters(p,
				null, null, endDate, null, Arrays.asList(lookupEncounterType("ART_INITIAL")), null, false);
		String reasons = "";

		List<Obs> obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("CD4 count")), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += ", CD4: " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Kaposis sarcoma side effects worsening while on ARVs?")), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += ", KS: " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Tuberculosis treatment status")), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += ", TB: " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("WHO stage")), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += ", STAGE: " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Cd4%")), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += ", TLC: " + o.getValueAsString(Context.getLocale());
		}
		obs = Context.getObsService().getObservations(
				Arrays.asList((Person) p), es, Arrays.asList(lookupConcept("Presumed severe HIV criteria present")), null,
				null, null, null, 1, null, null, endDate, false);
		if (obs.iterator().hasNext()) {
			Obs o = obs.iterator().next();
			reasons += ", PSHD: " + o.getValueAsString(Context.getLocale());
		}
		DataSetColumn c = new DataSetColumn("ARV start reasons", "ARV start reasons", String.class);
		row.addColumnValue(c, reasons);
		} catch (Exception e) {
			log.error(e);
		}
	}



}
