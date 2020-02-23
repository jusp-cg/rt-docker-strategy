package com.capgroup.dcip.webapi.security;

import com.capgroup.dcip.webapi.security.okta.OidcDiscoveryClient;
import com.capgroup.dcip.webapi.security.okta.OidcDiscoveryMetadata;
import com.capgroup.dcip.webapi.security.pojos.KeyPojo;
import com.capgroup.dcip.webapi.security.pojos.KeysResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
public class OpenIdConnectSigningKeyResolver extends SigningKeyResolverAdapter implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenIdConnectSigningKeyResolver.class);

    private Instant syncAfter = Instant.MIN;
    private ReadWriteLock lock = new ReentrantReadWriteLock(); //used to update syncAfter and cachedKeys from multiple threads
    private Map<String, Key> cachedKeys; // KeyId to PublicKey


    @Value("${oidc.authority}") //ex: https://capgroup-lab.oktapreview.com/oauth2/ausanedwu7KINzMqK0h7
    private String authority;

    @Value("#{T(java.time.Duration).parse('${oidc.metadataRefreshInterval:PT24H}')}") // default value: once a day
    private Duration refreshInterval;

    @Override
    public void afterPropertiesSet() {
        LOGGER.info("SigningKeyResolver created using authority {} and metadataRefreshInterval of {}", authority,
                refreshInterval);
        retrieveMetadataIfStale();
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        retrieveMetadataIfStale();

        String kid = header.getKeyId(); // the key we want to validate with is specified in the header
        LOGGER.trace("Incoming token was signed with keyID of {}", kid);
        if (!Strings.hasText(kid)) {
            LOGGER.warn("The incoming token was signed with an empty key id");
            throw new JwtException("Missing required 'kid' header param in JWT with claims: " + claims);
        }
        lock.readLock().lock();
        try {
            Key key = cachedKeys.get(kid);
            if (key == null) {
                throw new JwtException("No public key registered for kid: " + kid + ". JWT claims: " + claims);
            }
            return key;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void retrieveMetadataIfStale() {
        if (syncAfter.isAfter(Instant.now())) {
            LOGGER.trace("Not yet time to re-download metadata.");
            return;
        }
        lock.writeLock().lock();
        try {
            if (syncAfter.isBefore(Instant.now())) { //check again for anyone who was queued up while refresh was occurring, to avoid duplicate fetches
                LOGGER.info("Retrieving OIDC metadata using Okta SDK...");
                // use the Okta SDK to obtain the jwks_uri, which supplies the validation keys
                OidcDiscoveryClient client = new OidcDiscoveryClient(authority);
                OidcDiscoveryMetadata metadata = client.discover();
                LOGGER.debug("Parsed OIDC metadata. Keys URI is {}", metadata.getJwksUri());
                KeysResponse keysPojo = downloadKeys(metadata.getJwksUri());
                LOGGER.trace("Parsed OIDC keys.");

                //create a map of Key ID to PublicKey by using the modulus (n) and exponent (e) from Okta's response
                cachedKeys = parseResponsePojoIntoKeys(keysPojo);
                syncAfter = Instant.now().plus(refreshInterval);
                LOGGER.info("Finished retrieving metadata. {} keys obtained. Next sync at {}", cachedKeys.size(),
                        syncAfter);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Map<String, Key> parseResponsePojoIntoKeys(KeysResponse keysResponse) {
        return Arrays.stream(keysResponse.keys).collect(Collectors.toMap(KeyPojo::getKid, k -> {
            // take Okta metadata, specified in PEM format, to construct a PublicKey object.
            //MUST use the Apache Commons Base64 class, not the one built into Java, which will throw an exception
            BigInteger modulus = new BigInteger(1, Base64.decodeBase64(k.getN()));
            BigInteger publicExponent = new BigInteger(1, Base64.decodeBase64(k.getE()));
            PublicKey publicKey = null;
            try {
                publicKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, publicExponent));
                LOGGER.trace("PublicKey for kid {} constructed", k.getKid());
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                LOGGER.error("Security key error {}", e);
            }
            return publicKey;
        }));
    }

    private KeysResponse downloadKeys(String jwksUri) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(jwksUri, KeysResponse.class);
    }
}
