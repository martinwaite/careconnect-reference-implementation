package uk.nhs.careconnect.ri.entity.composition;


import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hl7.fhir.dstu3.model.Composition;
import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.organization.OrganisationEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Composition")
public class CompositionEntity  extends BaseResource {

    private static final int MAX_DESC_LENGTH = 1024;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="COMPOSITION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn (name = "PATIENT_ID",nullable = false, foreignKey= @ForeignKey(name="FK_PATIENT_COMPOSITION"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private PatientEntity patient;

    @OneToMany(mappedBy="composition", targetEntity = CompositionIdentifier.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<CompositionIdentifier> identifiers = new HashSet<>();

    @Column(name="status")
    Composition.CompositionStatus status;

    @ManyToOne
    @JoinColumn (name = "TYPE_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_COMPOSITION_TYPE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity type;

    @ManyToOne
    @JoinColumn (name = "CLASS_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_COMPOSITION_CLASS"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity class_;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn (name = "ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_COMPOSITION_ENCOUNTER"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private EncounterEntity encounter;

    @ManyToOne
    @JoinColumn(name="AUTHOR_PRACTITIONER_ID",foreignKey= @ForeignKey(name="FK_COMPOSITION_PRACTITIONER_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private PractitionerEntity authorPractitioner;

    @Column(name="TITLE_COMPOSITION",length = MAX_DESC_LENGTH,nullable = true)
    private String title;


    @Column (name = "CONFIDENTIALITY_CODE")
    private Composition.DocumentConfidentiality confidentiality;

    @ManyToOne
    @JoinColumn(name="CUSTODIAN_ORGANISATION_ID",foreignKey= @ForeignKey(name="FK_COMPOSITION_CUSTODIAN_ORGANISATION_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private OrganisationEntity custodianOrganisation;

    @OneToMany(mappedBy="composition", targetEntity = CompositionSection.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<CompositionSection> sections = new HashSet<>();

    @Override
    public Long getId() {
        return this.id;
    }

    public CompositionEntity setId(Long id) {
        this.id = id;
        return this;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public CompositionEntity setPatient(PatientEntity patient) {
        this.patient = patient;
        return this;
    }

    public Set<CompositionIdentifier> getIdentifiers() {
        return identifiers;
    }

    public CompositionEntity setIdentifiers(Set<CompositionIdentifier> identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    public Composition.CompositionStatus getStatus() {
        return status;
    }

    public CompositionEntity setStatus(Composition.CompositionStatus status) {
        this.status = status;
        return this;
    }

    public ConceptEntity getType() {
        return type;
    }

    public CompositionEntity setType(ConceptEntity type) {
        this.type = type;
        return this;
    }

    public ConceptEntity getClass_() {
        return class_;
    }

    public CompositionEntity setClass_(ConceptEntity class_) {
        this.class_ = class_;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public CompositionEntity setDate(Date date) {
        this.date = date;
        return this;
    }

    public EncounterEntity getEncounter() {
        return encounter;
    }

    public CompositionEntity setEncounter(EncounterEntity encounter) {
        this.encounter = encounter;
        return this;
    }

    public PractitionerEntity getAuthorPractitioner() {
        return authorPractitioner;
    }

    public CompositionEntity setAuthorPractitioner(PractitionerEntity authorPractitioner) {
        this.authorPractitioner = authorPractitioner;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public CompositionEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public Composition.DocumentConfidentiality getConfidentiality() {
        return confidentiality;
    }

    public CompositionEntity setConfidentiality(Composition.DocumentConfidentiality confidentiality) {
        this.confidentiality = confidentiality;
        return this;
    }

    public OrganisationEntity getCustodianOrganisation() {
        return custodianOrganisation;
    }

    public CompositionEntity setCustodianOrganisation(OrganisationEntity custodianOrganisation) {
        this.custodianOrganisation = custodianOrganisation;
        return this;
    }

    public Set<CompositionSection> getSections() {
        return sections;
    }

    public CompositionEntity setSections(Set<CompositionSection> sections) {
        this.sections = sections;
        return this;
    }
}
