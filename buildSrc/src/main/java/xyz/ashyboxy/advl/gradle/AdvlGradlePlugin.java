package xyz.ashyboxy.advl.gradle;

import com.google.gson.Gson;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;

import java.io.IOException;
import java.util.Map;

public class AdvlGradlePlugin implements Plugin<Project> {
    public static Gson gson = new Gson();

    protected static Logger logger;

    @Override
    public void apply(Project target) {
        logger = target.getLogger();
        logger.lifecycle("Hello from AdvlGradlePlugin");
        target.apply(Map.of("plugin", "java-library"));

        // thanks again loom
        target.getRepositories().maven(repo -> {
            repo.setName("Mojang");
            repo.setUrl(Consts.MOJANG_LIBRARIES);
            repo.metadataSources(sources -> {
				sources.mavenPom();
				sources.artifact();
				sources.ignoreGradleMetadataRedirection();
			});
            repo.artifactUrls(ArtifactRepositoryContainer.MAVEN_CENTRAL_URL);
        });
        target.getRepositories().mavenCentral();

        try {
            NamedDomainObjectProvider<Configuration> mcRuntimeConfiguration = target.getConfigurations().register(Consts.MC_RUNTIME);
            mcRuntimeConfiguration.configure(c -> {
                c.setCanBeConsumed(false);
                c.setCanBeResolved(true);
            });
            target.getConfigurations().getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, c -> {
                c.extendsFrom(mcRuntimeConfiguration.get());
            });

            MinecraftFiles.init(target);
            MinecraftMetadata.download();
            MinecraftAssets.download();
            Remapper.maybeRemap(MinecraftFiles.jarPath, MinecraftFiles.remappedJarPath, MinecraftFiles.mappingsPath);
            MinecraftMetadata.apply(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TaskContainer tasks = target.getTasks();
        tasks.register("runMc", RunMcTask.class, t -> {});
        tasks.register("remapMc", RemapMcTask.class, t -> {});
        tasks.register("decompileMc", DecompileTask.class, t -> {});

//        target.getExtensions().create("advlmc", AdvlGradleExtension.class);
    }
}
