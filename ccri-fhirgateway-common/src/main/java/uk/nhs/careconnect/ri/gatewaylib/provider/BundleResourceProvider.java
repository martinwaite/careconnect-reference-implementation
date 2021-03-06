package uk.nhs.careconnect.ri.gatewaylib.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.ValidationModeEnum;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import org.apache.camel.*;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.lib.OperationOutcomeFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@Component
public class BundleResourceProvider implements IResourceProvider {

    @Autowired
    CamelContext context;

    @Autowired
    FhirContext ctx;

    @Autowired
    ResourceTestProvider resourceTestProvider;

    private static final Logger log = LoggerFactory.getLogger(BundleResourceProvider.class);

    @Override
    public Class<Bundle> getResourceType() {
        return Bundle.class;
    }

    @Validate
    public MethodOutcome testResource(@ResourceParam Bundle bundle,
                                  @Validate.Mode ValidationModeEnum theMode,
                                  @Validate.Profile String theProfile) {
        return resourceTestProvider.testResource(bundle,theMode,theProfile);
    }

    @Create
    public MethodOutcome create(HttpServletRequest httpRequest, @ResourceParam Bundle bundle) {


        // Example message https://gist.github.com/IOPS-DEV/1a532eb43b226dcd6ce26a6b698019f4#file-ec_edischarge_full_payload_example-01

        /*

        Task to do here.



        Ensure the message can be accepted. Validate the message.
        (may need to convert from json to xml - currently throws an error if not xml)

        if ok send onto camel for processing (Async)

        build reply.

        P.S. We are using XML here, normally we use JSON but bundles in the NHS are primarily XML at present.

         */

        ProducerTemplate template = context.createProducerTemplate();

        IBaseResource resource = null;
        try {
            InputStream inputStream = null;
            String newXmlResource = ctx.newXmlParser().encodeResourceToString(bundle);

            switch (bundle.getType()) {
                case COLLECTION:
                case MESSAGE:

                    Exchange exchangeBundle = template.send("direct:FHIRBundleCollection", ExchangePattern.InOut, new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            exchange.getIn().setBody(newXmlResource);
                            exchange.getIn().setHeader(Exchange.HTTP_QUERY, null);
                            exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                            exchange.getIn().setHeader(Exchange.HTTP_PATH, "Bundle");
                           // exchange.getIn().setHeader("Prefer", "return=representation");
                            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/fhir+xml");
                        }
                    });
                    // TODO need proper responses from the camel processor. KGM 18/Apr/2018
                    resource = ctx.newXmlParser().parseResource((String) exchangeBundle.getIn().getBody());
                    break;

                case DOCUMENT:
                    Exchange exchangeDocument = template.send("direct:FHIRBundleDocument", ExchangePattern.InOut, new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            exchange.getIn().setBody(newXmlResource);
                            exchange.getIn().setHeader(Exchange.HTTP_QUERY, null);
                            exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                            exchange.getIn().setHeader(Exchange.HTTP_PATH, "Bundle");
                           // exchange.getIn().setHeader("Prefer", "return=representation");
                            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/fhir+xml");
                        }
                    });
                    // TODO need proper responses from the camel processor. KGM 18/Apr/2018

                    // This response is coming from an external FHIR Server, so uses inputstream
                    if (exchangeDocument.getIn().getBody() instanceof InputStream) {
                        inputStream = (InputStream) exchangeDocument.getIn().getBody();
                        Reader reader = new InputStreamReader(inputStream);
                        resource = ctx.newXmlParser().parseResource(reader);
                    } else
                        if (exchangeDocument.getIn().getBody() instanceof String) {
                            resource = ctx.newXmlParser().parseResource((String) exchangeDocument.getIn().getBody());
                        }

                    default:
                        // TODO
            }
        } catch(Exception ex) {
            log.error("XML Parse failed " + ex.getMessage());
            throw new InternalErrorException(ex.getMessage());
        }
        if (resource instanceof Bundle) {
            bundle = (Bundle) resource;
        } else if (resource instanceof OperationOutcome) {
            if(((OperationOutcome) resource).getIssue().size()>0)
            {
                OperationOutcome operationOutcome = (OperationOutcome) resource;
                log.info("Sever Returned: "+ctx.newJsonParser().encodeResourceToString(operationOutcome));
                OperationOutcomeFactory.convertToException(operationOutcome);
            }
        }
        else {
            throw new InternalErrorException("Unknown Error");
        }

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);

        OperationOutcome opOutcome = null;
        if (resource instanceof OperationOutcome) {
            opOutcome = (OperationOutcome) resource;
        } else {
            method.setResource(resource);
            opOutcome = new OperationOutcome();
        }

        method.setOperationOutcome(opOutcome);

        return method;
    }


    @Update
    public MethodOutcome updateBundle(HttpServletRequest theRequest, @ResourceParam Bundle bundle, @IdParam IdType bundleId, @ConditionalUrlParam String conditional, RequestDetails theRequestDetails) {

        ProducerTemplate template = context.createProducerTemplate();

        IBaseResource resource = null;
        try {
            InputStream inputStream = null;
            String newXmlResource = ctx.newXmlParser().encodeResourceToString(bundle);

            switch (bundle.getType()) {
                case COLLECTION:
                case MESSAGE:

                    Exchange exchangeBundle = template.send("direct:FHIRBundleCollection", ExchangePattern.InOut, new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            exchange.getIn().setBody(newXmlResource);
                            exchange.getIn().setHeader(Exchange.HTTP_QUERY, conditional);
                            exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
                            exchange.getIn().setHeader(Exchange.HTTP_PATH, "Bundle");

                            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/fhir+xml");
                        }
                    });
                    // TODO need proper responses from the camel processor. KGM 18/Apr/2018
                    resource = ctx.newXmlParser().parseResource((String) exchangeBundle.getIn().getBody());
                    break;

                case DOCUMENT:
                    Exchange exchangeDocument = template.send("direct:FHIRBundleDocument", ExchangePattern.InOut, new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            exchange.getIn().setBody(newXmlResource);
                            exchange.getIn().setHeader(Exchange.HTTP_QUERY, conditional);
                            exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
                            exchange.getIn().setHeader(Exchange.HTTP_PATH, "Bundle");

                            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/fhir+xml");
                        }
                    });
                    // TODO need proper responses from the camel processor. KGM 18/Apr/2018

                    // This response is coming from an external FHIR Server, so uses inputstream
                    if (exchangeDocument.getIn().getBody() instanceof InputStream) {
                        inputStream = (InputStream) exchangeDocument.getIn().getBody();
                        Reader reader = new InputStreamReader(inputStream);
                        resource = ctx.newXmlParser().parseResource(reader);
                    } else
                    if (exchangeDocument.getIn().getBody() instanceof String) {
                        resource = ctx.newXmlParser().parseResource((String) exchangeDocument.getIn().getBody());
                    }

                default:
                    // TODO
            }
        } catch(Exception ex) {
            log.error("XML Parse failed " + ex.getMessage());
            throw new InternalErrorException(ex.getMessage());
        }
        if (resource instanceof Bundle) {
            bundle = (Bundle) resource;
        } else if (resource instanceof OperationOutcome) {
            if(((OperationOutcome) resource).getIssue().size()>0)
            {
                OperationOutcome operationOutcome = (OperationOutcome) resource;
                log.info("Sever Returned: "+ctx.newJsonParser().encodeResourceToString(operationOutcome));
                OperationOutcomeFactory.convertToException(operationOutcome);
            }
        }
        else {
            throw new InternalErrorException("Unknown Error");
        }

        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);

        OperationOutcome opOutcome = null;
        if (resource instanceof OperationOutcome) {
            opOutcome = (OperationOutcome) resource;
        } else {
            method.setResource(resource);
            opOutcome = new OperationOutcome();
        }

        method.setOperationOutcome(opOutcome);

        return method;
    }


}
