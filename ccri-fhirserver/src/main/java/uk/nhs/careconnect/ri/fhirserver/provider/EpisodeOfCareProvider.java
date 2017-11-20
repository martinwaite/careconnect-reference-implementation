package uk.nhs.careconnect.ri.fhirserver.provider;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.common.OperationOutcomeFactory;
import uk.nhs.careconnect.ri.daointerface.EpisodeOfCareRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class EpisodeOfCareProvider implements IResourceProvider {


    @Autowired
    private EpisodeOfCareRepository episodeDao;

    @Autowired
    FhirContext ctx;
    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return EpisodeOfCare.class;
    }


    @Update
    public MethodOutcome updateEpisodeOfCare(HttpServletRequest theRequest, @ResourceParam EpisodeOfCare episode, @IdParam IdType theId, @ConditionalUrlParam String theConditional, RequestDetails theRequestDetails) {


        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);


        EpisodeOfCare newEpisodeOfCare = episodeDao.create(ctx,episode, theId, theConditional);
        method.setId(newEpisodeOfCare.getIdElement());
        method.setResource(newEpisodeOfCare);



        return method;
    }

    @Search
    public List<EpisodeOfCare> searchEpisodeOfCare(HttpServletRequest theRequest,
                                                   @OptionalParam(name = EpisodeOfCare.SP_PATIENT) ReferenceParam patient
            , @OptionalParam(name = EpisodeOfCare.SP_DATE) DateRangeParam date) {
        return episodeDao.search(ctx,patient, date);
    }

    @Read()
    public EpisodeOfCare getEpisodeOfCare(@IdParam IdType episodeId) {

        EpisodeOfCare episode = episodeDao.read(ctx,episodeId);

        if ( episode == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No EpisodeOfCare/ " + episodeId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return episode;
    }


}