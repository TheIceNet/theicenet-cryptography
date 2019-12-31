package com.theicenet.cryptography.service.asymmetric.rsa;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface RSASignatureService {

  byte[] sign(PrivateKey privateKey, byte[] content);

  boolean verify(PublicKey publicKey, byte[] content, byte[] signature);
}
