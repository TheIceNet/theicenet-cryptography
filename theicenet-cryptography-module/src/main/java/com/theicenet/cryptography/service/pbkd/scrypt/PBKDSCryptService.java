package com.theicenet.cryptography.service.pbkd.scrypt;

import com.theicenet.cryptography.service.pbkd.PBKDKeyService;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.Validate;
import org.bouncycastle.crypto.generators.SCrypt;
import org.springframework.stereotype.Service;

@Service
public class PBKDSCryptService implements PBKDKeyService {

  private static final String SCRYPT = "SCrypt";

  private final SCryptConfiguration sCryptConfiguration;

  public PBKDSCryptService(SCryptConfiguration sCryptConfiguration) {
    this.sCryptConfiguration = sCryptConfiguration;
  }

  @Override
  public SecretKey generateKey(String password, byte[] salt, int keyLengthInBits) {
    Validate.notNull(password);
    Validate.notNull(salt);
    Validate.isTrue(keyLengthInBits > 0);

    return new SecretKeySpec(
        generateKey(
            password,
            salt,
            keyLengthInBits,
            sCryptConfiguration),
        SCRYPT);
  }

  private byte[] generateKey(
      String password,
      byte[] salt,
      int keyLengthInBits,
      SCryptConfiguration sCryptConfiguration) {

    return SCrypt.generate(
        password.getBytes(StandardCharsets.UTF_8),
        salt,
        sCryptConfiguration.getCpuMemoryCost(),
        sCryptConfiguration.getBlockSize(),
        sCryptConfiguration.getParallelization(),
        keyLengthInBits / 8);
  }
}
