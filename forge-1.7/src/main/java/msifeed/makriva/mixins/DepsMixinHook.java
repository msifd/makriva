package msifeed.makriva.mixins;

import cpw.mods.fml.relauncher.CoreModManager;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DepsMixinHook implements IFMLLoadingPlugin {
    private final Logger LOG = LogManager.getLogger("Makriva-Deps-Hook");

    private final List<String> REFERENCES =
            Stream.of(
                            // "noppes.mpm.ModelData", // MPM
                            "noppes.mpm.data.ModelData" // MPM-Ariadna
                    )
                    .map(s -> s.replace('.', '/') + ".class")
                    .collect(Collectors.toList());

    @Override
    public void injectData(Map<String, Object> data) {
        if ((boolean) data.get("runtimeDeobfuscationEnabled")) {
            preloadDependencies(Paths.get("mods"));
        } else {
            LOG.info("Skip preload");
        }

        LOG.info("Add thirdparty mixins");
        Mixins.addConfiguration("mixins.thirdparty.makriva.json");
    }

    private void preloadDependencies(Path modsDir) {
        LOG.info("Preload dependency mods");

        if (!Files.isDirectory(modsDir)) {
            LOG.error("Missing mods dir");
            return;
        }

        try (Stream<Path> files = Files.walk(modsDir)) {
            files.filter(this::isJar).forEach(file -> preloadJar(modsDir, file));
        } catch (IOException e) {
            LOG.throwing(e);
        }
    }

    private boolean isJar(Path path) {
        final String name = path.toString();
        return name.endsWith(".jar") || name.endsWith(".zip");
    }

    private void preloadJar(Path modsDir, Path modFile) {
        final List<String> refs = findReferenceClasses(modFile);
        if (refs.isEmpty()) return;

        final Path jar = modsDir.relativize(modFile);
        LOG.info("Preload jar " + jar);
        REFERENCES.removeAll(refs);

        try {
            ((LaunchClassLoader) getClass().getClassLoader()).addURL(modFile.toUri().toURL());
            CoreModManager.getReparseableCoremods().add(jar.toString());
        } catch (Exception e) {
            LOG.warn("Failed to preload " + jar, e);
        }
    }

    private List<String> findReferenceClasses(Path path) {
        if (REFERENCES.isEmpty())
            return Collections.emptyList();

        try (FileSystem fs = FileSystems.newFileSystem(path, null)) {
            return REFERENCES.stream()
                    .filter(ref -> Files.exists(fs.getPath(ref)))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOG.warn(e);
        }

        return Collections.emptyList();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
