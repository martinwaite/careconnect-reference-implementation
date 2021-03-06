package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import uk.nhs.careconnect.ri.entity.documentReference.DocumentReferenceEntity;

import java.util.List;

public interface DocumentReferenceRepository extends BaseDao<DocumentReferenceEntity,DocumentReference> {
    void save(FhirContext ctx, DocumentReferenceEntity composition);

    DocumentReference read(FhirContext ctx, IdType theId);

    DocumentReferenceEntity readEntity(FhirContext ctx, IdType theId);

    DocumentReference create(FhirContext ctx, DocumentReference composition, @IdParam IdType theId, @ConditionalUrlParam String theConditional);

    List<DocumentReference> search(FhirContext ctx,

              @OptionalParam(name = Condition.SP_PATIENT) ReferenceParam patient
            , @OptionalParam(name = DocumentReference.SP_IDENTIFIER) TokenParam identifier
            , @OptionalParam(name = DocumentReference.SP_RES_ID) TokenParam id
            , @OptionalParam(name = DocumentReference.SP_TYPE) TokenParam type
            , @OptionalParam(name = DocumentReference.SP_PERIOD)DateRangeParam dateRange
            , @OptionalParam(name = DocumentReference.SP_SETTING) TokenParam setting

    );

    List<DocumentReferenceEntity> searchEntity(FhirContext ctx,
              @OptionalParam(name = DocumentReference.SP_PATIENT) ReferenceParam patient
            , @OptionalParam(name = Condition.SP_IDENTIFIER) TokenParam identifier
            , @OptionalParam(name = Condition.SP_RES_ID) TokenParam id
            , @OptionalParam(name = DocumentReference.SP_TYPE) TokenParam type
            , @OptionalParam(name = DocumentReference.SP_PERIOD)DateRangeParam dateRange
            , @OptionalParam(name = DocumentReference.SP_SETTING) TokenParam setting
    );
}
