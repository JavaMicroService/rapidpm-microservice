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
package org.rapidpm.microservice.propertyservice.impl;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.rapidpm.microservice.propertyservice.api.PropertyService;
import org.rapidpm.microservice.propertyservice.persistence.ConfigurationLoader;
import org.rapidpm.microservice.propertyservice.persistence.PropertiesLoader;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertyServiceImpl implements PropertyService {

  public static final String DEFAULT_MAPNAME = "properties";
  public static final String MAPNAME = "mapname";
  public static final String RAPID_PM_PROPERTY_SERVICE = "RapidPMPropertyService";
  private HazelcastInstance hazelcastInstance;
  private Map<String, String> properties;
  private boolean isRunning = false;

  @Inject
  PropertiesLoader propertiesLoader;
  @Inject
  ConfigurationLoader configurationLoader;

  @Override
  public void init(@Nullable String source) {
    if (System.getProperty("propertyservice.distributed", "") == "true") {
      hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(getDefaultConfig());
      properties = hazelcastInstance.getReplicatedMap(getMapName());
    } else {
      properties = new HashMap<>();
    }

    if (source != null && !source.isEmpty() && properties.isEmpty())
      properties.putAll(propertiesLoader.load(source));
    isRunning = true;
  }

  @Override
  public void initFromCmd() {
    if (System.getProperty("distributed", "") == "true") {
      hazelcastInstance = Hazelcast.getOrCreateHazelcastInstance(getDefaultConfig());
      properties = hazelcastInstance.getReplicatedMap(getMapName());
    } else {
      properties = new HashMap<>();
    }
    isRunning = true;
  }

  private Config getDefaultConfig() {
    final Config config = new Config();
    config.setInstanceName(RAPID_PM_PROPERTY_SERVICE);
    config.setProperty("hazelcast.network.tcpip", "true");
    config.setProperty("hazelcast.network.multicast", "false");
    return config;
  }

  @Override
  public String loadProperties(String scope) {
    if (!isRunning) {
      initFromCmd();
    }
    properties.putAll(propertiesLoader.load(System.getProperty("propertyservice.propertyfolder"), scope));

    return "success";
  }

  @Override
  public String getSingleProperty(String property) {
    // Todo return something useful or make optional
    return properties.getOrDefault(property , "");
  }

  @Override
  public Set<String> getIndexOfLoadedProperties() {
    return properties.keySet();
  }

  @Override
  public Set<String> getIndexOfScope(String scope) {
    return properties.keySet()
        .stream()
        .filter(key -> key.startsWith(scope))
        .collect(Collectors.toSet());
  }

  @Override
  public Map<String, String> getPropertiesOfScope(String scope) {
    final Map<String, String> domainProperties = new HashMap<>();
    properties.keySet()
        .stream()
        .filter(key -> key.startsWith(scope))
        .forEach(key -> domainProperties.put(key, properties.get(key)));
    return domainProperties;
  }

  @Override
  public void forget() {
    if (properties != null && !properties.isEmpty())
      properties.clear();
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public void shutdown() {
    if (hazelcastInstance != null)
      hazelcastInstance.shutdown();
  }

  @Override
  public File getConfigurationFile(String filename) throws IOException {
    return configurationLoader.loadConfigurationFile(filename);
  }

  private String getMapName() {
    if (System.getProperty(MAPNAME) != null)
      return System.getProperty("propertyservice.mapname");
    else
      return DEFAULT_MAPNAME;
  }

}
