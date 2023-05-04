package msifeed.makriva.sync;

import msifeed.makriva.model.Shape;
import msifeed.makriva.storage.CheckedBytes;

import java.util.Map;
import java.util.UUID;

public interface INetworkBridge {
    void upload(Shape shape);

    void relayToAll(UUID uuid, CheckedBytes encodedShape);
}
