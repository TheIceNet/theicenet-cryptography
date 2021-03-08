/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.theicenet.cryptography.acceptancetest.key.asymmetric.ecc.ecdsa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import com.theicenet.cryptography.key.asymmetric.AsymmetricKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Juan Fidalgo
 */
@SpringBootTest
class ECDSAKeyServiceIT {

  final int KEY_LENGTH_256_BITS = 256;

  @Autowired
  @Qualifier("ECDSAKey_secpXXXk1")
  AsymmetricKeyService ecdsaKeyService;

  @Test
  void producesECDSAKeyWhenGeneratingKey() {
    // When
    final var generatedKeyPair = ecdsaKeyService.generateKey(KEY_LENGTH_256_BITS);

    // Then
    assertThat(generatedKeyPair, is(notNullValue()));
  }
}