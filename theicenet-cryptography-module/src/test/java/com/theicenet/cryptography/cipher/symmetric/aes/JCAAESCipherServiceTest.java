package com.theicenet.cryptography.cipher.symmetric.aes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.theicenet.cryptography.cipher.symmetric.SymmetricIVBasedCipherService;
import com.theicenet.cryptography.test.util.HexUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

class JCAAESCipherServiceTest {

  // Given
  static final String AES = "AES";
  static final BlockCipherModeOfOperation CBC = BlockCipherModeOfOperation.CBC;
  static final BlockCipherModeOfOperation CFB = BlockCipherModeOfOperation.CFB;
  static final BlockCipherModeOfOperation OFB = BlockCipherModeOfOperation.OFB;
  static final BlockCipherModeOfOperation CTR = BlockCipherModeOfOperation.CTR;

  static final byte[] CLEAR_CONTENT =
      "Content to encrypt with AES and different options for block cipher mode of operation"
          .getBytes(StandardCharsets.UTF_8);

  static final SecretKey SECRET_KEY_1234567890123456_128_BITS =
      new SecretKeySpec(
          "1234567890123456".getBytes(StandardCharsets.UTF_8),
          AES);

  static final byte[] INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS =
      "KLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.UTF_8);

  static final byte[] ENCRYPTED_CONTENT_AES_CBC =
      HexUtil.decodeHex(
          "e9ace3b5980b905b3c5823555dbea50b69d0b312"
              + "9f3aa2540255b35dc5d46128a83ae6989e4d94ed"
              + "83d6ffcb4210ddd9686719807ed8537e6040d3cb"
              + "332a63dfe642db91b1e39bad80fa8a86329b04ee"
              + "8ee57305ff62e7daf001897f7c4a1e5a");

  static final byte[] ENCRYPTED_CONTENT_AES_CFB =
      HexUtil.decodeHex(
          "813d91455835f9650de0506a0cbc9126d4c171c5e"
              + "fc1c3c7137e9d2fb2f711897b3261d0f760243583"
              + "5a693ab44f52b0e51c889504655b6a88c64c446b6"
              + "669dfc61c082e932ec53767b3de363beb10fa3ceb"
              + "2ed8");

  static final byte[] ENCRYPTED_CONTENT_AES_OFB =
      HexUtil.decodeHex(
          "813d91455835f9650de0506a0cbc91263746a29bdf"
              + "2e031c65d44d000366eff30193861a14b73867329d"
              + "a374a511cc52dbfa0fc116f47423ed37694ceb016a"
              + "fd3b208a31e1aa4a7eb99b4f7e57966ec1376588d1");

  static final byte[] ENCRYPTED_CONTENT_AES_CTR =
      HexUtil.decodeHex(
          "813d91455835f9650de0506a0cbc9126da73e6"
              + "e016a787a39e6f0bd8914874f6af0f2fca3094"
              + "65217d86aa55d9a1689666ce4189cb6194e1ac"
              + "20e0ea5e2e60ec70b0f31255a4dc6cf304edb41"
              + "92d28c725751474");

  SymmetricIVBasedCipherService aesCipherService;

  @BeforeEach
  void setUp() {
    aesCipherService = new JCAAESCipherService(CTR);
  }

  @Test
  void producesNotNullWhenEncryptingByteArray() {
    // When
    final var encrypted =
        aesCipherService.encrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_CONTENT);

