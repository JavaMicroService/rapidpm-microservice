package org.rapidpm.microservice.propertyservice.api;

import org.rapidpm.proxybuilder.staticgenerated.annotations.StaticMetricsProxy;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@StaticMetricsProxy
public interface PropertyService {
  void init(@Nullable String source);

  void initFromCmd();

  String loadProperties(String scope);

  String getSingleProperty(String property);

  Set<String> getIndexOfLoadedProperties();

  Set<String> getIndexOfScope(String scope);

  Map<String, String> getPropertiesOfScope(String scope);

  boolean isRunning();

  void forget();

  void shutdown();

  File getConfigurationFile(String filename) throws IOException;
}
