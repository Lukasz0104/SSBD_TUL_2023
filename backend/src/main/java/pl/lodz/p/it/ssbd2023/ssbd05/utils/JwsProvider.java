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

import java.text.ParseException;

public class JwsProvider {


    @Inject
    private Properties properties;

    private String secret = properties.getJwsSecret();

    public String generateJws(String payload) throws JOSEException {
        JWSSigner signer = new MACSigner(secret);
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(payload));
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    public boolean verify(String payload) throws ParseException, JOSEException {
        JWSObject jwsObject = JWSObject.parse(payload);
        JWSVerifier verifier = new MACVerifier(secret);
        return jwsObject.verify(verifier);
    }
}