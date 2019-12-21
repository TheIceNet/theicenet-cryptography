package com.theicenet.cryptography.service.symmetric.pbkd.pbkdf2;

import javax.annotation.concurrent.Immutable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Immutable
@Component
final class PBKDF2Configuration {
  private final String PBKDF2_WITH_HMAC = "PBKDF2WithHmac";

  private final String algorithm;
  private final Integer iterations;

  PBKDF2Configuration(
      @Value("${cryptography.keyDerivationFunction.pbkdF2WithHmacSHA.shaAlgorithm}") ShaAlgorithm shaAlgorithm,
      @Value("${cryptography.keyDerivationFunction.pbkdF2WithHmacSHA.iterations}") Integer iterations) {

    this.algorithm = String.format("%s%s", PBKDF2_WITH_HMAC, shaAlgorithm.toString());
    this.iterations = iterations;
  }

  String getAlgorithm() {
    return algorithm;
  }

  Integer getIterations() {
    return iterations;
  }
}
