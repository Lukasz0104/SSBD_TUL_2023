package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.inject.Inject;
import pl.lodz.p.it.ssbd2023.ssbd05.exceptions.mapper.PayloadProcessingExceptionMapper;

import java.text.ParseException;
import java.util.logging.Logger;

public class JwsProvider {
    private static final Logger LOGGER = Logger.getLogger(PayloadProcessingExceptionMapper.class.getName());
    @Inject
    private Properties properties;

    public String signPayload(String payload) {
        try {
            JWSSigner signer = new MACSigner(properties.getJwsSecret());
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (JOSEException je) {
            LOGGER.severe(je.getMessage() + " " + je.getCause().getMessage());
            return "";
        }
    }

    public boolean verify(String payload) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(payload);
        JWSVerifier verifier = new MACVerifier(properties.getJwsSecret());
        return jwsObject.verify(verifier);
    }
}