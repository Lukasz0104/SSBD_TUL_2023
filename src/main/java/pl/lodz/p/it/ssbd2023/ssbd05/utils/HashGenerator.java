package pl.lodz.p.it.ssbd2023.ssbd05.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HashGenerator {

    public String generate(char[] chars) {
        return BCrypt.withDefaults().hashToString(10, chars);
    }
}
