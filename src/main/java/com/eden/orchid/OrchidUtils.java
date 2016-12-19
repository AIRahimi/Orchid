package com.eden.orchid;

import com.eden.orchid.compilers.Compiler;
import com.eden.orchid.compilers.PreCompiler;
import com.eden.orchid.compilers.SiteCompilers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class OrchidUtils {

    /**
     * Returns the jar file used to load class clazz, or null if clazz was not loaded from a jar.
     *
     * @param clazz  the class to load a jar from
     * @return  the JarFile for a given class, or null if the class was not loaded from a jar
     */
    private static JarFile jarForClass(Class<?> clazz) {
        String path = "/" + clazz.getName().replace('.', '/') + ".class";
        URL jarUrl = clazz.getResource(path);
        if (jarUrl == null) {
            return null;
        }

        String url = jarUrl.toString();
        int bang = url.indexOf("!");
        String JAR_URI_PREFIX = "jar:file:";
        if (url.startsWith(JAR_URI_PREFIX) && bang != -1) {
            try {
                return new JarFile(url.substring(JAR_URI_PREFIX.length(), bang));
            }
            catch (IOException e) {
                throw new IllegalStateException("Error loading jar file.", e);
            }
        }
        else {
            return null;
        }
    }

    public static JSONArray compileResources(String resourceDir, Compiler compiler) {
        JSONArray writtenFileNames = new JSONArray();

        JSONArray themeFiles = compileThemeResources(resourceDir, compiler);
        JSONArray externalFiles = compileExternalResources(resourceDir, compiler);

        for(int i = 0; i < themeFiles.length(); i++) {
            writtenFileNames.put(themeFiles.get(i));
        }
        for(int i = 0; i < externalFiles.length(); i++) {
            writtenFileNames.put(externalFiles.get(i));
        }

        return writtenFileNames;
    }

    private static JSONArray compileThemeResources(String resourceDir, Compiler compiler) {
        JSONArray writtenFileNames = new JSONArray();

        JarFile fromJar = jarForClass(Orchid.theme.getClass());

        for (Enumeration<JarEntry> entries = fromJar.entries(); entries.hasMoreElements(); ) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().startsWith(resourceDir + File.separator) && !entry.isDirectory()) {

                if (FilenameUtils.isExtension(entry.getName(), compiler.getSourceExtensions())) {
                    String destFileName =
                            resourceDir
                                    + File.separator
                                    + FilenameUtils.removeExtension(entry.getName()).substring(resourceDir.length() + 1) // strip the jar resource path from the entry name
                                    + "."
                                    + compiler.getOutputExtension(); // replace the file extension

                    // Load the contents of the file
                    try {
                        String fileContents = IOUtils.toString(fromJar.getInputStream(entry), "UTF-8");

                        // Pass the file contents through the precompiler
                        PreCompiler preCompiler = SiteCompilers.getPrecompiler(Orchid.theme.getPrecompilerClass());

                        if (preCompiler != null) {
                            fileContents = preCompiler.compile(fileContents, new JSONObject().put("site", Orchid.root.getJSONObject("options")));
                        }

                        fileContents = compiler.compile(FilenameUtils.getExtension(entry.getName()), fileContents);

                        // Write the file to the output destination
                        writeFile(destFileName, fileContents);
                        writtenFileNames.put(destFileName);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return writtenFileNames;
    }

    private static JSONArray compileExternalResources(String resourceDir, Compiler compiler) {
        JSONArray writtenFileNames = new JSONArray();

        String resDir = Orchid.root.getJSONObject("options").getString("resourcesDir") + File.separator + resourceDir;

        File res = new File(resDir);

        if (res.isDirectory()) {
            List<File> files = new ArrayList<>(FileUtils.listFiles(res, compiler.getSourceExtensions(), true));

            for (File file : files) {

                String destFileName =
                        resourceDir
                                + File.separator
                                + FilenameUtils.removeExtension(file.getName())
                                + "."
                                + compiler.getOutputExtension();

                try {
                    // Load the contents of the file
                    String fileContents = IOUtils.toString(new FileInputStream(file), "UTF-8");

                    // Pass the file contents through the precompiler
                    PreCompiler preCompiler = SiteCompilers.getPrecompiler(Orchid.theme.getPrecompilerClass());

                    if(preCompiler != null) {
                        fileContents = preCompiler.compile(fileContents, new JSONObject().put("site", Orchid.root.getJSONObject("options")));
                    }

                    fileContents = compiler.compile(FilenameUtils.getExtension(file.getName()), fileContents);

                    // Write the file to the output destination
                    writeFile(destFileName, fileContents);
                    writtenFileNames.put(destFileName);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return writtenFileNames;
    }

    public static String getResourceFileContents(String fileName) {
        fileName = fileName.replaceAll("/", File.separator);
        // First attempt to load a file in the external resources directory
        if(!isEmpty(Orchid.root.getJSONObject("options").getString("resourcesDir"))) {
            File res = new File(Orchid.root.getJSONObject("options").getString("resourcesDir") + File.separator + fileName);

            if(res.exists() && !res.isDirectory()) {
                try {
                    return IOUtils.toString(new FileInputStream(res), "UTF-8");
                }
                catch(IOException e) {

                }
            }
        }

        // If we don't have the requested file in external resources, try to load the file from internal resources
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream(fileName);
        try {
            return IOUtils.toString(is, "UTF-8");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // We couldn't find the requested file
        return null;
    }

    public static void writeFile(String dest, String contents) {
        Path file = Paths.get(Orchid.root.getJSONObject("options").getString("outputDir") + "/" + dest);
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, contents.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if the string is null or empty. An empty string is defined to be either 0-length or all whitespace
     *
     * @param str the string to be examined
     * @return true if str is null or empty
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0 || str.toString().trim().length() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean acceptsExtension(String sourceExt, String[] acceptedExts) {
        for(String ext : acceptedExts) {
            if(ext.equalsIgnoreCase(sourceExt)) {
                return true;
            }
        }

        return false;
    }
}
