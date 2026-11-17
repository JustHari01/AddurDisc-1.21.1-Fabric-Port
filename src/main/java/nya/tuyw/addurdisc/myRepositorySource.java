package nya.tuyw.addurdisc;

import com.mojang.logging.LogUtils;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class myRepositorySource implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Path folder;
    protected final PackType packType;
    protected final PackSource packSource;
    protected final String idPrefix;

    public myRepositorySource(Path folder, PackType packType, PackSource packSource, String idPrefix) {
        this.folder = folder;
        this.packType = packType;
        this.packSource = packSource;
        this.idPrefix = idPrefix;
    }

    @Override
    public void loadPacks(Consumer<Pack> onLoad) {
        try {
            FileUtil.createDirectoriesSafe(folder);
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder)) {
                for (Path entry : entries) {
                    Pack.ResourcesSupplier resources = detectPackResources(entry);
                    if (resources == null) {
                        continue;
                    }

                    String name = entry.getFileName().toString();
                    PackLocationInfo location = new PackLocationInfo(
                            idPrefix + "/" + name,
                            Component.literal(name),
                            packSource,
                            Optional.empty()
                    );
                    Pack pack = Pack.readMetaAndCreate(
                            location,
                            resources,
                            packType,
                            new PackSelectionConfig(true, Pack.Position.TOP, false)
                    );
                    if (pack != null) {
                        onLoad.accept(pack);
                    }
                }
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to list packs in {}", folder, exception);
        }
    }

    private static Pack.ResourcesSupplier detectPackResources(Path path) {
        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (NoSuchFileException exception) {
            return null;
        } catch (IOException exception) {
            LOGGER.warn("Failed to read properties of '{}', ignoring", path, exception);
            return null;
        }

        if (attributes.isDirectory() && Files.isRegularFile(path.resolve("pack.mcmeta"))) {
            return directoryResources(path);
        }
        if (attributes.isRegularFile() && path.getFileName().toString().endsWith(".zip")) {
            return zipResources(path);
        }

        LOGGER.debug("Found non-pack entry '{}', ignoring", path);
        return null;
    }

    private static Pack.ResourcesSupplier directoryResources(Path root) {
        return new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo location) {
                return new PathPackResources(location, root);
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                return new PathPackResources(location, root);
            }
        };
    }

    private static Pack.ResourcesSupplier zipResources(Path archive) {
        return new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo location) {
                return openZip(location, archive);
            }

            @Override
            public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                return openZip(location, archive);
            }
        };
    }

    private static PackResources openZip(PackLocationInfo location, Path archive) {
        try {
            FileSystem fileSystem = FileSystems.newFileSystem(archive, Map.of());
            return new PathPackResources(location, fileSystem.getPath("/")) {
                @Override
                public void close() {
                    super.close();
                    try {
                        fileSystem.close();
                    } catch (IOException exception) {
                        LOGGER.warn("Failed to close resource pack archive {}", archive, exception);
                    }
                }
            };
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to open resource pack archive " + archive, exception);
        }
    }
}
