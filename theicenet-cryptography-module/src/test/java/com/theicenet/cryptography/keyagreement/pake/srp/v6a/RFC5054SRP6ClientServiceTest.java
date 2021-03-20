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

import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.ByteArraysUtil.toBigInteger;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.ByteArraysUtil.toUnsignedByteArray;
import static com.theicenet.cryptography.keyagreement.pake.srp.v6a.SRP6ClientUtil.computeA;
import static com.theicenet.cryptography.test.support.HexUtil.encodeHex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.theicenet.cryptography.digest.DigestAlgorithm;
import com.theicenet.cryptography.keyagreement.SRP6ClientService;
import com.theicenet.cryptography.random.JCASecureRandomDataService;
import com.theicenet.cryptography.test.support.HexUtil;
import com.theicenet.cryptography.test.support.RunnerUtil;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Juan Fidalgo
 */
class RFC5054SRP6ClientServiceTest {

  final SRP6StandardGroup SG_2048 = SRP6GenericTestingVectors.SG_2048;
  final DigestAlgorithm HASH_SHA_256 = SRP6GenericTestingVectors.HASH_SHA_256;

  final BigInteger N = SG_2048.getN();
  final BigInteger g = SG_2048.getG();

  final byte[] IDENTITY = SRP6GenericTestingVectors.IDENTITY;
  final byte[] PASSWORD = SRP6GenericTestingVectors.PASSWORD;

  final byte[] SALT = SRP6GenericTestingVectors.SALT;

  final byte[] a = toUnsignedByteArray(SRP6GenericTestingVectors.a);
  final byte[] A = toUnsignedByteArray(SRP6GenericTestingVectors.EXPECTED_A);
  final byte[] B = toUnsignedByteArray(SRP6GenericTestingVectors.EXPECTED_B);
  final byte[] S = toUnsignedByteArray(SRP6GenericTestingVectors.EXPECTED_S);
  final byte[] M1 = toUnsignedByteArray(SRP6GenericTestingVectors.EXPECTED_M1);
  final byte[] M2 = toUnsignedByteArray(SRP6GenericTestingVectors.EXPECTED_M2);

  final byte[] SESSION_KEY = toUnsignedByteArray(SRP6GenericTestingVectors.EXPECTED_SESSION_KEY);

  SRP6ClientService srp6ClientService;

  @BeforeEach
  void setUp() {
    srp6ClientService =
        new RFC5054SRP6ClientService(
            SG_2048,
            HASH_SHA_256,
            new JCASecureRandomDataService(new SecureRandom()));
  }

  @Test
  void producesNotNullWhenComputingValuesA() {
    // When
    final var computedValuesA = srp6ClientService.computeValuesA();

    // Then
    assertThat(computedValuesA, is(notNullValue()));
  }

  @Test
  void producesNotNullClientPrivateValueWhenComputingValuesA() {
    // When
    final var computedValuesA = srp6ClientService.computeValuesA();

    // Then
    assertThat(computedValuesA.getClientPrivateValueA(), is(notNullValue()));
  }

  @Test
  void producesNotNullClientPublicValueWhenComputingValuesA() {
    // When
    final var computedValuesA = srp6ClientService.computeValuesA();

    // Then
    assertThat(computedValuesA.getClientPublicValueA(), is(notNullValue()));
  }

  @Test
  void producesTheRightValueWhenComputingValuesA() {
    // When
    final var computedValuesA = srp6ClientService.computeValuesA();

    // Then
    final byte[] EXPECTED_CLIENT_PUBLIC_VALUE_A =
        toUnsignedByteArray(
            computeA(
                N,
                g,
                toBigInteger(computedValuesA.getClientPrivateValueA())));

    assertThat(
        computedValuesA.getClientPublicValueA(),
        is(equalTo(EXPECTED_CLIENT_PUBLIC_VALUE_A)));
  }

  @Test
  void producesDifferentValuesWhenComputingTwoConsecutiveValuesA() {
    // When
    final var computedValuesA_1 = srp6ClientService.computeValuesA();
    final var computedValuesA_2 = srp6ClientService.computeValuesA();

    // Then
    assertThat(computedValuesA_1, is(not(samePropertyValuesAs(computedValuesA_2))));
  }

