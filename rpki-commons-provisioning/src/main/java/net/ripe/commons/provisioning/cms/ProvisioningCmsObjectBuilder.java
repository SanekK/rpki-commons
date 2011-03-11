package net.ripe.commons.provisioning.cms;

import net.ripe.commons.certification.x509cert.X509CertificateUtil;
import org.apache.commons.lang.Validate;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.joda.time.DateTimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;

public class ProvisioningCmsObjectBuilder {

    private static final String DIGEST_ALGORITHM_OID = CMSSignedDataGenerator.DIGEST_SHA256;

    private static final String CONTENT_TYPE = "1.2.840.113549.1.9.16.1.28";

    private X509Certificate certificate;

    private X509CRL crl;

    private String signatureProvider;

    private String payloadContent;


    public ProvisioningCmsObjectBuilder withCertificate(X509Certificate certificate) {
        this.certificate = certificate;
        return this;
    }

    public ProvisioningCmsObjectBuilder withCrl(X509CRL crl) {
        this.crl = crl;
        return this;
    }

    public ProvisioningCmsObjectBuilder withSignatureProvider(String signatureProvider) {
        this.signatureProvider = signatureProvider;
        return this;
    }

    public ProvisioningCmsObjectBuilder withPayloadContent(String payloadContent) {
        this.payloadContent = payloadContent;
        return this;
    }


    public ProvisioningCmsObject build(PrivateKey privateKey) {
        Validate.notNull(certificate, "certificate is required");
        Validate.notNull(crl, "crl is required");
        Validate.notEmpty(payloadContent, "Payload content is required");

        ProvisioningCmsObjectParser parser = new ProvisioningCmsObjectParser();
        parser.parseCms(generateCms(privateKey, getMessageContent()));
        return parser.getProvisioningCmsObject();
    }

    private byte[] generateCms(PrivateKey privateKey, ASN1Encodable encodableContent) {
        try {
            return doGenerate(privateKey, encodableContent);
        } catch (NoSuchAlgorithmException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        } catch (NoSuchProviderException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        } catch (CMSException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        } catch (IOException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        } catch (CertStoreException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        } catch (CertificateEncodingException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        }
    }

    private byte[] doGenerate(PrivateKey privateKey, ASN1Encodable encodableContent) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertStoreException, CMSException, NoSuchProviderException, IOException, CertificateEncodingException {
        CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
        addCertificateAndCrl(generator);
        generator.addSigner(privateKey, X509CertificateUtil.getSubjectKeyIdentifier(certificate), DIGEST_ALGORITHM_OID, createSignedAttributes(), null);

        byte[] content = encode(encodableContent);
        CMSSignedData data = generator.generate(CONTENT_TYPE, new CMSProcessableByteArray(content), true, signatureProvider);

        return data.getEncoded();
    }

    private void addCertificateAndCrl(CMSSignedDataGenerator generator) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, CertStoreException, CMSException {
        CollectionCertStoreParameters certStoreParameters = new CollectionCertStoreParameters(Arrays.asList(certificate, crl));
        CertStore certStore = CertStore.getInstance("Collection", certStoreParameters);
        generator.addCertificatesAndCRLs(certStore);
    }

    private AttributeTable createSignedAttributes() {
        Hashtable<DERObjectIdentifier, Attribute> attributes = new Hashtable<DERObjectIdentifier, Attribute>(); //NOPMD - ReplaceHashtableWithMap
        Attribute signingTimeAttribute = new Attribute(CMSAttributes.signingTime, new DERSet(new Time(new Date(DateTimeUtils.currentTimeMillis()))));
        attributes.put(CMSAttributes.signingTime, signingTimeAttribute);
        return new AttributeTable(attributes);
    }

    private ASN1Encodable getMessageContent() {
        return new DEROctetString(payloadContent.getBytes());
    }

    private byte[] encode(ASN1Encodable value) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DEROutputStream derOutputStream = new DEROutputStream(byteArrayOutputStream);
            derOutputStream.writeObject(value);
            derOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ProvisioningCmsObjectBuilderException(e);
        }
    }
}