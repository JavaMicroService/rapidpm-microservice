package org.rapidpm.microservice.propertyservice.persistence.file;


import org.rapidpm.microservice.propertyservice.persistence.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationFileLoader implements ConfigurationLoader {

  @Override
  public File loadConfigurationFile(String filename) throws IOException {
    final String basepath = System.getProperty("propertyservice.configfolder");
    Path path = Paths.get(basepath + "/" + filename);
    return path.toFile();
  }
}
