package com.dt.lBT;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SimpleConfig implements Config {

    public String path;

    private final File file;

    private YamlConfiguration cfg;

    public SimpleConfig(String name, String path) {
        this.path = path;
        (new File(path)).mkdir();
        this.file = new File(path, name);
        if (!this.file.exists())
            try {
                Main.getInstance().saveResource(name, true);
                this.cfg = YamlConfiguration.loadConfiguration(this.file);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean exists() {
        return this.file.exists();
    }

    public void delete() {
        this.file.delete();
        this.cfg = null;
    }

    public YamlConfiguration getConfig() {
        if (this.cfg == null)
            this.cfg = YamlConfiguration.loadConfiguration(this.file);
        return this.cfg;
    }

    public void save() {
        try {
            this.cfg.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return this.file;
    }

    public void reload() {
        System.out.println("Reloading config: " + file.getAbsolutePath());
        if (this.file.exists()) {
            this.cfg = YamlConfiguration.loadConfiguration(this.file);
            System.out.println("Config reloaded successfully.");
        } else {
            System.err.println("Config file not found: " + file.getName());
            try {
                Main.getInstance().saveResource(file.getName(), true);
                this.cfg = YamlConfiguration.loadConfiguration(this.file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
