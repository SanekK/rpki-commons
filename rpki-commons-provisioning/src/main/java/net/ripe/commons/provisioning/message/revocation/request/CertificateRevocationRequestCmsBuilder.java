package net.ripe.commons.provisioning.message.revocation.request;

import net.ripe.certification.client.xml.XStreamXmlSerializer;
import net.ripe.commons.provisioning.message.revocation.AbstractCertificateRevocationCmsBuilder;
import net.ripe.commons.provisioning.message.revocation.CertificateRevocationKeyElement;

public class CertificateRevocationRequestCmsBuilder extends AbstractCertificateRevocationCmsBuilder {
    
    private static final XStreamXmlSerializer<CertificateRevocationRequestPayload> SERIALIZER = new CertificateRevocationRequestPayloadSerializerBuilder().build();

    @Override
    protected String serializePayloadWrapper(String sender, String recipient) {
        CertificateRevocationKeyElement payload = new CertificateRevocationKeyElement(className, publicKey);
        CertificateRevocationRequestPayload wrapper = new CertificateRevocationRequestPayload(sender, recipient, payload);
        return SERIALIZER.serialize(wrapper);
    }
}
