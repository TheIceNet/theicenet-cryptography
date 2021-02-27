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
package com.theicenet.cryptography.signature.ecdsa;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;

import com.theicenet.cryptography.key.asymmetric.ecc.ECCKeyAlgorithm;
import com.theicenet.cryptography.signature.SignatureService;
import com.theicenet.cryptography.util.HexUtil;
import com.theicenet.cryptography.test.support.RunnerUtil;
import com.theicenet.cryptography.util.CryptographyProviderUtil;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author Juan Fidalgo
 */
class JCAECDSASignatureServiceTest {
  final ECCKeyAlgorithm ECDSA = ECCKeyAlgorithm.ECDSA;

  final byte[] CONTENT =
      "Content to be signed to test correctness of the ECDSA sign implementation."
          .getBytes(StandardCharsets.UTF_8);

  final byte[] DIFFERENT_CONTENT =
      "Totally different content to test that verify detects properly when signature is not correct."
          .getBytes(StandardCharsets.UTF_8);

  final byte[] ECDSA_PUBLIC_KEY_BRAINPOOLP256R1_BYTE_ARRAY =
      HexUtil.decodeHex(
          "305a301406072a8648ce3d020106092b240303020801010703420004276492e8990f82e5b"
              + "31d4931a35591756eb24db1534fae485e0e62a2a2188c6da2896928c35032e1b664"
              + "125225559865b03bf436fe1ccf368443bb7397dfc39e");

  final byte[] ECDSA_PRIVATE_KEY_BRAINPOOLP256R1_BYTE_ARRAY =
      HexUtil.decodeHex(
          "308188020100301406072a8648ce3d020106092b2403030208010107046d306b0201010420"
              + "824fb7361bcbdeea14011309fc016cac8180ce62fffa8e7e677646ac961ccfb4a144"
              + "03420004276492e8990f82e5b31d4931a35591756eb24db1534fae485e0e62a2a218"
              + "8c6da2896928c35032e1b664125225559865b03bf436fe1ccf368443bb7397dfc39e");

  final PublicKey ECDSA_PUBLIC_KEY_BRAINPOOLP256R1;
  final PrivateKey ECDSA_PRIVATE_KEY_BRAINPOOLP256R1;

  final byte[] SIGNATURE_SHA1_WITH_ECDSA =
      HexUtil.decodeHex(
          "304402206a2d12c6d68a10d93226fd858217077ce9eaa3c0a46ca6f8d89d411f5b69d865022060"
              + "865ee94b85228f4a19e492817d633717bb9a8fb9b78ecd67365918c1050848");

  final byte[] SIGNATURE_SHA224_WITH_ECDSA =
      HexUtil.decodeHex(
          "30440220459227e4286b0d68a6e93e0c0cb91b660e7e88c8397860c63607640fd0e5273502204b"
              + "4bf0d0c3a7b7a1a1cd47dc2b8367ac4ecad8a59f5ed3362d716a8058b7bc77");

  final byte[] SIGNATURE_SHA256_WITH_ECDSA =
      HexUtil.decodeHex(
          "304402202be4286aa2daf28ef992e52f360888987df981da2495553c49510358d84b6198022078f"
              + "bc3d6a0037c682454e908f463997a094222687af9232204f05d0001951291");

  JCAECDSASignatureServiceTest() throws Exception {
    // Bouncy Castle is required for ECDSA key factory
    CryptographyProviderUtil.addBouncyCastleCryptographyProvider();

    final var keyFactory = KeyFactory.getInstance(ECDSA.toString());

    final var x509EncodedKeySpec = new X509EncodedKeySpec(
        ECDSA_PUBLIC_KEY_BRAINPOOLP256R1_BYTE_ARRAY);
    ECDSA_PUBLIC_KEY_BRAINPOOLP256R1 = keyFactory.generatePublic(x509EncodedKeySpec);

    final var pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
        ECDSA_PRIVATE_KEY_BRAINPOOLP256R1_BYTE_ARRAY);
    ECDSA_PRIVATE_KEY_BRAINPOOLP256R1 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producesNotNullWhenSigningByteArray(ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    // When
    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // Then
    assertThat(signature, is(notNullValue()));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producesNotNullWhenSigningByteStream(ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    // When
    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT));

    // Then
    assertThat(signature, is(notNullValue()));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producesRightSizeWhenSigningByteArray(ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    // When
    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // Then
    assertThat( // For a curve between 160 and 512 bits key the signature size should be between 68 and 72 bytes
        signature.length,
        is(both(greaterThanOrEqualTo(68)).and(lessThanOrEqualTo(72))));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producesRightSizeWhenSigningStream(ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    // When
    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT));

