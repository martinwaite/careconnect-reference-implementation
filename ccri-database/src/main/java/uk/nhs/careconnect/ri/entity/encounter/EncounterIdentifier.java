package uk.nhs.careconnect.ri.entity.encounter;

import uk.nhs.careconnect.ri.entity.BaseIdentifier;

import javax.persistence.*;


@Entity
@Table(name="EncounterIdentifier", uniqueConstraints= @UniqueConstraint(name="PK_ENCOUNTER_IDENTIFIER", columnNames={"ENCOUNTER_IDENTIFIER_ID"})
		,indexes =
		{
				@Index(name = "IDX_ENCOUNTER_IDENTIFER", columnList="value,SYSTEM_ID")

		})
public class EncounterIdentifier extends BaseIdentifier {

	public EncounterIdentifier() {

	}

	public EncounterIdentifier(EncounterEntity encounter) {
		this.encounter = encounter;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "ENCOUNTER_IDENTIFIER_ID")
	private Long identifierId;

	@ManyToOne
	@JoinColumn (name = "ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_ENCOUNTER_IDENTIFIER"))
	private EncounterEntity encounter;


    public Long getIdentifierId() { return identifierId; }
	public void setIdentifierId(Long identifierId) { this.identifierId = identifierId; }

	public EncounterEntity getEncounter() {
	        return this.encounter;
	}

	public void setEncounter(EncounterEntity encounter) {
	        this.encounter = encounter;
	}




}
