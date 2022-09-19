package msifeed.makriva;

import msifeed.makriva.model.Shape;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class MakrivaConfig {
    private final Configuration config;

    public String shape;
    public float maxEyeHeight;
    public float minBBHeight;

    public MakrivaConfig(File cfgFile) {
        config = new Configuration(cfgFile);
        readConfig();
        config.save();
    }

    public void readConfig() {
        shape = getShapeProp().getString();
        maxEyeHeight = config.getFloat("maxEyeHeight", "server", 3.0f, 0, 16f, "Maximal height of player eye position");
        minBBHeight = config.getFloat("minBBHeight", "server", 0.4f, 0, 1.62f, "Minimal height of player custom bounding box");
    }

    public void setShape(String name) {
        shape = name;
        getShapeProp().setValue(name);
    }

    public Property getShapeProp() {
        return config.get("client", "shape", Shape.DEFAULT.name, "Current selected shape");
    }

    public void save() {
        config.save();
    }
}
