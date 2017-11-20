package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import jdk.nashorn.internal.parser.Token;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.nhs.careconnect.ri.daointerface.transforms.AllergyIntoleranceEntityToFHIRAllergyIntoleranceTransformer;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.allergy.AllergyIntoleranceEntity;
import uk.nhs.careconnect.ri.entity.allergy.AllergyIntoleranceIdentifier;
import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;
import uk.nhs.careconnect.ri.entity.condition.ConditionIdentifier;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Repository
@Transactional
public class AllergyIntoleranceDao implements AllergyIntoleranceRepository {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ConceptRepository conceptDao;

    @Autowired
    PatientRepository patientDao;

    @Autowired
    PractitionerRepository practitionerDao;

    @Autowired
    EncounterRepository encounterDao;

    @Autowired
    private CodeSystemRepository codeSystemSvc;


    private static final Logger log = LoggerFactory.getLogger(AllergyIntoleranceDao.class);

    @Override
    public void save(FhirContext ctx,AllergyIntoleranceEntity allergy) {

    }



    @Autowired
    AllergyIntoleranceEntityToFHIRAllergyIntoleranceTransformer allergyIntoleranceEntityToFHIRAllergyIntoleranceTransformer;

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
    @Override
    public AllergyIntolerance read(FhirContext ctx,IdType theId) {
        if (isNumeric(theId.getIdPart())) {
            AllergyIntoleranceEntity allergyIntolerance = (AllergyIntoleranceEntity) em.find(AllergyIntoleranceEntity.class, Long.parseLong(theId.getIdPart()));

            return allergyIntolerance == null
                    ? null
                    : allergyIntoleranceEntityToFHIRAllergyIntoleranceTransformer.transform(allergyIntolerance);
        } else {
            return null;
        }
    }

    @Override
    public AllergyIntoleranceEntity readEntity(FhirContext ctx,IdType theId) {
        if (isNumeric(theId.getIdPart())) {
            AllergyIntoleranceEntity allergyIntolerance = (AllergyIntoleranceEntity) em.find(AllergyIntoleranceEntity.class, Long.parseLong(theId.getIdPart()));
            return allergyIntolerance;
        }
        return null;
    }

