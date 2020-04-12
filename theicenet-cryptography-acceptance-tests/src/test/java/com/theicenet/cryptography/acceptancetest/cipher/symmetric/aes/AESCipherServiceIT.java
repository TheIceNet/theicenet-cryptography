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
package com.theicenet.cryptography.acceptancetest.cipher.symmetric.aes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.theicenet.cryptography.cipher.symmetric.SymmetricIVBasedCipherService;
import com.theicenet.cryptography.cipher.symmetric.SymmetricNonIVBasedCipherService;
import com.theicenet.cryptography.test.support.HexUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Juan Fidalgo
 */
@SpringBootTest
class AESCipherServiceIT {

  final String AES = "AES";

  final byte[] CLEAR_CONTENT =
      "Content to encrypt with AES and different options for block cipher mode of operation"
          .getBytes(StandardCharsets.UTF_8);

  final SecretKey SECRET_KEY_1234567890123456_128_BITS =
      new SecretKeySpec(
          "1234567890123456".getBytes(StandardCharsets.UTF_8),
          AES);

  final byte[] INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS =
      "KLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.UTF_8);

  final byte[] ENCRYPTED_CONTENT_AES_CFB =
      HexUtil.decodeHex(
          "813d91455835f9650de0506a0cbc9126d4c171c5e"
              + "fc1c3c7137e9d2fb2f711897b3261d0f760243583"
              + "5a693ab44f52b0e51c889504655b6a88c64c446b6"
              + "669dfc61c082e932ec53767b3de363beb10fa3ceb"
              + "2ed8");

  final byte[] ENCRYPTED_CONTENT_AES_ECB =
      HexUtil.decodeHex(
          "1f28432db0cb9a41a18068300e9731fc816b36e9b78d803e8ad1d7828ab8c"
              + "eef25722793b8c8e0b3a4c72f12ded24ea264d2c988f17d8d44c249"
              + "b3f8e588b41a7ab826fc440227e99ae6e1df2d50b4b00fce059bc32"
              + "c93e9fd7c5938327e38ab");

  @Autowired
  @Qualifier("AESIVBasedCipher")
  SymmetricIVBasedCipherService aesIVBasedCipherService;

  @Autowired
  @Qualifier("AESNonIVBasedCipher")
  SymmetricNonIVBasedCipherService aesCipherService;

  @Test
  void producesTheRightEncryptedResultWhenEncryptingWithCFBAndByteArray() {
    // When
    final var encrypted =
        aesIVBasedCipherService.encrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            CLEAR_CONTENT);

    // Then
    assertThat(encrypted, is(equalTo(ENCRYPTED_CONTENT_AES_CFB)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncryptingWithCFBAndStream() {
    // Given
    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

    // When
    aesIVBasedCipherService.encrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
        clearInputStream,
        encryptedOutputStream);

    // Then
    assertThat(encryptedOutputStream.toByteArray(), is(equalTo(ENCRYPTED_CONTENT_AES_CFB)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncryptingWithECBAndByteArray() {
    // When
    final var encrypted =
        aesCipherService.encrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            CLEAR_CONTENT);

    // Then
    assertThat(encrypted, is(equalTo(ENCRYPTED_CONTENT_AES_ECB)));
  }

  @Test
  void producesTheRightEncryptedResultWhenEncryptingWithECBAndStream() {
    // Given
    final var clearInputStream = new ByteArrayInputStream(CLEAR_CONTENT);
    final var encryptedOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.encrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        clearInputStream,
        encryptedOutputStream);

    // Then
    assertThat(encryptedOutputStream.toByteArray(), is(equalTo(ENCRYPTED_CONTENT_AES_ECB)));
  }

  @Test
  void producesTheRightDecryptedResultWhenDecryptingWithCFBAndByteArray() {
    // When
    final var decrypted =
        aesIVBasedCipherService.decrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
            ENCRYPTED_CONTENT_AES_CFB);

    // Then
    assertThat(decrypted, is(equalTo(CLEAR_CONTENT)));
  }

  @Test
  void producesTheRightDecryptedResultWhenDecryptingWithCFBAndStream() {
    // Given
    final var encryptedInputStream = new ByteArrayInputStream(ENCRYPTED_CONTENT_AES_CFB);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When
    aesIVBasedCipherService.decrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        INITIALIZATION_VECTOR_KLMNOPQRSTUVWXYZ_128_BITS,
        encryptedInputStream,
        clearOutputStream);

    // Then
    assertThat(clearOutputStream.toByteArray(), is(equalTo(CLEAR_CONTENT)));
  }

  @Test
  void producesTheRightDecryptedResultWhenDecryptingWithECBAndByteArray() {
    // When
    final var decrypted =
        aesCipherService.decrypt(
            SECRET_KEY_1234567890123456_128_BITS,
            ENCRYPTED_CONTENT_AES_ECB);

    // Then
    assertThat(decrypted, is(equalTo(CLEAR_CONTENT)));
  }

  @Test
  void producesTheRightDecryptedResultWhenDecryptingWithECBAndStream() {
    // Given
    final var encryptedInputStream = new ByteArrayInputStream(ENCRYPTED_CONTENT_AES_ECB);
    final var clearOutputStream = new ByteArrayOutputStream();

    // When
    aesCipherService.decrypt(
        SECRET_KEY_1234567890123456_128_BITS,
        encryptedInputStream,
        clearOutputStream);

    // Then
    assertThat(clearOutputStream.toByteArray(), is(equalTo(CLEAR_CONTENT)));
  }
}