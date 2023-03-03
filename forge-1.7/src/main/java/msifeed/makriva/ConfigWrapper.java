package msifeed.makriva;

import msifeed.makriva.config.ConfigData;
import msifeed.makriva.config.IConfigWrapper;
import msifeed.makriva.model.Shape;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigWrapper implements IConfigWrapper {
    private final Configuration config;
    private final ConfigData data = new ConfigData();

    public ConfigWrapper(File cfgFile) {
        config = new Configuration(cfgFile);
        read();
        config.save();
    }

    @Override
    public ConfigData get() {
        return data;
    }

    @Override
    public void selectShape(String name) {
        data.shape = name;
        getShapeProp().setValue(name);
        write();
    }

    @Override
    public void read() {
        data.shape = getShapeProp().getString();
        data.maxEyeHeight = config.getFloat("maxEyeHeight", "server", 3.0f, 0, 16f, "Maximal height of player eye position");
        data.minBBHeight = config.getFloat("minBBHeight", "server", 0.4f, 0, 1.62f, "Minimal height of player custom bounding box");
    }

    private Property getShapeProp() {
        return config.get("client", "shape", Shape.DEFAULT.name, "Current selected shape");
    }

    @Override
    public void write() {
        config.save();
    }
}
