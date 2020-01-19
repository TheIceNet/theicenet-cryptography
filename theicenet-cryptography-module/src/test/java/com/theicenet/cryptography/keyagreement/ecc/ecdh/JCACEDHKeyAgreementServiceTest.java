package com.theicenet.cryptography.keyagreement.ecc.ecdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;

import com.theicenet.cryptography.key.asymmetric.ecc.ECCKeyAlgorithm;
import com.theicenet.cryptography.keyagreement.KeyAgreementService;
import com.theicenet.cryptography.test.util.HexUtil;
import com.theicenet.cryptography.util.CryptographyProviderUtil;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;

class JCACEDHKeyAgreementServiceTest {
  final ECCKeyAlgorithm ECDH = ECCKeyAlgorithm.ECDH;

  final byte[] ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BYTE_ARRAY_ALICE =
      HexUtil.decodeHex(
          "305a301406072a8648ce3d020106092b24030302080101070342000430acba7508c3842bd719923"
              + "20cb86bd93cb31d46fe76c860fc5d9a17d68e257a3922d39c018f2ce4632aa0db89fd4a95"
              + "5889da34556e47ab19adf317673bc75d");

  final byte[] ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BYTE_ARRAY_ALICE =
      HexUtil.decodeHex(
          "308188020100301406072a8648ce3d020106092b2403030208010107046d306b020101042031bb"
              + "5a63396638ba89a75640a151a625aa23504ab037e2f983ff799cc658262ba14403420004"
              + "30acba7508c3842bd71992320cb86bd93cb31d46fe76c860fc5d9a17d68e257a3922d39c"
              + "018f2ce4632aa0db89fd4a955889da34556e47ab19adf317673bc75d");

  final byte[] ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BYTE_ARRAY_BOB =
      HexUtil.decodeHex(
          "305a301406072a8648ce3d020106092b240303020801010703420004831d47e0175135e72050c56"
              + "fb9c3a97db56370123b66e5ebec702bcc5889149628822b169c967830499668d78eb5f38e"
              + "c437eef1c8dab3fac2896ec6b5c0f534");

  final byte[] ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BYTE_ARRAY_BOB =
      HexUtil.decodeHex(
          "308188020100301406072a8648ce3d020106092b2403030208010107046d306b0201010420446b35"
              + "84913b39cb26083996c3d976cbe4a64b9f1873cc40cabad3c97b1c40d3a14403420004831d"
              + "47e0175135e72050c56fb9c3a97db56370123b66e5ebec702bcc5889149628822b169c9678"
              + "30499668d78eb5f38ec437eef1c8dab3fac2896ec6b5c0f534");

  final PublicKey ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE;
  final PrivateKey ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE;
  final PublicKey ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB;
  final PrivateKey ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB;

  final byte[] ECDH_DERIVED_SECRET_KEY =
      HexUtil.decodeHex("3078620e26babfd1200f70a280f7370ef15ce0176e983a2f6803de6eff5dc269");

  final KeyAgreementService keyAgreementService;

  JCACEDHKeyAgreementServiceTest() throws Exception {
    // Bouncy Castle is required for ECDH key factory
    CryptographyProviderUtil.addBouncyCastleCryptographyProvider();

    final var keyFactory = KeyFactory.getInstance(ECDH.toString());

    final var x509EncodedKeySpecAlice = new X509EncodedKeySpec(
        ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BYTE_ARRAY_ALICE);
    ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE = keyFactory.generatePublic(x509EncodedKeySpecAlice);

    final var pkcs8EncodedKeySpecAlice = new PKCS8EncodedKeySpec(
        ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BYTE_ARRAY_ALICE);
    ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE = keyFactory.generatePrivate(pkcs8EncodedKeySpecAlice);

    final var x509EncodedKeySpecBob = new X509EncodedKeySpec(
        ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BYTE_ARRAY_BOB);
    ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB = keyFactory.generatePublic(x509EncodedKeySpecBob);

    final var pkcs8EncodedKeySpecBob = new PKCS8EncodedKeySpec(
        ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BYTE_ARRAY_BOB);
    ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB = keyFactory.generatePrivate(pkcs8EncodedKeySpecBob);

    keyAgreementService = new JCACEDHKeyAgreementService();
  }

