/*
 * Copyright (C) 2015 Macros reply GmbH
 * Created by Macros reply GmbH - Team.
 *
 */

package org.rapidpm.microservice.propertyservice.startup.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.rapidpm.microservice.optionals.cli.CmdLineStartupAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
 *
 * Created by RapidPM - Team on 17.05.16.
 */
public class LoadPropertiesStartupAction implements CmdLineStartupAction {

  public static final String OPT = "i";

  @Override
  public List<Option> getOptions() {
    return Arrays.asList(new Option(OPT, true, "path to properties"));
  }

  @Override
  public void execute(CommandLine commandLine) {
    String optionValue = commandLine.getOptionValue(OPT);
    if (commandLine.hasOption(OPT) && !optionValue.isEmpty()) {
      System.out.println("read properties from file: " + optionValue);
      readProperties(optionValue);
    }
  }

  private void readProperties(String optionValue) {
    final File file = new File(optionValue);
    if (file.exists() && file.canRead()) {
      try (final FileInputStream fileInputStream = new FileInputStream(file)) {
        System.getProperties().load(fileInputStream);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
