package uk.nhs.careconnect.ri.daointerface.transforms;


import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.entity.BaseAddress;
import uk.nhs.careconnect.ri.entity.medication.MedicationRequestDosage;
import uk.nhs.careconnect.ri.entity.medication.MedicationRequestEntity;
import uk.nhs.careconnect.ri.entity.medication.MedicationRequestIdentifier;
import uk.org.hl7.fhir.core.Stu3.CareConnectExtension;
import uk.org.hl7.fhir.core.Stu3.CareConnectProfile;
import uk.org.hl7.fhir.core.Stu3.CareConnectSystem;


@Component
public class MedicationRequestEntityToFHIRMedicationTransformer implements Transformer<MedicationRequestEntity, Medication> {



    @Override
    public Medication transform(final MedicationRequestEntity medicationRequestEntity) {

        // TODO Move to transf
        Medication medication = new Medication();

        Meta meta = new Meta().addProfile(CareConnectProfile.Medication_1);

        if (medicationRequestEntity.getUpdated() != null) {
            meta.setLastUpdated(medicationRequestEntity.getUpdated());
        }
        else {
            if (medicationRequestEntity.getCreated() != null) {
                meta.setLastUpdated(medicationRequestEntity.getCreated());
            }
        }
        medication.setMeta(meta);

        medication.setId(medicationRequestEntity.getId().toString());
        medication.getCode()
                .addCoding()
                .setCode(medicationRequestEntity.getMedicationCode().getCode())
                .setSystem(medicationRequestEntity.getMedicationCode().getSystem())
                .setDisplay(medicationRequestEntity.getMedicationCode().getDisplay());
        return medication;

    }
}
