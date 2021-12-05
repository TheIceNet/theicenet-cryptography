/*
 * Copyright 2019-2021 the original author or authors.
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
package com.theicenet.cryptography.keyagreement.pake.srp.v6a;

import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_A;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_B;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_K;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_M1;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_M2;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_S;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_SESSION_KEY;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.EXPECTED_U;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.HASH_SHA_256;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.N;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.a;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6GenericTestingVectors.g;
import static com.theicenet.cryptography.util.ByteArraysUtil.toUnsignedByteArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.theicenet.cryptography.digest.DigestService;
import com.theicenet.cryptography.digest.JCADigestService;
import com.theicenet.cryptography.random.SecureRandomDataService;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @author Juan Fidalgo
 */
@ExtendWith(MockitoExtension.class)
class SRP6CommonUtilTest {

  final DigestService sha256Digest = new JCADigestService(HASH_SHA_256);

  @Mock
  SecureRandomDataService secureRandomDataService;

  @Test
  void throwsNullPointerExceptionWhenComputingKAndNullDigest() {
    // Given
    final DigestService NULL_DIGEST_SERVICE = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeK(NULL_DIGEST_SERVICE, N, g)); // When
  }

  @Test
  void throwsNullPointerExceptionWhenComputingKAndNullN() {
    // Given
    final BigInteger NULL_N = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeK(sha256Digest, NULL_N, g)); // When
  }

  @Test
  void throwsNullPointerExceptionWhenComputingKAndNullG() {
    // Given
    final BigInteger NULL_G = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeK(sha256Digest, N, NULL_G)); // When
  }

  @Test
  void producesNotNullWhenComputingK() {
    // When
    final var computedK = SRP6CommonUtil.computeK(sha256Digest, N, g);

    // Then
    assertThat(computedK, is(notNullValue()));
  }

  @Test
  void producesTheRightKWhenComputingK() {
    // When
    final var computedK = SRP6CommonUtil.computeK(sha256Digest, N, g);

    // Then
    assertThat(computedK, is(equalTo(EXPECTED_K)));
  }

  @Test
  void throwNullPointerExceptionWhenComputingUAndNullDigest() {
    // Given
    final DigestService NULL_DIGEST_SERVICE = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeU(NULL_DIGEST_SERVICE, N, EXPECTED_A, EXPECTED_B)); // When
  }

  @Test
  void throwNullPointerExceptionWhenComputingUAndNullN() {
    // Given
    final BigInteger NULL_N = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeU(sha256Digest, NULL_N, EXPECTED_A, EXPECTED_B)); // When
  }

  @Test
  void throwNullPointerExceptionWhenComputingUAndNullPublicValueA() {
    // Given
    final BigInteger NULL_PUBLIC_VALUE_A = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeU(sha256Digest, N, NULL_PUBLIC_VALUE_A, EXPECTED_B)); // When
  }

  @Test
  void throwNullPointerExceptionWhenComputingUAndNullPublicValueB() {
    // Given
    final BigInteger NULL_PUBLIC_VALUE_B = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeU(sha256Digest, N, EXPECTED_A, NULL_PUBLIC_VALUE_B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingUAndInvalidPublicValueA() {
    // Given
    final BigInteger INVALID_PUBLIC_VALUE_A = N.multiply(BigInteger.TEN);

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> SRP6CommonUtil.computeU(sha256Digest, N, INVALID_PUBLIC_VALUE_A, EXPECTED_B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingUAndInvalidPublicValueB() {
    // Given
    final BigInteger INVALID_PUBLIC_VALUE_B = N.multiply(BigInteger.TEN);

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> SRP6CommonUtil.computeU(sha256Digest, N, EXPECTED_A, INVALID_PUBLIC_VALUE_B)); // When
  }

  @Test
  void producesNotNullWhenComputingU() {
    // When
    final var computedU = SRP6CommonUtil.computeU(sha256Digest, N, EXPECTED_A, EXPECTED_B);

    // Then
    assertThat(computedU, is(notNullValue()));
  }

  @Test
  void producesTheRightUWhenComputingU() {
    // When
    final var computedU = SRP6CommonUtil.computeU(sha256Digest, N, EXPECTED_A, EXPECTED_B);

    // Then
    assertThat(computedU, is(equalTo(EXPECTED_U)));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM1AndNullDigest() {
    // Given
    final DigestService NULL_DIGEST_SERVICE = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM1(
                NULL_DIGEST_SERVICE,
                N,
                EXPECTED_A,
                EXPECTED_B,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM1AndNullN() {
    // Given
    final BigInteger NULL_N = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM1(
                sha256Digest,
                NULL_N,
                EXPECTED_A,
                EXPECTED_B,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM1AndNullPublicValueA() {
    // Given
    final BigInteger NULL_PUBLIC_VALUE_A = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () ->
            SRP6CommonUtil.computeM1(
                sha256Digest,
                N,
                NULL_PUBLIC_VALUE_A,
                EXPECTED_B,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM1AndNullPublicValueB() {
    // Given
    final BigInteger NULL_PUBLIC_VALUE_B = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM1(
                sha256Digest,
                N,
                EXPECTED_A,
                NULL_PUBLIC_VALUE_B,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM1AndNullS() {
    // Given
    final BigInteger NULL_S = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM1(
                sha256Digest,
                N,
                EXPECTED_A,
                EXPECTED_B,
                NULL_S));
  }

  @Test
  void producesNotNullWhenComputingM1() {
    // When
    final var computedM1 =
        SRP6CommonUtil.computeM1(
            sha256Digest,
            N, EXPECTED_A,
            EXPECTED_B,
            EXPECTED_S);

    // Then
    assertThat(computedM1, is(notNullValue()));
  }

  @Test
  void producesTheRightM1WhenComputingM1() {
    // When
    final var computedM1 =
        SRP6CommonUtil.computeM1(
            sha256Digest,
            N, EXPECTED_A,
            EXPECTED_B,
            EXPECTED_S);

    // Then
    assertThat(computedM1, is(equalTo(EXPECTED_M1)));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM2AndNullDigest() {
    // Given
    final DigestService NULL_DIGEST_SERVICE = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM2(
                NULL_DIGEST_SERVICE,
                N,
                EXPECTED_A,
                EXPECTED_M1,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM2AndNullN() {
    // Given
    final BigInteger NULL_N = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM2(
                sha256Digest,
                NULL_N,
                EXPECTED_A,
                EXPECTED_M1,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM2AndNullPublicValueA() {
    // Given
    final BigInteger NULL_PUBLIC_VALUE_A = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM2(
                sha256Digest,
                N,
                NULL_PUBLIC_VALUE_A,
                EXPECTED_M1,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM2AndNullM1() {
    // Given
    final BigInteger NULL_M1 = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM2(
                sha256Digest,
                N,
                EXPECTED_A,
                NULL_M1,
                EXPECTED_S));
  }

  @Test
  void throwNullPointerExceptionWhenComputingM2AndNullS() {
    // Given
    final BigInteger NULL_S = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> // When
            SRP6CommonUtil.computeM2(
                sha256Digest,
                N,
                EXPECTED_A,
                EXPECTED_M1,
                NULL_S));
  }

  @Test
  void producesNotNullWhenComputingM2() {
    // When
    final var computedM2 =
        SRP6CommonUtil.computeM2(
            sha256Digest,
            N,
            EXPECTED_A,
            EXPECTED_M1,
            EXPECTED_S);

    // Then
    assertThat(computedM2, is(notNullValue()));
  }

  @Test
  void producesTheRightResultWhenComputingM2() {
    // When
    final var computedM2 =
        SRP6CommonUtil.computeM2(
            sha256Digest,
            N,
            EXPECTED_A,
            EXPECTED_M1,
            EXPECTED_S);

    // Then
    assertThat(computedM2, is(equalTo(EXPECTED_M2)));
  }

  @Test
  void throwsNullPointerExceptionWhenComputingSessionKeyAndNullDigest() {
    // Given
    final DigestService NULL_DIGEST_SERVICE = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeSessionKey(NULL_DIGEST_SERVICE, N, EXPECTED_S)); // When
  }

  @Test
  void throwsNullPointerExceptionWhenComputingSessionKeyAndNullN() {
    // Given
    final BigInteger NULL_N = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeSessionKey(sha256Digest, NULL_N, EXPECTED_S)); // When
  }

  @Test
  void throwsNullPointerExceptionWhenComputingSessionKeyAndNullS() {
    // Given
    final BigInteger NULL_S = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.computeSessionKey(sha256Digest, N, NULL_S)); // When
  }

  @Test
  void producesNotNullWhenComputingSessionKey() {
    // When
    final var computedSessionKey = SRP6CommonUtil.computeSessionKey(sha256Digest, N, EXPECTED_S);

    // Then
    assertThat(computedSessionKey, is(notNullValue()));
  }
  
  @Test
  void producesTheRightResultWhenComputingSessionKey() {
    // When
    final var computedSessionKey = SRP6CommonUtil.computeSessionKey(sha256Digest, N, EXPECTED_S);

    // Then
    assertThat(computedSessionKey, is(equalTo(EXPECTED_SESSION_KEY)));
  }

  @Test
  void throwsNullPointerExceptionWhenGeneratingPrivateValueAndNullN() {
    // Given
    final BigInteger NULL_N = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.generatePrivateValue(NULL_N, secureRandomDataService)); // When
  }

  @Test
  void throwsNullPointerExceptionWhenGeneratingPrivateValueAndNullRandomDataService() {
    // Given
    final SecureRandomDataService NULL_RANDOM_DATA_SERVICE = null;

    // Then
    assertThrows(
        NullPointerException.class,
        () -> SRP6CommonUtil.generatePrivateValue(N, NULL_RANDOM_DATA_SERVICE)); // When
  }

  @Test
  void producesNotNullWhenGeneratingPrivateValue() {
    // Given
    when(secureRandomDataService.generateSecureRandomData(anyInt()))
        .thenReturn(toUnsignedByteArray(a));

    // When
    final var generatedPrivateValue =
        SRP6CommonUtil.generatePrivateValue(
            N,
            secureRandomDataService);

    // Then
    assertThat(generatedPrivateValue, is(notNullValue()));
  }

  @Test
  void producesValueWithLengthAtLeast256BitsWhenGeneratingPrivateValue() {
    // Given
    when(secureRandomDataService.generateSecureRandomData(anyInt()))
        .thenReturn(new byte[]{10})
        .thenReturn(new byte[]{100})
        .thenReturn(toUnsignedByteArray(a));

    // When
    final var generatedPrivateValue =
        SRP6CommonUtil.generatePrivateValue(N, secureRandomDataService);

    //Then
    assertThat(generatedPrivateValue.bitLength(), is(greaterThanOrEqualTo(256)));
  }

  @Test
  void iteratesUntilValueHasLengthAtLeast256BitsWhenGeneratingPrivateValue() {
    // Given
    when(secureRandomDataService.generateSecureRandomData(anyInt()))
        .thenReturn(new byte[]{10})
        .thenReturn(new byte[]{100})
        .thenReturn(toUnsignedByteArray(a));

    // When
    final var generatedPrivateValue =
        SRP6CommonUtil.generatePrivateValue(N, secureRandomDataService);

    //Then
    verify(secureRandomDataService, times(3)).generateSecureRandomData(anyInt());
  }

  @Test
  void producesTheRightResultWhenValidatingPublicValueAndValidPublicValue() {
    // Given
    final var VALID_PUBLIC_VALUE = N.multiply(BigInteger.TEN).add(BigInteger.ONE);

    // When
    final var validationResult = SRP6CommonUtil.isValidPublicValue(N, VALID_PUBLIC_VALUE);

    // Then
    assertThat(validationResult, is(true));
  }

  @Test
  void producesTheRightResultWhenValidatingPublicValueAndInvalidPublicValue() {
    // Given
    final var INVALID_PUBLIC_VALUE = N.multiply(BigInteger.TEN);

    // When
    final var validationResult = SRP6CommonUtil.isValidPublicValue(N, INVALID_PUBLIC_VALUE);

    // Then
    assertThat(validationResult, is(false));
  }

  @Test
  void producesNotNullWhenCalculatingPadLength() {
    // Given
    final var ANY_VALUE = new BigInteger("1234567890");;

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(ANY_VALUE);

    // Then
    assertThat(calculatedPadLength, is(notNullValue()));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_0_Bits() {
    // Given
    final var _0_BITS_N = new BigInteger("0");;

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_0_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(0)));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_1_Bits() {
    // Given
    final var _1_BITS_N = new BigInteger("1");

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_1_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(1)));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_2_Bits() {
    // Given
    final var _2_BITS_N = new BigInteger("2");

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_2_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(1)));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_7_Bits() {
    // Given
    final var _7_BITS_N = new BigInteger("65");

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_7_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(1)));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_8_Bits() {
    // Given
    final var _8_BITS_N = new BigInteger("129");

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_8_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(1)));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_9_Bits() {
    // Given
    final var _9_BITS_N = new BigInteger("257");

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_9_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(2)));
  }

  @Test
  void producesTheRightPadLengthWhenCalculatingPadLengthAndNLengthIs_47_Bits() {
    // Given
    final var _47_BITS_N = new BigInteger("123456789012345");

    // When
    final var calculatedPadLength = SRP6CommonUtil.calculatePadLength(_47_BITS_N);

    // Then
    assertThat(calculatedPadLength, is(equalTo(6)));
  }
}