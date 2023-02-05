package msifeed.makriva;

import msifeed.makriva.config.IConfigWrapper;
import msifeed.makriva.render.ModelManager;
import msifeed.makriva.storage.ShapeStorage;
import msifeed.makriva.sync.SharedShapes;
import msifeed.makriva.sync.SyncRelay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MakrivaShared {
    public static final String MOD_ID = "makriva";
    public static final Logger LOG = LogManager.getLogger(MOD_ID);

    public static final SharedShapes SHARED = new SharedShapes();
    public static IConfigWrapper CFG;
    public static SyncRelay RELAY;

    public static ShapeStorage STORAGE;
    public static ModelManager<?> MODELS;
}
