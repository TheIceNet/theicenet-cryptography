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
package com.theicenet.cryptography;

import com.theicenet.cryptography.cipher.asymmetric.rsa.JCARSACipherService;
import com.theicenet.cryptography.cipher.asymmetric.rsa.RSAPadding;
import com.theicenet.cryptography.util.PropertiesUtil;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author Juan Fidalgo
 * @since 1.1.0
 */
public class RSADynamicContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>  {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {

    final ConfigurableEnvironment environment = applicationContext.getEnvironment();
    final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

    final Set<RSAPadding> paddings =
        PropertiesUtil.getProperty(
            environment,
            "cryptography.cipher.asymmetric.rsa.padding",
            RSAPadding.class);

    paddings.forEach(padding -> registerBean(beanFactory, padding));
  }

  private void registerBean(
      ConfigurableListableBeanFactory beanFactory,
      RSAPadding rsaPadding) {

    beanFactory.registerSingleton(
        String.format("%s_%s", "RSACipher", rsaPadding),
        new JCARSACipherService(rsaPadding));
  }
}