    // Then
    assertThat( // For a curve between 160 and 512 bits key the signature size should be between 68 and 72 bytes
        signature.length,
        is(both(greaterThanOrEqualTo(68)).and(lessThanOrEqualTo(72))));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producesSignatureDifferentToClearContentWhenSigningByteArray(ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    // When
    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // Then
    assertThat(signature, is(not(equalTo(CONTENT))));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producesSignatureDifferentToClearContentWhenSigningStream(ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    // When
    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT));

    // Then
    assertThat(signature, is(not(equalTo(CONTENT))));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producedSignatureVerifiesToTrueWhenVerifyingByteArrayAndSignatureCorrespondsWithContent(
      ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            CONTENT,
            signature);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void producedSignatureVerifiesToTrueWhenVerifyingStreamAndSignatureCorrespondsWithContent(
      ECDSASignatureAlgorithm algorithm) {
    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT),
            signature);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void signatureVerifiesToFalseWhenVerifyingByteArrayAndSignatureDoesNotCorrespondsWithContent(
      ECDSASignatureAlgorithm algorithm) {

    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            DIFFERENT_CONTENT,
            signature);

    // Then
    assertThat(verifyingResult, is(equalTo(false)));
  }

  @ParameterizedTest
  @EnumSource(ECDSASignatureAlgorithm.class)
  void signatureVerifiesToFalseWhenVerifyingStreamAndSignatureDoesNotCorrespondsWithContent(
      ECDSASignatureAlgorithm algorithm) {

    // Given
    final SignatureService ecdsaSignatureService = new JCAECDSASignatureService(algorithm);

    final var signature =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            CONTENT);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(DIFFERENT_CONTENT),
            signature);

    // Then
    assertThat(verifyingResult, is(equalTo(false)));
  }

  @Test
  void verifiesProperlyWhenVerifyingByteArrayWithSha1WithECDSA() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA1withECDSA);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            CONTENT,
            SIGNATURE_SHA1_WITH_ECDSA);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingStreamWithSha1WithECDSA() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA1withECDSA);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT),
            SIGNATURE_SHA1_WITH_ECDSA);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingByteArrayWithSha224WithECDSA() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA224withECDSA);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            CONTENT,
            SIGNATURE_SHA224_WITH_ECDSA);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingStreamWithSha224WithECDSA() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA224withECDSA);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT),
            SIGNATURE_SHA224_WITH_ECDSA);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingByteArrayWithSha256WithECDSA() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            CONTENT,
            SIGNATURE_SHA256_WITH_ECDSA);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingStreamWithSha256WithECDSA() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    // When
    final var verifyingResult =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT),
            SIGNATURE_SHA256_WITH_ECDSA);

    // Then
    assertThat(verifyingResult, is(equalTo(true)));
  }

  @Test
  void producesDifferentButValidSignaturesWhenSigningTwoConsecutiveTimesTheSameContentWithTheSamePrivateKeyForByteArray() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    // When
    final var signature_1 = ecdsaSignatureService.sign(ECDSA_PRIVATE_KEY_BRAINPOOLP256R1, CONTENT);
    final var signature_2 = ecdsaSignatureService.sign(ECDSA_PRIVATE_KEY_BRAINPOOLP256R1, CONTENT);

    // Then
    assertThat(signature_1, is(not(equalTo(signature_2))));
    assertThat(
        ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature_1),
        is(equalTo(true)));
    assertThat(
        ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature_2),
        is(equalTo(true)));
  }

  @Test
  void producesDifferentButValidSignaturesWhenSigningTwoConsecutiveTimesTheSameContentWithTheSamePrivateKeyForStream() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    // When
    final var signature_1 =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT));

    final var signature_2 =
        ecdsaSignatureService.sign(
            ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT));

    // Then
    assertThat(signature_1, is(not(equalTo(signature_2))));
    assertThat(
        ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature_1),
        is(equalTo(true)));
    assertThat(
        ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature_2),
        is(equalTo(true)));
  }

  @Test
  void producesDifferentButValidSignaturesWhenSigningManyConsecutiveTimesTheSameContentWithTheSamePrivateKeyForByteArray() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _100 = 100;

    // When
    final var generatedSignaturesSet =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () ->
                HexUtil.encodeHex(
                    ecdsaSignatureService.sign(ECDSA_PRIVATE_KEY_BRAINPOOLP256R1, CONTENT)));

    // Then
    assertThat(generatedSignaturesSet, hasSize(_100));
    generatedSignaturesSet.stream()
        .map(HexUtil::decodeHex)
        .map(signature ->
            ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature))
        .forEach(signatureValidation -> assertThat(signatureValidation, is(equalTo(true))));
  }

  @Test
  void producesDifferentButValidSignaturesWhenSigningManyConsecutiveTimesTheSameContentWithTheSamePrivateKeyForStream() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _100 = 100;

    // When
    final var generatedSignaturesSet =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () ->
                HexUtil.encodeHex(
                    ecdsaSignatureService.sign(
                        ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
                        new ByteArrayInputStream(CONTENT))));

    // Then
    assertThat(generatedSignaturesSet, hasSize(_100));
    generatedSignaturesSet.stream()
        .map(HexUtil::decodeHex)
        .map(signature ->
            ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature))
        .forEach(signatureValidation -> assertThat(signatureValidation, is(equalTo(true))));
  }

  @Test
  void producesDifferentButValidSignaturesWhenSigningConcurrentlyTheSameContentWithTheSamePrivateKeyForByteArray() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _500 = 500;

    // When
    final var generatedSignaturesSet =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () ->
                HexUtil.encodeHex(
                    ecdsaSignatureService.sign(ECDSA_PRIVATE_KEY_BRAINPOOLP256R1, CONTENT)));

    // Then
    assertThat(generatedSignaturesSet, hasSize(_500));
    generatedSignaturesSet.stream()
        .map(HexUtil::decodeHex)
        .map(signature ->
            ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature))
        .forEach(signatureValidation -> assertThat(signatureValidation, is(equalTo(true))));
  }

  @Test
  void producesDifferentButValidSignaturesWhenSigningConcurrentlyTheSameContentWithTheSamePrivateKeyForStream() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _500 = 500;

    // When
    final var generatedSignaturesSet =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () ->
                HexUtil.encodeHex(
                    ecdsaSignatureService.sign(
                        ECDSA_PRIVATE_KEY_BRAINPOOLP256R1,
                        new ByteArrayInputStream(CONTENT))));

    // Then
    assertThat(generatedSignaturesSet, hasSize(_500));
    generatedSignaturesSet.stream()
        .map(HexUtil::decodeHex)
        .map(signature ->
            ecdsaSignatureService.verify(ECDSA_PUBLIC_KEY_BRAINPOOLP256R1, CONTENT, signature))
        .forEach(signatureValidation -> assertThat(signatureValidation, is(equalTo(true))));
  }

  @Test
  void verifiesProperlyWhenVerifyingTwoConsecutiveTimesTheSameSignatureForByteArray() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    // When
    final var verifyingResult_1 =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            CONTENT,
            SIGNATURE_SHA256_WITH_ECDSA);

    final var verifyingResult_2 =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            CONTENT,
            SIGNATURE_SHA256_WITH_ECDSA);

    // Then
    assertThat(verifyingResult_1, is(equalTo(true)));
    assertThat(verifyingResult_2, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingTwoConsecutiveTimesTheSameSignatureForStream() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    // When
    final var verifyingResult_1 =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT),
            SIGNATURE_SHA256_WITH_ECDSA);

    final var verifyingResult_2 =
        ecdsaSignatureService.verify(
            ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
            new ByteArrayInputStream(CONTENT),
            SIGNATURE_SHA256_WITH_ECDSA);

    // Then
    assertThat(verifyingResult_1, is(equalTo(true)));
    assertThat(verifyingResult_2, is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingManyConsecutiveTimesTheSameSignatureForByteArray() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _100 = 100;

    // When
    final var verifyingResultsSet =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () ->
                ecdsaSignatureService.verify(
                    ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
                    CONTENT,
                    SIGNATURE_SHA256_WITH_ECDSA));

    // Then
    assertThat(verifyingResultsSet, hasSize(1));
    assertThat(verifyingResultsSet.iterator().next(), is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingManyConsecutiveTimesTheSameSignatureForStream() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _100 = 100;

    // When
    final var verifyingResultsSet =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () ->
                ecdsaSignatureService.verify(
                    ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
                    new ByteArrayInputStream(CONTENT),
                    SIGNATURE_SHA256_WITH_ECDSA));

    // Then
    assertThat(verifyingResultsSet, hasSize(1));
    assertThat(verifyingResultsSet.iterator().next(), is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingConcurrentlyTheSameSignatureForByteArray() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _500 = 500;

    // When
    final var verifyingResultsSet =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () ->
                ecdsaSignatureService.verify(
                    ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
                    CONTENT,
                    SIGNATURE_SHA256_WITH_ECDSA));

    // Then
    assertThat(verifyingResultsSet, hasSize(1));
    assertThat(verifyingResultsSet.iterator().next(), is(equalTo(true)));
  }

  @Test
  void verifiesProperlyWhenVerifyingConcurrentlyTheSameSignatureForStream() {
    // Given
    final SignatureService ecdsaSignatureService =
        new JCAECDSASignatureService(ECDSASignatureAlgorithm.SHA256withECDSA);

    final var _500 = 500;

    // When
    final var verifyingResultsSet =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () ->
                ecdsaSignatureService.verify(
                    ECDSA_PUBLIC_KEY_BRAINPOOLP256R1,
                    new ByteArrayInputStream(CONTENT),
                    SIGNATURE_SHA256_WITH_ECDSA));

    // Then
    assertThat(verifyingResultsSet, hasSize(1));
    assertThat(verifyingResultsSet.iterator().next(), is(equalTo(true)));
  }
}