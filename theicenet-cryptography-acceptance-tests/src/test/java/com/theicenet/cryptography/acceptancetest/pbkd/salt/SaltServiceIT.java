package com.theicenet.cryptography.acceptancetest.pbkd.salt;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import com.theicenet.cryptography.pbkd.salt.SaltService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SaltServiceIT {

  final int SALT_LENGTH_128_BYTES = 128;

  @Autowired
  SaltService saltService;

  @Test
  void producesSaltWhenGeneratingRandom() {
    // When
    final var generatedSalt = saltService.generateRandom(SALT_LENGTH_128_BYTES);

    // Then
    assertThat(generatedSalt, is(notNullValue()));
  }
}