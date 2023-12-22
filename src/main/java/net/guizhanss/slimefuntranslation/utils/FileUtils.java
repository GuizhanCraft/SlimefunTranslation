package net.guizhanss.slimefuntranslation.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import lombok.experimental.UtilityClass;

@SuppressWarnings("ConstantConditions")
@UtilityClass
public final class FileUtils {
    /**
     * Lists all YAML files in the given folder.
     * The folder or files starts with a dot (.) or an underscore(_) will be ignored.
     *
     * @param folder
     *     the folder to search in
     *
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
            String filename = file.getName();
            if (filename.startsWith(".") || filename.startsWith("_")) {
                continue;
            }
            if (file.isDirectory()) {
                String subFolderPath = path + filename + "/";
                result.addAll(listYamlFiles(file, subFolderPath));
            } else {
                if (filename.endsWith(".yml") || filename.endsWith(".yaml")) {
                    result.add(path + filename);
                }
            }
        }
        return result;
    }
}
