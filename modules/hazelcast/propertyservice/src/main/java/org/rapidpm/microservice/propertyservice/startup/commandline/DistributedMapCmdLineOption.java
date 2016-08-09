package org.rapidpm.microservice.propertyservice.startup.commandline;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.rapidpm.microservice.optionals.cli.CmdLineStartupAction;

import java.util.Collections;
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
public class DistributedMapCmdLineOption implements CmdLineStartupAction {

  public static final String OPT = "dm";

  @Override
  public List<Option> getOptions() {
    return Collections.singletonList(new Option(OPT, "distributedmap", false, "enable a shared map of properties"));
  }

  @Override
  public void execute(CommandLine commandLine) {
    System.setProperty("propertyservice.distributed", "true");
  }

}
