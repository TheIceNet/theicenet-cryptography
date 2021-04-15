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
package com.theicenet.cryptography.key.asymmetric.rsa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.theicenet.cryptography.key.asymmetric.AsymmetricKeyService;
import com.theicenet.cryptography.random.JCASecureRandomDataService;
import com.theicenet.cryptography.random.SecureRandomAlgorithm;
import com.theicenet.cryptography.random.SecureRandomDataService;
import com.theicenet.cryptography.test.support.HexUtil;
import com.theicenet.cryptography.test.support.RunnerUtil;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Juan Fidalgo
 */
class JCARSAKeyServiceTest {

  final String RSA = "RSA";
  final String X_509 = "X.509";
  final String PKCS_8 = "PKCS#8";
  final int KEY_LENGTH_1024_BITS = 1024;
  final int KEY_LENGTH_2048_BITS = 2048;

  final SecureRandomDataService secureRandomDataService =
      new JCASecureRandomDataService(SecureRandomAlgorithm.DEFAULT); // This is not mocked, because JCARSAKeyService must use the SecureRandom embedded in SecureRandomDataService, so a real instance is created and the component is fully tested. SecureRandomDataService acts only as a container for the library's SecureRandom

  AsymmetricKeyService rsaKeyService;

  @BeforeEach
  void setUp() {
    rsaKeyService = new JCARSAKeyService(secureRandomDataService);
  }

