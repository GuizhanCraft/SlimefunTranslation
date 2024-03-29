package net.guizhanss.slimefuntranslation.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.guizhanss.slimefuntranslation.SlimefunTranslation;

import lombok.experimental.UtilityClass;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public final class FileUtils {
    /**
     * List all the folders (excluding sub folders) in the given folder.
     *
     * @param folder the folder to search in
     * @return a list of all folders in the given folder
     */
    @Nonnull
    public static List<String> listFolders(@Nonnull File folder) {
        if (folder == null || !folder.isDirectory()) {
            return Collections.emptyList();
        }
        var files = folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                result.add(file.getName());
            }
        }
        return result;
    }

    /**
     * Lists all YAML files in the given folder.
     * The folder or files starts with a dot (.) or an underscore(_) will be ignored.
     *
     * @param folder the folder to search in
     * @return a list of all YAML files in the given folder
     */
    @Nonnull
    public static List<String> listYamlFiles(@Nonnull File folder) {
        return listYamlFiles(folder, "");
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    private static List<String> listYamlFiles(File folder, String path) {
        if (folder == null || !folder.isDirectory()) {
            return Collections.emptyList();
        }
        var files = folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        for (File file : files) {
            final String filename = file.getName();
            if (filename.startsWith(".") || filename.startsWith("_")) {
                continue;
            }
            if (file.isDirectory()) {
                String subFolderPath = path + filename + File.separator;
                result.addAll(listYamlFiles(file, subFolderPath));
            } else {
                if (filename.endsWith(".yml") || filename.endsWith(".yaml")) {
                    result.add(path + filename);
                }
            }
        }
        return result;
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public List<String> listYamlFilesInJar(File jarFile, String folderPath) {
        if (jarFile == null || !jarFile.isFile()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        try (JarInputStream stream = new JarInputStream(new FileInputStream(jarFile))) {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                String entryName = entry.getName();
                String filename = entryName.substring(entryName.lastIndexOf("/") + 1);
                // Check if it's a .yml file and is under the "translations" folder
                if (entryName.startsWith(folderPath) && filename.endsWith(".yml") && !entry.isDirectory()) {
                    result.add(entryName.replace(folderPath, ""));
                }
            }
        } catch (IOException ex) {
            SlimefunTranslation.log(Level.SEVERE, ex, "An error has occurred while listing YAML files in jar file {0}", jarFile.getName());
        }
        return result;
    }
}
