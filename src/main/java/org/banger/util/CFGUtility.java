package org.banger.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class CFGUtility {

    private File file;
    private FileConfiguration cfg;

    public CFGUtility(Plugin plugin, String pfad){
        this(plugin.getDataFolder().getAbsolutePath() + "/" + pfad);
    }
    public CFGUtility(String pfad){
        this.file = new File(pfad);
        this.cfg = YamlConfiguration.loadConfiguration(this.file);

    }

    public boolean save(){
        try {
            this.cfg.save(this.file);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public File getFile(){
        return this.file;
    }

    public FileConfiguration getCfg(){
        return this.cfg;
    }

}