    // Then
    assertThat(encrypted, is(notNullValue()));
  }

  @Test
  void producesNotNullWhenEncryptingStream() throws IOException {
    // Given
    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

      // When
      aesCipherService.encrypt(
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
          clearInputStream,
          encryptedOutputStream);

      // Then
      assertThat(encryptedOutputStream.toByteArray(), is(notNullValue()));
  }

  @Test
  void producesNotEmptyWhenEncryptingByteArray() {
    // When
    final var encrypted =
        aesCipherService.encrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_CONTENT);

    // Then
    assertThat(encrypted.length, is(greaterThan(0)));
  }

  @Test
  void producesNotEmptyWhenEncryptingByteStream() throws IOException {
    // Given
    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

      // When
      aesCipherService.encrypt(
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
          clearInputStream,
          encryptedOutputStream);

      // Then
      assertThat(encryptedOutputStream.toByteArray().length, is(greaterThan(0)));
  }

  @Test
  void throwsIllegalArgumentExceptionWhenEncryptingByteArrayWithInvalidIVSize() {
    // Given initialization vector of invalid size (= 64 bits)
    final var INITIALIZATION_VECTOR_KLMNOPQR_64_BITS =
        "KLMNOPQR".getBytes(StandardCharsets.UTF_8);

    // When encrypting AES with invalid IV size
    // Then throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> {
      aesCipherService.encrypt(
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQR_64_BITS,
          CLEAR_CONTENT);
    });
  }

  @Test
  void throwsIllegalArgumentExceptionWhenEncryptingStreamWithInvalidIVSize() {
    // Given initialization vector of invalid size (= 64 bits)
    final var INITIALIZATION_VECTOR_KLMNOPQR_64_BITS =
        "KLMNOPQR".getBytes(StandardCharsets.UTF_8);

    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

    // When encrypting AES with invalid IV size
    // Then throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> {
      aesCipherService.encrypt(
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQR_64_BITS,
          clearInputStream,
          encryptedOutputStream);
    });
  }

  @ParameterizedTest
  @EnumSource(
      value = BlockCipherModeOfOperation.class,
      names = {"CBC"},
      mode = EnumSource.Mode.EXCLUDE)
  void producesSizeOfEncryptedEqualsToSizeOfClearContentWhenEncryptingByteArray(BlockCipherModeOfOperation blockMode) {
    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    // When
    final var encrypted =
        aesCipherService.encrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_CONTENT);

    // Then
    assertThat(encrypted.length, is(equalTo(CLEAR_CONTENT.length)));
  }

  @Test
  void producesSizeOfEncryptedEqualsToSizeOfClearContentPlusPaddingWhenEncryptingByteArrayWithBlockModeCBC() {
    // Given
    aesCipherService = new JCAAESCipherService(BlockCipherModeOfOperation.CBC);

    // When
    final var encrypted =
        aesCipherService.encrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_CONTENT);

    // Then
    assertThat(
        encrypted.length,
        is(equalTo(CLEAR_CONTENT.length + (16 - CLEAR_CONTENT.length % 16))));
  }

  @ParameterizedTest
  @EnumSource(
      value = BlockCipherModeOfOperation.class,
      names = {"CBC"},
      mode = EnumSource.Mode.EXCLUDE)
  void producesSizeOfEncryptedEqualsToSizeOfClearContentWhenEncryptingStream(BlockCipherModeOfOperation blockMode) throws IOException {
    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.encrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
        clearInputStream,
        encryptedOutputStream);

    // Then
    assertThat(encryptedOutputStream.toByteArray().length, is(is(equalTo(CLEAR_CONTENT.length))));
  }

  @Test
  void producesSizeOfEncryptedEqualsToSizeOfClearContentPlusPaddingWhenEncryptingStreamWithBlockModeCBC() {
    // Given
    aesCipherService = new JCAAESCipherService(BlockCipherModeOfOperation.CBC);

    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.encrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
        clearInputStream,
        encryptedOutputStream);

    // Then
    assertThat(
        encryptedOutputStream.toByteArray().length,
        is(equalTo(CLEAR_CONTENT.length + (16 - CLEAR_CONTENT.length % 16))));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithClearContentAndSecretKeyAndIVAndBlockModeAndExpectedEncryptedResult")
  void producesTheRightEncryptedResultWhenEncryptingByteArray(
      byte[] clearContent,
      SecretKey secretKey,
      byte[] iv,
      BlockCipherModeOfOperation blockMode,
      byte[] expectedEncryptedResult) {

    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    // When
    final var encrypted =
        aesCipherService.encrypt(
            secretKey,
            iv,
            clearContent);

    // Then
    assertThat(encrypted, is(equalTo(expectedEncryptedResult)));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithClearContentAndSecretKeyAndIVAndBlockModeAndExpectedEncryptedResult")
  void producesTheRightEncryptedResultWhenEncryptingStream(
      byte[] clearContent,
      SecretKey secretKey,
      byte[] iv,
      BlockCipherModeOfOperation blockMode,
      byte[] expectedEncryptedResult) {

    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    final var clearInputStream = new ByteArrayInputStream(clearContent);
    final var encryptedOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.encrypt(
        secretKey,
        iv,
        clearInputStream,
        encryptedOutputStream);

    // Then
    assertThat(encryptedOutputStream.toByteArray(), is(equalTo(expectedEncryptedResult)));
  }

  static Stream<Arguments> argumentsWithClearContentAndSecretKeyAndIVAndBlockModeAndExpectedEncryptedResult() {
    return Stream.of(
        Arguments.of(
            CLEAR_CONTENT,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CBC,
            ENCRYPTED_CONTENT_AES_CBC),
        Arguments.of(
            CLEAR_CONTENT,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CFB,
            ENCRYPTED_CONTENT_AES_CFB),
        Arguments.of(
            CLEAR_CONTENT,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            OFB,
            ENCRYPTED_CONTENT_AES_OFB),
        Arguments.of(
            CLEAR_CONTENT,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CTR,
            ENCRYPTED_CONTENT_AES_CTR)
    );
  }

  @Test
  void producesNotNullWhenDecryptingByteArray() {
    // When
    final var decrypted =
        aesCipherService.decrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            ENCRYPTED_CONTENT_AES_CTR);

    // Then
    assertThat(decrypted, is(notNullValue()));
  }

  @Test
  void producesNotNullWhenDecryptingStream() {
    // Given
    final var encryptedInputStream = new ByteArrayInputStream(ENCRYPTED_CONTENT_AES_CTR);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.decrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
        encryptedInputStream,
        clearOutputStream);

    // Then
    assertThat(clearOutputStream.toByteArray(), is(notNullValue()));
  }

  @Test
  void producesNotEmptyWhenDecryptingByteArray() {
    // When
    final var decrypted =
        aesCipherService.decrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            ENCRYPTED_CONTENT_AES_CTR);

    // Then
    assertThat(decrypted.length, is(greaterThan(0)));
  }

  @Test
  void producesNotEmptyWhenDecryptingStream() {
    // Given
    final var encryptedInputStream = new ByteArrayInputStream(ENCRYPTED_CONTENT_AES_CTR);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.decrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
        encryptedInputStream,
        clearOutputStream);

    // Then
    assertThat(clearOutputStream.toByteArray().length, is(greaterThan(0)));
  }

  @Test
  void throwsIllegalArgumentExceptionWhenDecryptingByteArrayWithInvalidIVSize() {
    // Given initialization vector of invalid size (= 64 bits)
    final var INITIALIZATION_VECTOR_KLMNOPQR_64_BITS =
        "KLMNOPQR".getBytes(StandardCharsets.UTF_8);

    // When decrypting AES with invalid IV size
    // Then throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> {
      aesCipherService.decrypt(
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQR_64_BITS,
          ENCRYPTED_CONTENT_AES_CTR);
    });
  }

  @Test
  void throwsIllegalArgumentExceptionWhenDecryptingStreamWithInvalidIVSize() {
    // Given initialization vector of invalid size (= 64 bits)
    final var INITIALIZATION_VECTOR_KLMNOPQR_64_BITS =
        "KLMNOPQR".getBytes(StandardCharsets.UTF_8);

    final var encryptedInputStream = new ByteArrayInputStream(ENCRYPTED_CONTENT_AES_CTR);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When decrypting AES with invalid IV size
    // Then throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> {
      aesCipherService.decrypt(
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQR_64_BITS,
          encryptedInputStream,
          clearOutputStream);
    });
  }

  @ParameterizedTest
  @MethodSource("argumentsWithEncryptedContentAndSecretKeyAndIVAndBlockModeAndExpectedDecryptedSize")
  void producesSizeOfDecryptedEqualsToSizeOfEncryptedContentWhenDecryptingByteArray(
      byte[] encryptedContent,
      SecretKey secretKey,
      byte[] iv,
      BlockCipherModeOfOperation blockMode,
      Integer expectedDecryptedSize) {

    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    // When
    final var decrypted =
        aesCipherService.decrypt(
            secretKey,
            iv,
            encryptedContent);

    // Then
    assertThat(decrypted.length, is(equalTo(expectedDecryptedSize)));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithEncryptedContentAndSecretKeyAndIVAndBlockModeAndExpectedDecryptedSize")
  void producesSizeOfDecryptedEqualsToSizeOfEncryptedContentWhenDecryptingStream(
      byte[] encryptedContent,
      SecretKey secretKey,
      byte[] iv,
      BlockCipherModeOfOperation blockMode,
      Integer expectedDecryptedSize) {

    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    final var encryptedInputStream = new ByteArrayInputStream(encryptedContent);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.decrypt(
        secretKey,
        iv,
        encryptedInputStream,
        clearOutputStream);

    // Then
    assertThat(clearOutputStream.toByteArray().length, is(equalTo(expectedDecryptedSize)));
  }

  static Stream<Arguments> argumentsWithEncryptedContentAndSecretKeyAndIVAndBlockModeAndExpectedDecryptedSize() {
    return Stream.of(
        Arguments.of(
            ENCRYPTED_CONTENT_AES_CFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CFB,
            ENCRYPTED_CONTENT_AES_CFB.length),
        Arguments.of(
            ENCRYPTED_CONTENT_AES_CBC,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CBC,
            ENCRYPTED_CONTENT_AES_CBC.length - (16 - CLEAR_CONTENT.length % 16)),
        Arguments.of(
            ENCRYPTED_CONTENT_AES_OFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            OFB,
            ENCRYPTED_CONTENT_AES_OFB.length),
        Arguments.of(
            ENCRYPTED_CONTENT_AES_CTR,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CTR,
            ENCRYPTED_CONTENT_AES_CTR.length)
    );
  }

  @ParameterizedTest
  @MethodSource("argumentsWithEncryptedContentAndSecretKeyAndIVAndBlockModeAndExpectedDecryptedResult")
  void producesTheRightDecryptedResultWhenDecryptingByteArray(
      byte[] encryptedContent,
      SecretKey secretKey,
      byte[] iv,
      BlockCipherModeOfOperation blockMode,
      byte[] expectedDecryptedResult) {

    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    // When
    final var decrypted =
        aesCipherService.decrypt(
            secretKey,
            iv,
            encryptedContent);

    // Then
    assertThat(decrypted, is(equalTo(expectedDecryptedResult)));
  }

  @ParameterizedTest
  @MethodSource("argumentsWithEncryptedContentAndSecretKeyAndIVAndBlockModeAndExpectedDecryptedResult")
  void producesTheRightDecryptedResultWhenDecryptingStream(
      byte[] encryptedContent,
      SecretKey secretKey,
      byte[] iv,
      BlockCipherModeOfOperation blockMode,
      byte[] expectedDecryptedResult) {

    // Given
    aesCipherService = new JCAAESCipherService(blockMode);

    final var encryptedInputStream = new ByteArrayInputStream(encryptedContent);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.decrypt(
        secretKey,
        iv,
        encryptedInputStream,
        clearOutputStream);

    // Then
    assertThat(clearOutputStream.toByteArray(), is(equalTo(expectedDecryptedResult)));
  }

  static Stream<Arguments> argumentsWithEncryptedContentAndSecretKeyAndIVAndBlockModeAndExpectedDecryptedResult() {
    return Stream.of(
        Arguments.of(
            ENCRYPTED_CONTENT_AES_CBC,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CBC,
            CLEAR_CONTENT),
        Arguments.of(
            ENCRYPTED_CONTENT_AES_CFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CFB,
            CLEAR_CONTENT),
        Arguments.of(
            ENCRYPTED_CONTENT_AES_OFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            OFB,
            CLEAR_CONTENT),
        Arguments.of(
            ENCRYPTED_CONTENT_AES_CTR,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CTR,
            CLEAR_CONTENT)
    );
  }
}

