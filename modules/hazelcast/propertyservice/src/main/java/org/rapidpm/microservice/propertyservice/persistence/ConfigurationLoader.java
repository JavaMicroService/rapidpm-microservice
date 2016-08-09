package org.rapidpm.microservice.propertyservice.persistence;


import java.io.File;
import java.io.IOException;

public interface ConfigurationLoader {

  File loadConfigurationFile(String filename) throws IOException;

}