    @Override
    public AllergyIntolerance create(FhirContext ctx, AllergyIntolerance allergy, IdType theId, String theConditional) {

        log.debug("Allergy.save");
        //  log.info(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(encounter));
        AllergyIntoleranceEntity allergyEntity = null;

        if (allergy.hasId()) allergyEntity = readEntity(ctx, allergy.getIdElement());

        if (theConditional != null) {
            try {


                if (theConditional.contains("fhir.leedsth.nhs.uk/Id/allergy")) {
                    URI uri = new URI(theConditional);

                    String scheme = uri.getScheme();
                    String host = uri.getHost();
                    String query = uri.getRawQuery();
                    log.debug(query);
                    String[] spiltStr = query.split("%7C");
                    log.debug(spiltStr[1]);

                    List<AllergyIntoleranceEntity> results = searchEntity(ctx, null, null,null, new TokenParam().setValue(spiltStr[1]).setSystem("https://fhir.leedsth.nhs.uk/Id/allergy"));
                    for (AllergyIntoleranceEntity con : results) {
                        allergyEntity = con;
                        break;
                    }
                } else {
                    log.info("NOT SUPPORTED: Conditional Url = "+theConditional);
                }

            } catch (Exception ex) {

            }
        }

        if (allergyEntity == null) allergyEntity = new AllergyIntoleranceEntity();


        PatientEntity patientEntity = null;
        if (allergy.hasPatient()) {
            log.trace(allergy.getPatient().getReference());
            patientEntity = patientDao.readEntity(ctx, new IdType(allergy.getPatient().getReference()));
            allergyEntity.setPatient(patientEntity);
        }
        if (allergy.hasClinicalStatus()) {
            allergyEntity.setClinicalStatus(allergy.getClinicalStatus());
        }
        if (allergy.hasVerificationStatus()) {
            allergyEntity.setVerificationStatus(allergy.getVerificationStatus());
        }
        if (allergy.hasCode()) {
            ConceptEntity code = conceptDao.findCode(allergy.getCode().getCoding().get(0).getSystem(),allergy.getCode().getCoding().get(0).getCode());
            if (code != null) { allergyEntity.setCode(code); }
            else {
                log.error("Code: Missing System/Code = "+ allergy.getCode().getCoding().get(0).getSystem() +" code = "+allergy.getCode().getCoding().get(0).getCode());

                throw new IllegalArgumentException("Missing System/Code = "+ allergy.getCode().getCoding().get(0).getSystem()
                        +" code = "+allergy.getCode().getCoding().get(0).getCode());
            }
        }
        if (allergy.hasAssertedDate()) {
            allergyEntity.setAssertedDateTime(allergy.getAssertedDate());
        }
        if (allergy.hasLastOccurrence()) {
            allergyEntity.setLastOccurenceDateTime(allergy.getLastOccurrence());
        }
        if (allergy.hasOnset()) {
            try {
                allergyEntity.setOnsetDateTime(allergy.getOnsetDateTimeType().getValue());
            } catch (Exception ex) {

            }
        }
        if (allergy.hasAsserter() && allergy.getAsserter().getReference().contains("Practitioner")) {
            PractitionerEntity practitionerEntity = practitionerDao.readEntity(ctx, new IdType(allergy.getAsserter().getReference()));
            allergyEntity.setAsserterPractitioner(practitionerEntity);
        }

        em.persist(allergyEntity);

        for (Identifier identifier : allergy.getIdentifier()) {
            AllergyIntoleranceIdentifier allergyIdentifier = null;

            for (AllergyIntoleranceIdentifier orgSearch : allergyEntity.getIdentifiers()) {
                if (identifier.getSystem().equals(orgSearch.getSystemUri()) && identifier.getValue().equals(orgSearch.getValue())) {
                    allergyIdentifier = orgSearch;
                    break;
                }
            }
            if (allergyIdentifier == null)  allergyIdentifier = new AllergyIntoleranceIdentifier();

            allergyIdentifier.setValue(identifier.getValue());
            allergyIdentifier.setSystem(codeSystemSvc.findSystem(identifier.getSystem()));
            allergyIdentifier.setAllergyIntolerance(allergyEntity);
            em.persist(allergyIdentifier);
        }



        return allergyIntoleranceEntityToFHIRAllergyIntoleranceTransformer.transform(allergyEntity);
    }

    @Override
    public List<AllergyIntolerance> search(FhirContext ctx,ReferenceParam patient, DateRangeParam date, TokenParam clinicalStatus, TokenParam identifier) {
        List<AllergyIntoleranceEntity> qryResults = searchEntity(ctx,patient, date, clinicalStatus,identifier);
        List<AllergyIntolerance> results = new ArrayList<>();

        for (AllergyIntoleranceEntity allergyIntoleranceEntity : qryResults)
        {
            // log.trace("HAPI Custom = "+doc.getId());
            AllergyIntolerance allergyIntolerance = allergyIntoleranceEntityToFHIRAllergyIntoleranceTransformer.transform(allergyIntoleranceEntity);
            results.add(allergyIntolerance);
        }

        return results;
    }

