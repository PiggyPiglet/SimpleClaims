package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Constants;
import org.bson.BsonValue;
import org.bson.json.JsonWriter;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;

public class FileUtils {

    public static String MAIN_PATH = Constants.UNIVERSE_PATH.resolve("SimpleClaims").toAbsolutePath().toString();
    public static String PARTY_PATH = MAIN_PATH + File.separator + "Parties.json";
    public static String CLAIM_PATH = MAIN_PATH + File.separator + "Claims.json";
    public static String NAMES_CACHE_PATH = MAIN_PATH + File.separator + "NameCache.json";
    public static String ADMIN_OVERRIDES_PATH = MAIN_PATH + File.separator + "AdminOverrides.json";

    public static void ensureDirectory(String path){
        var file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void ensureMainDirectory(){
        ensureDirectory(MAIN_PATH);
    }

    public static File ensureFile(String path, String defaultContent){
        var file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                var writer = new FileWriter(file);
                writer.write(defaultContent);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void backupFile(String path) {
        var file = new File(path);
        if (file.exists()) {
            try {
                Files.copy(file.toPath(), Paths.get(path + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean restoreFromBackup(String path) {
        var backupFile = new File(path + ".bak");
        if (backupFile.exists()) {
            try {
                Files.copy(backupFile.toPath(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void loadWithBackup(Runnable loadRunnable, String path, HytaleLogger logger) {
        try {
            loadRunnable.run();
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("LOADING FILE ERROR: " + path + ", trying backup...");
            if (restoreFromBackup(path)) {
                try {
                    loadRunnable.run();
                } catch (Exception ex) {
                    logger.at(Level.SEVERE).log("LOADING BACKUP FILE ERROR: " + path);
                    ex.printStackTrace();
                    throw ex;
                }
            } else {
                logger.at(Level.SEVERE).log("NO BACKUP FOUND FOR: " + path);
                e.printStackTrace();
            }
        }
    }

}
