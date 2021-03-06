package uk.nhs.careconnect.ri.entity.diagnosticReport;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;


import javax.persistence.*;


@Entity
@Table(name="DiagnosticReportDiagnosis", uniqueConstraints= @UniqueConstraint(name="PK_DIAGNOSTIC_REPORT_DIAGNOSIS", columnNames={"DIAGNOSTIC_REPORT_DIAGNOSIS_ID"})
		)
public class DiagnosticReportDiagnosis {

	public DiagnosticReportDiagnosis() {

	}

	public DiagnosticReportDiagnosis(DiagnosticReportEntity diagnosticReport) {
		this.diagnosticReport = diagnosticReport;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "DIAGNOSTIC_REPORT_DIAGNOSIS_ID")
	private Long diagnosisId;

	@ManyToOne
	@JoinColumn (name = "DIAGNOSTIC_REPORT_ID",foreignKey= @ForeignKey(name="FK_DIAGNOSTIC_REPORT_DIAGNOSTIC_REPORT_DIAGNOSIS"))
	private DiagnosticReportEntity diagnosticReport;

	@ManyToOne
	@JoinColumn (name = "CONCEPT_ID",foreignKey= @ForeignKey(name="FK_DIAGNOSTIC_REPORT_CONCEPT"))
	@LazyCollection(LazyCollectionOption.TRUE)
	private ConceptEntity diagnosis;

    public Long getDiagnosisId() { return diagnosisId; }
	public void setDiagnosisId(Long diagnosisId) { this.diagnosisId = diagnosisId; }

	public DiagnosticReportEntity getDiagnosticReport() {
	        return this.diagnosticReport;
	}

	public void setDiagnosticReport(DiagnosticReportEntity diagnosticReport) {
	        this.diagnosticReport = diagnosticReport;
	}

	public ConceptEntity getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(ConceptEntity diagnosis) {
		this.diagnosis = diagnosis;
	}
}
