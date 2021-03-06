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
package junit.org.rapidpm.microservice.rest.optionals.properties.startup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import junit.org.rapidpm.microservice.rest.optionals.properties.impl.BaseDITest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.rest.optionals.properties.api.PropertiesStore;
import org.rapidpm.microservice.rest.optionals.properties.startup.PropertyFileStartupAction;

import java.util.Optional;


/**
 * Copyright (C) 2010 RapidPM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by RapidPM - Team on 16.12.2016.
 */
public class PropertyFileStartupActionTest extends BaseDITest {

  public static final String TESTFILE_PATH = "testfile";

  @AfterEach
  public void tearDown() throws Exception {
    super.tearDown();
    Main.stop();
    System.clearProperty(PropertiesStore.PROPERTYFILE);
  }

  @Test
  public void test001() throws Exception {
    Main.deploy(Optional.of(new String[]{"-" + PropertyFileStartupAction.COMMAND_SHORT + "=" + TESTFILE_PATH}));
    assertNotNull(System.getProperty(PropertiesStore.PROPERTYFILE));
    assertEquals(TESTFILE_PATH, System.getProperty(PropertiesStore.PROPERTYFILE));
  }

  @Test
  public void test002() throws Exception {
    Main.deploy(Optional.of(new String[]{"-" + PropertyFileStartupAction.COMMAND_LONG + "=" + TESTFILE_PATH}));
    assertNotNull(System.getProperty(PropertiesStore.PROPERTYFILE));
    assertEquals(TESTFILE_PATH, System.getProperty(PropertiesStore.PROPERTYFILE));
  }

}
