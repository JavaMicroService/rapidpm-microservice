/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package junit.org.rapidpm.microservice.propertyservice.persistence.file.v002;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.microservice.propertyservice.persistence.PropertiesLoader;
import org.rapidpm.microservice.propertyservice.persistence.file.PropertiesFileLoader;

import java.util.Map;

public class PropertiesFileLoaderTest002 {

  private PropertiesLoader fileLoader;

  @BeforeEach
  public void setUp() throws Exception {
    fileLoader = new PropertiesFileLoader();
  }

  @Test
  public void test001() throws Exception {
    final Map<String, String> properties = fileLoader.load(this.getClass().getResource("").getPath(), "scope01");

    Assertions.assertNotNull(properties);
    Assertions.assertTrue(properties.size() > 0);
    Assertions.assertTrue(properties.containsKey("scope01.001"));
    Assertions.assertEquals("001", properties.get("scope01.001"));
  }

  @Test
  public void test002() throws Exception {
    final Map<String, String> scope01 = fileLoader.load(this.getClass().getResource("").getPath(), "scope01");
    final Map<String, String> scope02 = fileLoader.load(this.getClass().getResource("").getPath(), "scope02");

    Assertions.assertNotNull(scope01);
    Assertions.assertTrue(scope01.size() == 2);
    Assertions.assertNotNull(scope02);
    Assertions.assertTrue(scope02.size() == 1);
  }

}
