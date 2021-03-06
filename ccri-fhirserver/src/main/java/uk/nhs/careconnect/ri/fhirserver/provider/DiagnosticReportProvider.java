package uk.nhs.careconnect.ri.fhirserver.provider;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.DiagnosticReport;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.daointerface.DiagnosticReportRepository;
import uk.nhs.careconnect.ri.lib.OperationOutcomeFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class DiagnosticReportProvider implements ICCResourceProvider {



    @Autowired
    private DiagnosticReportRepository diagnosticReportDao;

    @Autowired
    FhirContext ctx;

    @Override
    public Long count() {
        return diagnosticReportDao.count();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return DiagnosticReport.class;
    }


    @Update
    public MethodOutcome update(HttpServletRequest theRequest, @ResourceParam DiagnosticReport diagnosticReport, @IdParam IdType theId, @ConditionalUrlParam String theConditional, RequestDetails theRequestDetails) {

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);

        DiagnosticReport newDiagnosticReport = diagnosticReportDao.create(ctx,diagnosticReport, theId, theConditional);
        method.setId(newDiagnosticReport.getIdElement());
        method.setResource(newDiagnosticReport);

        return method;
    }

    @Create
    public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam DiagnosticReport diagnosticReport) {

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();
        method.setOperationOutcome(opOutcome);

        DiagnosticReport newDiagnosticReport = diagnosticReportDao.create(ctx,diagnosticReport, null,null);
        method.setId(newDiagnosticReport.getIdElement());
        method.setResource(newDiagnosticReport);

        return method;
    }

    @Search
    public List<DiagnosticReport> search(HttpServletRequest theRequest,
                                  @OptionalParam(name = DiagnosticReport.SP_PATIENT) ReferenceParam patient
            , @OptionalParam(name = DiagnosticReport.SP_IDENTIFIER) TokenParam identifier
            , @OptionalParam(name = DiagnosticReport.SP_RES_ID) TokenParam resid
                                  ) {
        return diagnosticReportDao.search(ctx,patient,identifier,resid);
    }

    @Read()
    public DiagnosticReport get(@IdParam IdType diagnosticReportId) {

        DiagnosticReport diagnosticReport = diagnosticReportDao.read(ctx,diagnosticReportId);

        if ( diagnosticReport == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No DiagnosticReport/ " + diagnosticReportId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return diagnosticReport;
    }


}
