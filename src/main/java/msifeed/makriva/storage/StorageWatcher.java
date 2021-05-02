package msifeed.makriva.storage;

import msifeed.makriva.Makriva;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class StorageWatcher implements Runnable {
    private final Path dir;
    private final WatchService watcher;
    private final ShapeStorage storage;

    public StorageWatcher(Path dir, ShapeStorage storage) throws IOException {
        this.dir = dir;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.storage = storage;

        dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

        Makriva.LOG.info("Watching shapes dir: " + dir.toAbsolutePath());
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                final WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    handleEvent(event);
                }

                final boolean valid = key.reset();
                if (!valid) {
                    key.cancel();
                    watcher.close();
                    break;
                }
            }
        } catch (InterruptedException e) {
            Makriva.LOG.info("Watcher interrupted", e);
        } catch (Exception e) {
            Makriva.LOG.error("Watcher excepted", e);
        }
    }

    private void handleEvent(WatchEvent<?> event) {
        final WatchEvent.Kind<?> kind = event.kind();
        final Path file = (Path) event.context();
        if (!file.toString().endsWith(".json")) return;

        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE) || kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            storage.loadShapeFile(dir.resolve(file));
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            storage.removeShape(file.toString());
        }
    }
}
