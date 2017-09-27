package uk.nhs.careconnect.ri.entity.Terminology;

import uk.nhs.careconnect.ri.entity.BaseResource;

import javax.persistence.*;

@Entity
@Table(name="ConceptDesignation", indexes= {

        @Index(columnList = "designationId", name = "IDX_DESIGNATION")
})
public class ConceptDesignation extends BaseResource {

    public enum DesignationUse {
        FullySpecifiedName,
        Synonym,
        Definition
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "CONCEPT_DESIGNATION_ID")
    private Long Id;

    @ManyToOne
    @JoinColumn (name = "CONCEPT_ID",foreignKey= @ForeignKey(name="FK_CONCEPT_CONCEPT_DESIGNATION"))
    private ConceptEntity conceptEntity;

    @Enumerated(EnumType.ORDINAL)
    private DesignationUse designationUse;

    @ManyToOne
    @JoinColumn(name="language")
    private ConceptEntity language;

    @Column(name="term")
    private String term;

    @Column(name="designationId")
    private String designationId;

    @Column(name="active")
    private Boolean active;

    public String setTerm(String term) {
        this.term = term;
        return this.term;
    }

    public Boolean getActive() {
        return active;
    }

    public ConceptEntity getConceptEntity() {
        return conceptEntity;
    }

    public Boolean setActive(Boolean active) {
        this.active = active;
        return active;
    }

    public String getDesignationId() {
        return designationId;
    }

    public ConceptDesignation setDesignationId(String designationId) {
        this.designationId = designationId;
        return this;
    }

    public ConceptEntity getLanguage() {
        return language;
    }

    public DesignationUse getUse() {
        return designationUse;
    }

    public ConceptDesignation setUse(DesignationUse designationUse) {
        this.designationUse = designationUse;
        return this;
    }

    public Long getId() {
        return Id;
    }

    public String getValue() {
        return term;
    }

    public void setConceptEntity(ConceptEntity conceptEntity) {
        this.conceptEntity = conceptEntity;
    }

    public Long setId(Long id) {
        this.Id = id;
        return this.Id;
    }

    public ConceptEntity setLanguage(ConceptEntity language) {
        this.language = language;
        return this.language;
    }


}
