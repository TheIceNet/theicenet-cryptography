package com.theicenet.cryptography.service.symmetric.pbkd.argon2;

import javax.annotation.concurrent.Immutable;

@Immutable
final class Argon2Configuration {
  private final Argon2Type type;
  private final Argon2Version version;
  private final Integer iterations;
  private final Integer memoryPowOfTwo;
  private final Integer parallelism;

  Argon2Configuration(
      Argon2Type type,
      Argon2Version version,
      Integer iterations,
      Integer memoryPowOfTwo,
      Integer parallelism) {

    this.type = type;
    this.version = version;
    this.iterations = iterations;
    this.memoryPowOfTwo = memoryPowOfTwo;
    this.parallelism = parallelism;
  }

  Argon2Type getType() {
    return type;
  }

  Argon2Version getVersion() {
    return version;
  }

  Integer getIterations() {
    return iterations;
  }

  Integer getMemoryPowOfTwo() {
    return memoryPowOfTwo;
  }

  Integer getParallelism() {
    return parallelism;
  }
}