  @Test
  void producesDifferentValuesWhenComputingManyConsecutiveValuesA() {
    // Given
    final var _100 = 100;

    // When
    final var computedValuesAs =
        RunnerUtil.runConsecutivelyToList(
            _100,
            () -> srp6ClientService.computeValuesA());

    // Then
    assertThat(
        computedValuesAs.stream()
            .map(SRP6ClientValuesA::getClientPrivateValueA)
            .map(HexUtil::encodeHex)
            .collect(Collectors.toUnmodifiableSet()),
        hasSize(_100));

    assertThat(
        computedValuesAs.stream()
            .map(SRP6ClientValuesA::getClientPublicValueA)
            .map(HexUtil::encodeHex)
            .collect(Collectors.toUnmodifiableSet()),
        hasSize(_100));
  }

  @Test
  void producesTheRightValueWhenComputingManyConsecutiveValuesA() {
    // Given
    final var _100 = 100;

    // When
    final var computedValuesAs =
        RunnerUtil.runConsecutivelyToList(
            _100,
            () -> srp6ClientService.computeValuesA());

    // Then
    computedValuesAs.forEach(computedValuesA -> {
      final byte[] EXPECTED_CLIENT_PUBLIC_VALUE_A =
          toUnsignedByteArray(
              computeA(
                  N,
                  g,
                  toBigInteger(computedValuesA.getClientPrivateValueA())));

      assertThat(
          computedValuesA.getClientPublicValueA(),
          is(equalTo(EXPECTED_CLIENT_PUBLIC_VALUE_A)));
    });
  }

  @Test
  void producesDifferentValuesWhenComputingConcurrentlyManyValuesA() {
    // Given
    final var _500 = 500;

    // When
    final var computedValuesAs =
        RunnerUtil.runConcurrentlyToList(
            _500,
            () -> srp6ClientService.computeValuesA());

    // Then
    assertThat(
        computedValuesAs.stream()
            .map(SRP6ClientValuesA::getClientPrivateValueA)
            .map(HexUtil::encodeHex)
            .collect(Collectors.toUnmodifiableSet()),
        hasSize(_500));

    assertThat(
        computedValuesAs.stream()
            .map(SRP6ClientValuesA::getClientPublicValueA)
            .map(HexUtil::encodeHex)
            .collect(Collectors.toUnmodifiableSet()),
        hasSize(_500));
  }

  @Test
  void producesTheRightValueWhenComputingConcurrentlyManyValuesA() {
    // Given
    final var _500 = 500;

    // When
    final var computedValuesAs =
        RunnerUtil.runConcurrentlyToList(
            _500,
            () -> srp6ClientService.computeValuesA());

    // Then
    computedValuesAs.forEach(computedValuesA -> {
      final byte[] EXPECTED_CLIENT_PUBLIC_VALUE_A =
          toUnsignedByteArray(
              computeA(
                  N,
                  g,
                  toBigInteger(computedValuesA.getClientPrivateValueA())));

      assertThat(computedValuesA.getClientPublicValueA(),
          is(equalTo(EXPECTED_CLIENT_PUBLIC_VALUE_A)));
    });
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSAndNullSalt() {
    // Given
    final byte[] NULL_SALT = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeS(NULL_SALT, IDENTITY, PASSWORD, a, A, B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSAndNullIdentity() {
    // Given
    final byte[] NULL_IDENTITY = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeS(SALT, NULL_IDENTITY, PASSWORD, a, A, B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSAndNullPassword() {
    // Given
    final byte[] NULL_PASSWORD = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeS(SALT, IDENTITY, NULL_PASSWORD, a, A, B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSAndNullClientPrivateValueA() {
    // Given
    final byte[] NULL_CLIENT_PRIVATE_VALUE_A = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () ->
            srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, NULL_CLIENT_PRIVATE_VALUE_A, A, B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSAndNullClientPublicValueA() {
    // Given
    final byte[] NULL_CLIENT_PUBLIC_VALUE_A = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () ->
            srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, NULL_CLIENT_PUBLIC_VALUE_A, B)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSAndNullServerPublicValueB() {
    // Given
    final byte[] NULL_SERVERS_PUBLIC_VALUE_B = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () ->
            srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, NULL_SERVERS_PUBLIC_VALUE_B)); // When
  }

  @Test
  void producesNotNullWhenComputingS() {
    // When
    final var computedS = srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B);

    // Then
    assertThat(computedS, is(notNullValue()));
  }

  @Test
  void producesTheRightValueWhenComputingS() {
    // When
    final var computedS = srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B);

    // Then
    assertThat(computedS, is(equalTo(S)));
  }

  @Test
  void producesTheSameValueWhenComputingTwoConsecutiveSAndTheSameInputData() {
    // When
    final var computedS_1 = srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B);
    final var computedS_2 = srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B);

    // Then
    assertThat(computedS_1, is(equalTo(computedS_2)));
  }

  @Test
  void producesDifferentValuesWhenComputingTwoConsecutiveSAndDifferentInputData() {
    // When
    final var computedS_1 = srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B);

    final var computedS_2 =
        srp6ClientService.computeS(
            SRP6RFC5054TestingVectors.SALT,
            SRP6RFC5054TestingVectors.IDENTITY,
            SRP6RFC5054TestingVectors.PASSWORD,
            toUnsignedByteArray(SRP6RFC5054TestingVectors.a),
            toUnsignedByteArray(SRP6RFC5054TestingVectors.EXPECTED_A),
            toUnsignedByteArray(SRP6RFC5054TestingVectors.EXPECTED_B));

    // Then
    assertThat(computedS_1, is(not(equalTo(computedS_2))));
  }

  @Test
  void producesTheSameValueWhenComputingManyConsecutiveSAndTheSameInputData() {
    // Given
    final var _100 = 100;

    // When
    final var computedSs =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> encodeHex(srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B)));