  @Test
  void producesNotNullSecretKeyWhenGeneratingSecretKeyForAlice() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    // Then
    assertThat(generatedSecretKey, is(notNullValue()));
  }

  @Test
  void producesNotNullSecretKeyWhenGeneratingSecretKeyForBob() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    // Then
    assertThat(generatedSecretKey, is(notNullValue()));
  }

  @Test
  void producesNotEmptySecretKeyWhenGeneratingSecretKeyForAlice() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    // Then
    assertThat(generatedSecretKey.length, is(greaterThan(0)));
  }

  @Test
  void producesNotEmptySecretKeyWhenGeneratingSecretKeyForBob() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    // Then
    assertThat(generatedSecretKey.length, is(greaterThan(0)));
  }

  @Test
  void producesSecretKeyWithRightLengthWhenGeneratingSecretKeyForAlice() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    // Then
    assertThat(generatedSecretKey.length, is(equalTo(ECDH_DERIVED_SECRET_KEY.length)));
  }

  @Test
  void producesSecretKeyWithRightLengthWhenGeneratingSecretKeyForBob() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    // Then
    assertThat(generatedSecretKey.length, is(equalTo(ECDH_DERIVED_SECRET_KEY.length)));
  }

  @Test
  void producesTheSameSecretKeyForAliceAndBob() {
    // When
    final var generatedSecretKeyAlice =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    final var generatedSecretKeyBob =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    // Then
    assertThat(generatedSecretKeyAlice, is(equalTo(generatedSecretKeyBob)));
  }

  @Test
  void producesTheRightSecretKeyWhenGeneratingSecretKeyForAlice() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    // Then
    assertThat(generatedSecretKey, is(equalTo(ECDH_DERIVED_SECRET_KEY)));
  }

  @Test
  void producesTheRightSecretKeyWhenGeneratingSecretKeyForBob() {
    // When
    final var generatedSecretKey =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    // Then
    assertThat(generatedSecretKey, is(equalTo(ECDH_DERIVED_SECRET_KEY)));
  }

  @Test
  void producesTheSameSecretKeyWhenGeneratingTwoConsecutiveSecretKeyForAlice() {
    // When
    final var generatedSecretKey_1 =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    final var generatedSecretKey_2 =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);

    // Then
    assertThat(generatedSecretKey_1, is(equalTo(generatedSecretKey_2)));
  }

  @Test
  void producesTheSameSecretKeyWhenGeneratingTwoConsecutiveSecretKeyForBon() {
    // When
    final var generatedSecretKey_1 =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    final var generatedSecretKey_2 =
        keyAgreementService.generateSecretKey(
            ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
            ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);

    // Then
    assertThat(generatedSecretKey_1, is(equalTo(generatedSecretKey_2)));
  }

  @Test
  void producesTheSameSecretKeyWhenGeneratingManyConsecutiveSecretKeyForAlice() {
    // Given
    final var _100 = 100;

    // When generating consecutive secret key for alice
    final var generateSecretKeys =
        IntStream
            .range(0, _100)
            .mapToObj(index ->
                keyAgreementService.generateSecretKey(
                  ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
                  ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB))
            .map(Hex::encodeHex)
            .map(String::new)
            .collect(Collectors.toUnmodifiableSet());

    // Then all secret keys generated are the same
    assertThat(generateSecretKeys, hasSize(1));
  }

  @Test
  void producesTheSameSecretKeyWhenGeneratingManyConsecutiveSecretKeyForBob() {
    // Given
    final var _100 = 100;

    // When generating consecutive secret key for bob
    final var generateSecretKeys =
        IntStream
            .range(0, _100)
            .mapToObj(index ->
                keyAgreementService.generateSecretKey(
                    ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
                    ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE))
            .map(Hex::encodeHex)
            .map(String::new)
            .collect(Collectors.toUnmodifiableSet());

    // Then all secret keys generated are the same
    assertThat(generateSecretKeys, hasSize(1));
  }

  @Test
  void producesTheSameSecretKeyWhenGeneratingConcurrentlyManySecretKeyForAlice() throws InterruptedException {
    // Given
    final var _500 = 500;

    // When generating concurrently secret key for alice
    final var countDownLatch = new CountDownLatch(_500);
    final var executorService = Executors.newFixedThreadPool(_500);

    final var generatedSecretKeys = new CopyOnWriteArraySet<byte[]>();

    IntStream
        .range(0, _500)
        .forEach(index ->
            executorService.execute(() -> {
              countDownLatch.countDown();
              try {
                countDownLatch.await();
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
              }

              final var generatedSecretKey =
                  keyAgreementService.generateSecretKey(
                      ECDH_PRIVATE_KEY_BRAINPOOLP256R1_ALICE,
                      ECDH_PUBLIC_KEY_BRAINPOOLP256R1_BOB);
              generatedSecretKeys.add(generatedSecretKey);
            }));

    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.SECONDS);

    // Then all secret keys generated are the same
    assertThat(
        generatedSecretKeys.stream()
          .map(Hex::encodeHex)
          .map(String::new)
          .collect(Collectors.toUnmodifiableSet()),
        hasSize(1));
  }

  @Test
  void producesTheSameSecretKeyWhenGeneratingConcurrentlyManySecretKeyForBob() throws InterruptedException {
    // Given
    final var _500 = 500;

    // When generating concurrently secret key for alice
    final var countDownLatch = new CountDownLatch(_500);
    final var executorService = Executors.newFixedThreadPool(_500);

    final var generatedSecretKeys = new CopyOnWriteArraySet<byte[]>();

    IntStream
        .range(0, _500)
        .forEach(index ->
            executorService.execute(() -> {
              countDownLatch.countDown();
              try {
                countDownLatch.await();
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
              }

              final var generatedSecretKey =
                  keyAgreementService.generateSecretKey(
                      ECDH_PRIVATE_KEY_BRAINPOOLP256R1_BOB,
                      ECDH_PUBLIC_KEY_BRAINPOOLP256R1_ALICE);
              generatedSecretKeys.add(generatedSecretKey);
            }));

    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.SECONDS);

    // Then all secret keys generated are the same
    assertThat(
        generatedSecretKeys.stream()
            .map(Hex::encodeHex)
            .map(String::new)
            .collect(Collectors.toUnmodifiableSet()),
        hasSize(1));
  }
}