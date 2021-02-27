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
package com.theicenet.cryptography.acceptancetest.digest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import com.theicenet.cryptography.digest.DigestService;
import com.theicenet.cryptography.util.HexUtil;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Juan Fidalgo
 */
@SpringBootTest
class DigestServiceIT {

  final byte[] CONTENT =
      "Content to digest with different algorithm to check the digesting implementation is correct"
          .getBytes(StandardCharsets.UTF_8);

  final byte[] SHA_1_HASH =
      HexUtil.decodeHex("cc0639f168304020f9e8ab80961cf41c3b877d16");

  @Autowired
  DigestService digestService;

  @Test
  void producesTheRightHashWhenDigestingByteArray() {
    // When
    final var hash = digestService.digest(CONTENT);

    // Then
    assertThat(hash, is(equalTo(SHA_1_HASH)));
  }

  @Test
  void producesTheRightHashWhenDigestingStream() {
    // Given
    final var contentInputStream = new ByteArrayInputStream(CONTENT);

    // When
    final var hash = digestService.digest(contentInputStream);

    // Then
    assertThat(hash, is(equalTo(SHA_1_HASH)));
  }
}