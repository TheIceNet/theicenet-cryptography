package com.theicenet.cryptography.service.symmetric.aes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JCAAESCryptographyServiceTest {

  // Given
  final String AES = "AES";
  final BlockCipherModeOfOperation CBC = BlockCipherModeOfOperation.CBC;
  final BlockCipherModeOfOperation CFB = BlockCipherModeOfOperation.CFB;
  final BlockCipherModeOfOperation OFB = BlockCipherModeOfOperation.OFB;
  final BlockCipherModeOfOperation CTR = BlockCipherModeOfOperation.CTR;

  final byte[] CLEAR_MESSAGE =
      "Content to encrypt with AES and different options for block cipher mode of operation"
          .getBytes(StandardCharsets.UTF_8);

  final SecretKey SECRET_KEY_1234567890123456_128_BITS =
      new SecretKeySpec(
          "1234567890123456".getBytes(StandardCharsets.UTF_8),
          AES);

  final byte[] INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS =
      "KLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.UTF_8);

  final byte[] ENCRYPTED_MESSAGE_AES_CBC =
      Hex.decodeHex(
          "e9ace3b5980b905b3c5823555dbea50b69d0b312"
              + "9f3aa2540255b35dc5d46128a83ae6989e4d94ed"
              + "83d6ffcb4210ddd9686719807ed8537e6040d3cb"
              + "332a63dfe642db91b1e39bad80fa8a86329b04ee"
              + "8ee57305ff62e7daf001897f7c4a1e5a");

  final byte[] ENCRYPTED_MESSAGE_AES_CFB =
      Hex.decodeHex(
          "813d91455835f9650de0506a0cbc9126d4c171c5e"
              + "fc1c3c7137e9d2fb2f711897b3261d0f760243583"
              + "5a693ab44f52b0e51c889504655b6a88c64c446b6"
              + "669dfc61c082e932ec53767b3de363beb10fa3ceb"
              + "2ed8");

  final byte[] ENCRYPTED_MESSAGE_AES_OFB =
      Hex.decodeHex(
          "813d91455835f9650de0506a0cbc91263746a29bdf"
              + "2e031c65d44d000366eff30193861a14b73867329d"
              + "a374a511cc52dbfa0fc116f47423ed37694ceb016a"
              + "fd3b208a31e1aa4a7eb99b4f7e57966ec1376588d1");

  final byte[] ENCRYPTED_MESSAGE_AES_CTR =
      Hex.decodeHex(
          "813d91455835f9650de0506a0cbc9126da73e6"
              + "e016a787a39e6f0bd8914874f6af0f2fca3094"
              + "65217d86aa55d9a1689666ce4189cb6194e1ac"
              + "20e0ea5e2e60ec70b0f31255a4dc6cf304edb41"
              + "92d28c725751474");

  AESCryptographyService AESCryptographyService;

  JCAAESCryptographyServiceTest() throws DecoderException {}

  @BeforeEach
  void setUp() {
    AESCryptographyService = new JCAAESCryptographyService();
  }

  @Test
  void producesNotNullWhenEncrypting() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CTR,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted, is(notNullValue()));
  }

  @Test
  void producesNotEmptyWhenEncrypting() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CTR,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted.length, is(greaterThan(0)));
  }

  @Test
  void throwsIllegalArgumentExceptionWhenEncryptingAndInvalidIVSize() {
    // Given initialization vector of invalid size (= 64 bits)
    final byte[] INITIALIZATION_VECTOR_KLMNOPQR_64_BITS =
        "KLMNOPQR".getBytes(StandardCharsets.UTF_8);

    // When encrypting AES with invalid IV size
    // Then throws IllegalArgumentException
    assertThrows(IllegalArgumentException.class, () -> {
      AESCryptographyService.encrypt(
          CTR,
          SECRET_KEY_1234567890123456_128_BITS,
          INITIALIZATION_VECTOR_KLMNOPQR_64_BITS,
          CLEAR_MESSAGE);
    });
  }

  @Test
  void producesSizeOfEncryptedEqualsToSizeOfClearContentWhenEncryptingAndBlockModeCFB() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted.length, is(equalTo(CLEAR_MESSAGE.length)));
  }

  @Test
  void producesSizeOfEncryptedEqualsToSizeOfClearContentWhenEncryptingAndBlockModeCBC() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CBC,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(
        encrypted.length,
        is(equalTo(CLEAR_MESSAGE.length + (16 - CLEAR_MESSAGE.length % 16))));
  }

  @Test
  void producesSizeOfEncryptedEqualsToSizeOfClearContentWhenEncryptingAndBlockModeOFB() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            OFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted.length, is(equalTo(CLEAR_MESSAGE.length)));
  }

  @Test
  void producesSizeOfEncryptedEqualsToSizeOfClearContentWhenEncryptingAndBlockModeCTR() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CTR,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted.length, is(equalTo(CLEAR_MESSAGE.length)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncrypting_CBC_PKCS5PADDING() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CBC,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted, is(equalTo(ENCRYPTED_MESSAGE_AES_CBC)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncrypting_CFB_NOPADDING() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted, is(equalTo(ENCRYPTED_MESSAGE_AES_CFB)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncrypting_OFB_NOPADDING() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            OFB,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted, is(equalTo(ENCRYPTED_MESSAGE_AES_OFB)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncryptingAES_CTR_NOPADDING() {
    // When encrypting
    var encrypted =
        AESCryptographyService.encrypt(
            CTR,
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_MESSAGE);

    // Then
    assertThat(encrypted, is(equalTo(ENCRYPTED_MESSAGE_AES_CTR)));
  }
}

