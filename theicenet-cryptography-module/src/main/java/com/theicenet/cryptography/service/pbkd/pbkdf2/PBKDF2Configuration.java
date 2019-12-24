package com.theicenet.cryptography.service.pbkd.pbkdf2;

public final class PBKDF2Configuration {
  private final String PBKDF2_WITH_HMAC = "PBKDF2WithHmac";

  private final String algorithm;
  private final Integer iterations;

  public PBKDF2Configuration(ShaAlgorithm shaAlgorithm, Integer iterations) {

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