    @Override
    public List<AllergyIntoleranceEntity> searchEntity(FhirContext ctx, ReferenceParam patient, DateRangeParam date, TokenParam clinicalStatus, TokenParam identifier) {


        List<AllergyIntoleranceEntity> qryResults = null;

        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<AllergyIntoleranceEntity> criteria = builder.createQuery(AllergyIntoleranceEntity.class);
        Root<AllergyIntoleranceEntity> root = criteria.from(AllergyIntoleranceEntity.class);

        List<Predicate> predList = new LinkedList<Predicate>();
        List<AllergyIntolerance> results = new ArrayList<AllergyIntolerance>();

        if (patient != null) {
            Join<AllergyIntoleranceEntity, PatientEntity> join = root.join("patient", JoinType.LEFT);

            Predicate p = builder.equal(join.get("id"),patient.getIdPart());
            predList.add(p);
        }
        if (identifier !=null)
        {
            Join<AllergyIntoleranceEntity, AllergyIntoleranceIdentifier> join = root.join("identifiers", JoinType.LEFT);

            Predicate p = builder.equal(join.get("value"),identifier.getValue());
            predList.add(p);
            // TODO predList.add(builder.equal(join.get("system"),identifier.getSystem()));

        }

        if (clinicalStatus != null) {

            Integer status = null;
            switch (clinicalStatus.getValue().toLowerCase()) {
                case "active":
                    status = 0;
                    break;
                case "inactive":
                    status = 1;
                    break;
                case "resolved":
                    status = 2;
                    break;
                default: status = -1;

            }

            Predicate p = builder.equal(root.get("clinicalStatus"), status);
            predList.add(p);

        }

        ParameterExpression<java.util.Date> parameterLower = builder.parameter(java.util.Date.class);
        ParameterExpression<java.util.Date> parameterUpper = builder.parameter(java.util.Date.class);

        if (date !=null)
        {


            if (date.getLowerBoundAsInstant() != null) log.debug("getLowerBoundAsInstant()="+date.getLowerBoundAsInstant().toString());
            if (date.getUpperBoundAsInstant() != null) log.debug("getUpperBoundAsInstant()="+date.getUpperBoundAsInstant().toString());


            if (date.getLowerBound() != null) {

                DateParam dateParam = date.getLowerBound();
                log.debug("Lower Param - " + dateParam.getValue() + " Prefix - " + dateParam.getPrefix());

                switch (dateParam.getPrefix()) {
                    case GREATERTHAN: {
                        Predicate p = builder.greaterThan(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);

                        break;
                    }
                    case GREATERTHAN_OR_EQUALS: {
                        Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);
                        break;
                    }
                    case APPROXIMATE:
                    case EQUAL: {

                        Predicate plow = builder.greaterThanOrEqualTo(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(plow);
                        break;
                    }
                    case NOT_EQUAL: {
                        Predicate p = builder.notEqual(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);
                        break;
                    }
                    case STARTS_AFTER: {
                        Predicate p = builder.greaterThan(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);
                        break;

                    }
                    default:
                        log.trace("DEFAULT DATE(0) Prefix = " + date.getValuesAsQueryTokens().get(0).getPrefix());
                }
            }
            if (date.getUpperBound() != null) {

                DateParam dateParam = date.getUpperBound();

                log.debug("Upper Param - " + dateParam.getValue() + " Prefix - " + dateParam.getPrefix());

                switch (dateParam.getPrefix()) {
                    case APPROXIMATE:
                    case EQUAL: {
                        Predicate pupper = builder.lessThan(root.<Date>get("assertedDateTime"), parameterUpper);
                        predList.add(pupper);
                        break;
                    }

                    case LESSTHAN_OR_EQUALS: {
                        Predicate p = builder.lessThanOrEqualTo(root.<Date>get("assertedDateTime"), parameterUpper);
                        predList.add(p);
                        break;
                    }
                    case ENDS_BEFORE:
                    case LESSTHAN: {
                        Predicate p = builder.lessThan(root.<Date>get("assertedDateTime"), parameterUpper);
                        predList.add(p);

                        break;
                    }
                    default:
                        log.trace("DEFAULT DATE(0) Prefix = " + date.getValuesAsQueryTokens().get(0).getPrefix());
                }
            }

        }



        Predicate[] predArray = new Predicate[predList.size()];
        predList.toArray(predArray);
        if (predList.size()>0)
        {
            criteria.select(root).where(predArray);
        }
        else
        {
            criteria.select(root);
        }


        TypedQuery<AllergyIntoleranceEntity> typedQuery = em.createQuery(criteria);

        if (date != null) {
            if (date.getLowerBound() != null)
                typedQuery.setParameter(parameterLower, date.getLowerBoundAsInstant(), TemporalType.TIMESTAMP);
            if (date.getUpperBound() != null)
                typedQuery.setParameter(parameterUpper, date.getUpperBoundAsInstant(), TemporalType.TIMESTAMP);
        }
        qryResults = typedQuery.getResultList();

        return qryResults;
    }
}