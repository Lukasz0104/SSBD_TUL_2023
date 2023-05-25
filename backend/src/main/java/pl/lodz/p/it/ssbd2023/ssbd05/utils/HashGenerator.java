package pl.lodz.p.it.ssbd2023.ssbd05.utils;


import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.interceptor.Interceptors;
import jakarta.security.enterprise.identitystore.PasswordHash;
import pl.lodz.p.it.ssbd2023.ssbd05.interceptors.LoggerInterceptor;

@ApplicationScoped
@Interceptors(LoggerInterceptor.class)
public class HashGenerator implements PasswordHash {

    @Override
    public String generate(char[] chars) {
        return BCrypt.withDefaults().hashToString(10, chars);
    }

    @Override
    public boolean verify(char[] chars, String s) {
        return BCrypt.verifyer().verify(chars, s).verified;
    }
}
