package com.theicenet.cryptography.service.asymmetric.rsa;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface RSACryptographyService {

  byte[] encrypt(RSAPadding padding, PublicKey publicKey, byte[] clearContent);

  byte[] decrypt(RSAPadding padding, PrivateKey privateKey, byte[] encryptedContent);
}
