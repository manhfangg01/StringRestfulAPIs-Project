package vn.hoidanit.jobhunter.util;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.util.Base64;

@Service
public class SecurityUtil {

    public void createToken(Authentication authentication) {

    }

}
