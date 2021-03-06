package uk.nhs.careconnect.ri.fhirserver.provider;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.daointerface.MedicationStatementRepository;
import uk.nhs.careconnect.ri.lib.OperationOutcomeFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class MedicationStatementProvider implements ICCResourceProvider {


    @Autowired
    private MedicationStatementRepository statementDao;

    @Autowired
    FhirContext ctx;
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return MedicationStatement.class;
    }

    @Override
    public Long count() {
        return statementDao.count();
    }

    @Update
    public MethodOutcome update(HttpServletRequest theRequest, @ResourceParam MedicationStatement statement, @IdParam IdType theId, @ConditionalUrlParam String theConditional, RequestDetails theRequestDetails) {


        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);


        MedicationStatement newMedicationStatement = statementDao.create(ctx,statement, theId, theConditional);
        method.setId(newMedicationStatement.getIdElement());
        method.setResource(newMedicationStatement);



        return method;
    }

    @Search
    public List<MedicationStatement> search(HttpServletRequest theRequest,
                                            @OptionalParam(name = MedicationStatement.SP_PATIENT) ReferenceParam patient
            , @OptionalParam(name = MedicationStatement.SP_EFFECTIVE) DateRangeParam effectiveDate
            , @OptionalParam(name = MedicationStatement.SP_STATUS) TokenParam status
            , @OptionalParam(name = MedicationStatement.SP_RES_ID) TokenParam resid
    ) {
        return statementDao.search(ctx,patient, effectiveDate, status,resid);
    }

    @Read()
    public MedicationStatement get(@IdParam IdType statementId) {

        MedicationStatement statement = statementDao.read(ctx,statementId);

        if ( statement == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No MedicationStatement/ " + statementId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return statement;
    }


}
