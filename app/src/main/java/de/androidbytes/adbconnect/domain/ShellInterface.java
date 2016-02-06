package de.androidbytes.adbconnect.domain;


import java.util.List;

import eu.chainfire.libsuperuser.Shell;


/**
 * Created by Christoph on 21.09.2015.
 */
public class ShellInterface {

    public static boolean isRootAvailable() {
        return Shell.SU.available();
    }

    public static boolean executeRootCommands(String[] rootCommands) {
        List<String> result = Shell.SU.run(rootCommands);
        boolean isRootAvailable = result.size() > 0;
        return isRootAvailable;
    }

    public static List<String> executeShellCommands(String[] shellCommands) {
        return Shell.run("sh", shellCommands, null, false);
    }

}
