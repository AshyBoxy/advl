package xyz.ashyboxy.advl.gradle;

import com.google.gson.Gson;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;

import java.util.Map;

public class AdvlGradlePlugin implements Plugin<Project> {
    public static Gson gson = new Gson();

    @Override
    public void apply(Project target) {
        target.getLogger().lifecycle("Hello from AdvlGradlePlugin");
        target.apply(Map.of("plugin", "java-library"));

        // thanks again loom
        target.getRepositories().maven(repo -> {
            repo.setName("mojang");
            repo.setUrl("https://libraries.minecraft.net/");
            repo.metadataSources(sources -> {
				sources.mavenPom();
				sources.artifact();
				sources.ignoreGradleMetadataRedirection();
			});
            repo.artifactUrls(ArtifactRepositoryContainer.MAVEN_CENTRAL_URL);
        });
        target.getRepositories().mavenCentral();

        MinecraftMetadata.init(target);
        MinecraftMetadata.download();
        MinecraftMetadata.apply(target);

//        target.getExtensions().create("advlmc", AdvlGradleExtension.class);
    }
}