    // Then
    assertThat(computedSs, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenComputingManyConsecutiveSAndTheSameInputData() {
    // Given
    final var _100 = 100;

    // When
    final var computedSs =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> encodeHex(srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B)));

    // Then
    assertThat(
        computedSs.iterator().next(),
        is(equalTo(encodeHex(S))));
  }

  @Test
  void producesTheSameValueWhenComputingConcurrentlyManySAndTheSameInputData() {
    // Given
    final var _500 = 500;

    // When
    final var computedSs =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> encodeHex(srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B)));

    // Then
    assertThat(computedSs, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenComputingConcurrentlyManySAndTheSameInputData() {
    // Given
    final var _500 = 500;

    // When
    final var computedSs =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> encodeHex(srp6ClientService.computeS(SALT, IDENTITY, PASSWORD, a, A, B)));

    // Then
    assertThat(computedSs.iterator().next(), is(equalTo(encodeHex(S))));
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingM1AndNullClientPublicValueA() {
    // Given
    final byte[] NULL_CLIENT_PUBLIC_VALUE_A = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeM1(NULL_CLIENT_PUBLIC_VALUE_A, B, S)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingM1AndNullServerPublicValueB() {
    // Given
    final byte[] NULL_SERVER_PUBLIC_VALUE_B = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeM1(A, NULL_SERVER_PUBLIC_VALUE_B, S)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingM1AndNullS() {
    // Given
    final byte[] NULL_S = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeM1(A, B, NULL_S)); // When
  }

  @Test
  void producesNotNullWhenComputingM1() {
    // When
    final var computedM1 = srp6ClientService.computeM1(A, B, S);

    // Then
    assertThat(computedM1, is(notNullValue()));
  }

  @Test
  void producesTheRightValueWhenComputingM1() {
    // When
    final var computedM1 = srp6ClientService.computeM1(A, B, S);

    // Then
    assertThat(computedM1, is(equalTo(M1)));
  }

  @Test
  void producesTheSameValueWhenComputingTwoConsecutiveM1AndTheSameInputData() {
    // When
    final var computedM1_1 = srp6ClientService.computeM1(A, B, S);
    final var computedM1_2 = srp6ClientService.computeM1(A, B, S);

    // Then
    assertThat(computedM1_1, is(equalTo(computedM1_2)));
  }

  @Test
  void producesDifferentValuesWhenComputingTwoConsecutiveM1AndDifferentInputData() {
    // When
    final var computedM1_1 = srp6ClientService.computeM1(A, B, S);

    final var computedM2_2 =
        srp6ClientService.computeM1(
            toUnsignedByteArray(SRP6RFC5054TestingVectors.EXPECTED_A),
            toUnsignedByteArray(SRP6RFC5054TestingVectors.EXPECTED_B),
            toUnsignedByteArray(SRP6RFC5054TestingVectors.EXPECTED_S));

    // Then
    assertThat(computedM1_1, is(not(equalTo(computedM2_2))));
  }

  @Test
  void producesTheSameValueWhenComputingManyConsecutiveM1AndTheSameInputData() {
    // Given
    final var _100 = 100;

    // When
    final var computedM1s =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> encodeHex(srp6ClientService.computeM1(A, B, S)));

    // Then
    assertThat(computedM1s, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenComputingManyConsecutiveM1AndTheSameInputData() {
    // Given
    final var _100 = 100;

    // When
    final var computedM1s =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> encodeHex(srp6ClientService.computeM1(A, B, S)));

    // Then
    assertThat(computedM1s.iterator().next(), is(equalTo(encodeHex(M1))));
  }

  @Test
  void producesTheSameValueWhenComputingConcurrentlyManyM1AndTheSameInputData() {
    // Given
    final var _500 = 500;

    // When
    final var computedM1s =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> encodeHex(srp6ClientService.computeM1(A, B, S)));

    // Then
    assertThat(computedM1s, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenComputingConcurrentlyManyM1AndTheSameInputData() {
    // Given
    final var _500 = 500;

    // When
    final var computedM1s =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> encodeHex(srp6ClientService.computeM1(A, B, S)));

    // Then
    assertThat(computedM1s.iterator().next(), is(equalTo(encodeHex(M1))));
  }

  @Test
  void throwsIllegalArgumentExceptionWhenValidatingReceivedM2AndNullClientPublicValueA() {
    // Given
    final byte[] NULL_CLIENT_PUBLIC_VALUE_A = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.isValidReceivedM2(NULL_CLIENT_PUBLIC_VALUE_A, S, M1, M2)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenValidatingReceivedM2AndNullS() {
    // Given
    final byte[] NULL_S = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.isValidReceivedM2(A, NULL_S, M1, M2)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenValidatingReceivedM2AndNullM1() {
    // Given
    final byte[] NULL_M1 = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.isValidReceivedM2(A, S, NULL_M1, M2)); // When
  }

  @Test
  void throwsIllegalArgumentExceptionWhenValidatingReceivedM2AndNullReceivedM2() {
    // Given
    final byte[] NULL_RECEIVED_M2 = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.isValidReceivedM2(A, S, M1, NULL_RECEIVED_M2)); // When
  }

  @Test
  void producesTheRightValueWhenValidatingReceivedM2AndValidM2() {
    // When
    final var isValidReceivedM2 = srp6ClientService.isValidReceivedM2(A, S, M1, M2);

    // Then
    assertThat(isValidReceivedM2, is(true));
  }

  @Test
  void producesTheRightValueWhenValidatingReceivedM2AndInvalidM2() {
    // Given
    final var INVALID_RECEIVED_M2 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    // When
    final var isValidReceivedM2 =
        srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2);

    // Then
    assertThat(isValidReceivedM2, is(false));
  }

  @Test
  void producesTheSameValueWhenValidatingTwoReceivedM2AndValidM2() {
    // When
    final var isValidReceivedM2_1 = srp6ClientService.isValidReceivedM2(A, S, M1, M2);
    final var isValidReceivedM2_2 = srp6ClientService.isValidReceivedM2(A, S, M1, M2);

    // Then
    assertThat(isValidReceivedM2_1, is(equalTo(isValidReceivedM2_2)));
  }

  @Test
  void producesTheSameValueWhenValidatingTwoReceivedM2AndInvalidM2() {
    // Given
    final var INVALID_RECEIVED_M2 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    // When
    final var isValidReceivedM2_1 =
        srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2);

    final var isValidReceivedM2_2 =
        srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2);

    // Then
    assertThat(isValidReceivedM2_1, is(equalTo(isValidReceivedM2_2)));
  }

  @Test
  void producesTheSameValueWhenValidatingManyConsecutiveReceivedM2AndValidM2() {
    // Given
    final var _100 = 100;

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, M2));

    // Then
    assertThat(isValidReceivedM2s, hasSize(1));
  }

  @Test
  void producesTheSameValueWhenValidatingManyConsecutiveReceivedM2AndInvalidM2() {
    // Given
    final var _100 = 100;
    final var INVALID_RECEIVED_M2 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2));

    // Then
    assertThat(isValidReceivedM2s, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenValidatingManyConsecutiveReceivedM2AndValidM2() {
    // Given
    final var _100 = 100;

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, M2));

    // Then
    assertThat(isValidReceivedM2s.iterator().next(), is(true));
  }

  @Test
  void producesTheRightValueWhenValidatingManyConsecutiveReceivedM2AndInvalidM2() {
    // Given
    final var _100 = 100;
    final var INVALID_RECEIVED_M2 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2));

    // Then
    assertThat(isValidReceivedM2s.iterator().next(), is(false));
  }

  @Test
  void producesTheSameValueWhenValidatingConcurrentlyManyReceivedM2AndValidM2() {
    // Given
    final var _500 = 500;

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, M2));

    // Then
    assertThat(isValidReceivedM2s, hasSize(1));
  }

  @Test
  void producesTheSameValueWhenValidatingConcurrentlyManyReceivedM2AndInvalidM2() {
    // Given
    final var _500 = 500;
    final var INVALID_RECEIVED_M2 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2));

    // Then
    assertThat(isValidReceivedM2s, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenValidatingConcurrentlyManyReceivedM2AndValidM2() {
    // Given
    final var _500 = 500;

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, M2));

    // Then
    assertThat(isValidReceivedM2s.iterator().next(), is(true));
  }

  @Test
  void producesTheRightValueWhenValidatingConcurrentlyManyReceivedM2AndInvalidM2() {
    // Given
    final var _500 = 500;
    final var INVALID_RECEIVED_M2 = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

    // When
    final var isValidReceivedM2s =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> srp6ClientService.isValidReceivedM2(A, S, M1, INVALID_RECEIVED_M2));

    // Then
    assertThat(isValidReceivedM2s.iterator().next(), is(false));
  }

  @Test
  void throwsIllegalArgumentExceptionWhenComputingSessionKeyAndNullS() {
    // Given
    final byte[] NULL_S = null;

    // Then
    assertThrows(
        IllegalArgumentException.class,
        () -> srp6ClientService.computeSessionKey(NULL_S)
    );
  }

  @Test
  void producesNotNullWhenComputingSessionKey() {
    // When
    final var computedSessionKey = srp6ClientService.computeSessionKey(S);

    // Then
    assertThat(computedSessionKey, is(notNullValue()));
  }

  @Test
  void producesTheRightValueWhenComputingSessionKey() {
    // When
    final var computedSessionKey = srp6ClientService.computeSessionKey(S);

    // Then
    assertThat(computedSessionKey, is(equalTo(SESSION_KEY)));
  }

  @Test
  void producesTheSameValueWhenComputingTwoConsecutiveSessionKeyAndTheSameInputData() {
    // When
    final var computedSessionKey_1 = srp6ClientService.computeSessionKey(S);
    final var computedSessionKey_2 = srp6ClientService.computeSessionKey(S);

    // Then
    assertThat(computedSessionKey_1, is(equalTo(computedSessionKey_2)));
  }

  @Test
  void producesDifferentValuesWhenComputingTwoConsecutiveSessionKeyAndDifferentInputData() {
    // When
    final var computedSessionKey_1 = srp6ClientService.computeSessionKey(S);

    final var computedSessionKey_2 =
        srp6ClientService.computeSessionKey(
            toUnsignedByteArray(SRP6RFC5054TestingVectors.EXPECTED_S));

    // Then
    assertThat(computedSessionKey_1, is(not(equalTo(computedSessionKey_2))));
  }

  @Test
  void producesTheSameValueWhenComputingManyConsecutiveSessionKeyAndTheSameInputData() {
    // Given
    final var _100 = 100;

    // When
    final var computedSessionKeys =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> encodeHex(srp6ClientService.computeSessionKey(S)));

    // Then
    assertThat(computedSessionKeys, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenComputingManyConsecutiveSessionKeyAndTheSameInputData() {
    // Given
    final var _100 = 100;

    // When
    final var computedSessionKeys =
        RunnerUtil.runConsecutivelyToSet(
            _100,
            () -> encodeHex(srp6ClientService.computeSessionKey(S)));

    // Then
    assertThat(computedSessionKeys.iterator().next(), is(equalTo(encodeHex(SESSION_KEY))));
  }

  @Test
  void producesTheSameValueWhenComputingConcurrentlyManySessionKeyAndTheSameInputData() {
    // Given
    final var _500 = 500;

    // When
    final var computedSessionKeys =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> encodeHex(srp6ClientService.computeSessionKey(S)));

    // Then
    assertThat(computedSessionKeys, hasSize(1));
  }

  @Test
  void producesTheRightValueWhenComputingConcurrentlyManySessionKeyAndTheSameInputData() {
    // Given
    final var _500 = 500;

    // When
    final var computedSessionKeys =
        RunnerUtil.runConcurrentlyToSet(
            _500,
            () -> encodeHex(srp6ClientService.computeSessionKey(S)));

    // Then
    assertThat(computedSessionKeys.iterator().next(), is(equalTo(encodeHex(SESSION_KEY))));
  }
}