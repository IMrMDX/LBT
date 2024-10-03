package com.dt.lBT;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public interface Config {
    boolean exists();

    void delete();

    YamlConfiguration getConfig();

    void save();

    File getFile();
}
