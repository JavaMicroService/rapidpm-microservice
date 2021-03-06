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
package junit.org.rapidpm.microservice.rest.optionals.properties.rest.v003;

import junit.org.rapidpm.microservice.BasicRestTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.rest.optionals.properties.rest.PropertyServiceRestEndpoint;
import org.rapidpm.microservice.rest.optionals.properties.startup.AuthenticationStartupAction;
import org.rapidpm.microservice.rest.optionals.properties.startup.PropertyFileStartupAction;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.io.File;

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
public class PropertyServiceRestEndpointTest003 extends BasicRestTest {

  public static final String GETPROPERTY = "getproperty";
  final File propertyFile = new File(this.getClass().getResource("test.properties").getFile());
  final File clientFile = new File(this.getClass().getResource("clients.txt").getFile());

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    DI.clearReflectionModel();
    DI.activatePackages("org.rapidpm");
    final Class<? extends BasicRestTest> aClass = this.getClass();
    DI.activatePackages(aClass);

    Main.deploy(java.util.Optional.of(new String[]{
        "-" + PropertyFileStartupAction.COMMAND_SHORT + "=" + propertyFile.getAbsolutePath(),
        "-" + AuthenticationStartupAction.COMMAND_SHORT + "=" + clientFile.getAbsolutePath()
    }));
  }

  @Test
  public void test001() throws Exception {
    // localhost not allowed to request property
    final String generateBasicReqURL = generateBasicReqURL(PropertyServiceRestEndpoint.class);
    Client client = ClientBuilder.newClient();
    final Invocation.Builder request = client
        .target(generateBasicReqURL)
        .path(GETPROPERTY)
        .queryParam("name", "test.001")
        .request();
    final Response response = request.get();
    Assertions.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    Assertions.assertEquals("client <127.0.0.1> not allowed", response.readEntity(String.class));
  }

}
