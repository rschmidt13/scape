/*
 *  Copyright 2011 IMPACT (www.impact-project.eu)/SCAPE (www.scape-project.eu)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package eu.scape_project.xa.tw.util;

/**
 * StringConverterUtil
 * @author shsdev https://github.com/shsdev
 * @version 0.3
 */
public class StringConverterUtil {

   /**
     * Package name to namespace
     *
     * @param pn e.g. eu.scape_project.pc.services
     * @return e.g. http://scape-project.eu/pc/services
     */
    public static String packageNameToNamespace(String pn) {

        String newpn = pn;
        int ind = newpn.indexOf(".");
        String tld = newpn.substring(0, ind);
        newpn = newpn.substring(ind + 1, newpn.length());
        ind = newpn.indexOf(".");
        String domain = newpn.substring(0, ind);
        domain = domain.replace("_", "-");
        newpn = newpn.substring(ind + 1, newpn.length());
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(domain);
        sb.append(".");
        sb.append(tld);
        sb.append("/");
        while (newpn.indexOf(".") != -1) {
            ind = newpn.indexOf(".");
            String path = newpn.substring(0, ind);
            sb.append(path);
            sb.append("/");
            newpn = newpn.substring(ind + 1, newpn.length());
        }
        if(!newpn.isEmpty())
            sb.append(newpn);
        return (sb.toString());

    }


    /**
     * Converts package names to paths by replacing all periods, ".", with a forward slash.
     * @param pn the package name as a string
     * @return the package name as a path
     */
    public static String packageNameToPackagePath(String pn) {
        String pp = pn.replaceAll("\\.", "/");
        return pp;
    }

    /**
     * Converts between underscored variable names VAR_NAME and property forms, VAR.NAME.
     * Periods are not allowed in Java variable names.
     * 
     * @param var a variable name to be converted
     * @return the property name for the variable
     */
    public static String varToProp(String var) {
        String prop = var.replaceAll("_", ".");
        return prop;
    }

    /**
     * Converts between period separated property names and underscore separated variable names,
     * i.e. VAR.NAME becomes VAR_NAME
     * Periods are not allowed in Java variable names.
	 *
     * @param var the property name to convert
     * @return the converted variable name
     */
    public static String propToVar(String var) {
        String prop = var.replaceAll("\\.", "_");
        return prop;
    }

    /**
     * Converts a type name to a file name, replaces all colons with an underscore.
     * @param var the type name to convert
     * @return the file name
     */
    public static String typeToFilename(String var) {
        String ret = var.replaceAll(":", "_").toLowerCase();
        return ret;
    }

}
