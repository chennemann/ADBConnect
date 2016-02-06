/**
 * Copyright (C) 2016 Christoph Hennemann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.androidbytes.adbconnect.domain;


import eu.chainfire.libsuperuser.Shell;

import java.util.List;



public class ShellInterface {

    public static boolean isRootAvailable() {
        return Shell.SU.available();
    }

    public static boolean executeRootCommands(String[] rootCommands) {
        List<String> result = Shell.SU.run(rootCommands);
        return result.size() > 0;
    }

    public static List<String> executeShellCommands(String[] shellCommands) {
        return Shell.run("sh", shellCommands, null, false);
    }

}