  @Test
  void throwsIllegalArgumentExceptionWhenGeneratingKeyAndInvalidKeyLength() {
    // Given
    final var KEY_LENGTH_MINUS_ONE = -1;

    // Then throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () ->
      rsaKeyService.generateKey(KEY_LENGTH_MINUS_ONE)); // When generating key and invalid key length
  }

  @Test
  void producesNotNullKeyPairWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair, is(notNullValue()));
  }

  @Test
  void producesNotNullPublicKeyWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPublic(), is(notNullValue()));
  }

  @Test
  void producesNotNullPrivateKeyWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPrivate(), is(notNullValue()));
  }

  @Test
  void producesPublicKeyWithRSAAlgorithmWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPublic().getAlgorithm(), is(equalTo(RSA)));
  }

  @Test
  void producesPrivateKeyWithRSAAlgorithmWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPrivate().getAlgorithm(), is(equalTo(RSA)));
  }

  @Test
  void producesPublicKeyWithX509FormatWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPublic().getFormat(), is(equalTo(X_509)));
  }

  @Test
  void producesPrivateKeyWithPKCS8FormatWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPrivate().getFormat(), is(equalTo(PKCS_8)));
  }

  @Test
  void producesPublicKeyWithContentWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPublic().getEncoded(), is(notNullValue()));
  }

  @Test
  void producesPrivateKeyWithContentWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPrivate().getEncoded(), is(notNullValue()));
  }

  @Test
  void producesPublicKeyWithNonEmptyContentWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPublic().getEncoded().length, is(greaterThan(0)));
  }

  @Test
  void producesPrivateKeyWithNonEmptyContentWhenGeneratingKey() {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then
    assertThat(generatedKeyPair.getPrivate().getEncoded().length, is(greaterThan(0)));
  }

  @ParameterizedTest
  @ValueSource(ints = {KEY_LENGTH_1024_BITS, KEY_LENGTH_2048_BITS})
  void producesPublicKeyWithTheRightModulusLengthWhenGeneratingKey(int keyLength) throws Exception {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(keyLength);

    // Then
    final var keyFactory = KeyFactory.getInstance(RSA);
    final var rsaPublicKeySpec = keyFactory.getKeySpec(generatedKeyPair.getPublic(), RSAPublicKeySpec.class);

    assertThat(rsaPublicKeySpec.getModulus().bitLength(), is(equalTo(keyLength)));
  }

  @ParameterizedTest
  @ValueSource(ints = {KEY_LENGTH_1024_BITS, KEY_LENGTH_2048_BITS})
  void producesPrivateKeyWithTheRightModulusLengthWhenGeneratingKey(int keyLength) throws Exception {
    // When
    final var generatedKeyPair = rsaKeyService.generateKey(keyLength);

    // Then
    final var keyFactory = KeyFactory.getInstance(RSA);
    final var rsaPrivateKeySpec = keyFactory.getKeySpec(generatedKeyPair.getPrivate(), RSAPrivateKeySpec.class);

    assertThat(rsaPrivateKeySpec.getModulus().bitLength(), is(equalTo(keyLength)));
  }

  @Test
  void producesDifferentPublicKeysWhenGeneratingTwoConsecutiveKeysWithTheSameLength() {
    // When generating two consecutive key pairs with the same length
    final var generatedKeyPair_1 = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);
    final var generatedKeyPair_2 = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then the generated public keys are different
    assertThat(
        generatedKeyPair_1.getPublic().getEncoded(),
        is(not(equalTo(
            generatedKeyPair_2.getPublic().getEncoded()))));
  }

  @Test
  void producesDifferentPrivateKeysWhenGeneratingTwoConsecutiveKeysWithTheSameLength() {
    // When generating two consecutive key pairs with the same length
    final var generatedKeyPair_1 = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);
    final var generatedKeyPair_2 = rsaKeyService.generateKey(KEY_LENGTH_1024_BITS);

    // Then the generated private keys are different
    assertThat(
        generatedKeyPair_1.getPrivate().getEncoded(),
        is(not(equalTo(
            generatedKeyPair_2.getPrivate().getEncoded()))));
  }

  @Test
  void producesDifferentPublicKeysWhenGeneratingManyConsecutiveKeysWithTheSameLength() {
    // Given
    final var _100 = 100;

    // When generating consecutive key pairs with the same length
    final var generatedPublicKeysSet =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () ->
                HexUtil.encodeHex(
                    rsaKeyService
                        .generateKey(KEY_LENGTH_1024_BITS)
                        .getPublic()
                        .getEncoded()));

    // Then all public keys have been generated and all them are different
    assertThat(generatedPublicKeysSet, hasSize(_100));
  }

  @Test
  void producesDifferentPrivateKeysWhenGeneratingManyConsecutiveKeysWithTheSameLength() {
    // Given
    final var _100 = 100;

    // When generating consecutive key pairs with the same length
    final var generatedPrivateKeysSet =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () ->
                HexUtil.encodeHex(
                    rsaKeyService
                        .generateKey(KEY_LENGTH_1024_BITS)
                        .getPrivate()
                        .getEncoded()));

    // Then all private key have been generated and all them are different
    assertThat(generatedPrivateKeysSet, hasSize(_100));
  }

  @Test
  void producesDifferentPublicKeysWhenGeneratingConcurrentlyManyKeysWithTheSameLength() throws Exception {
    // Given
    final var _500 = 500;

    // When generating concurrently at the same time key pairs with the same length
    final var generatedPublicKeysSet =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () ->
                HexUtil.encodeHex(
                    rsaKeyService
                        .generateKey(KEY_LENGTH_1024_BITS)
                        .getPublic()
                        .getEncoded()));

    // When generating concurrently at the same time key pairs with the same length
    assertThat(generatedPublicKeysSet, hasSize(_500));
  }

  @Test
  void producesDifferentPrivateKeysWhenGeneratingConcurrentlyManyKeysWithTheSameLength() throws Exception {
    // Given
    final var _500 = 500;

    // When generating concurrently at the same time key pairs with the same length
    final var generatedPrivateKeysSet =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () ->
                HexUtil.encodeHex(
                    rsaKeyService
                        .generateKey(KEY_LENGTH_1024_BITS)
                        .getPrivate()
                        .getEncoded()));

    // Then all private keys have been generated and all them are different
    assertThat(generatedPrivateKeysSet, hasSize(_500));
  }
}