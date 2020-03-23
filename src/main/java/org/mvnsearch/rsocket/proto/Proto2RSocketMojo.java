package org.mvnsearch.rsocket.proto;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.mvnsearch.rsocket.proto.parser.ProtoFile;
import org.mvnsearch.rsocket.proto.parser.ProtoParser;
import org.mvnsearch.rsocket.proto.parser.ProtoService;

import java.io.File;
import java.io.IOException;

/**
 * Convert service in proto file  to RSocket Java Interface
 *
 * @author linux_china
 */
@Mojo(name = "proto2rsocket", defaultPhase = LifecyclePhase.COMPILE)
public class Proto2RSocketMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;
    @Parameter(property = "output")
    String output = "target/generated-sources/protobuf/java/";
    @Parameter(property = "source")
    String source = "src/main/proto";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            File outputDir = new File(project.getBasedir(), output);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            File sourceDir = new File(project.getBasedir(), source);
            if (!sourceDir.exists()) {
                throw new MojoExecutionException("Source directory not exists: " + source);
            }
            File[] protoFiles = sourceDir.listFiles((dir, name) -> name.endsWith(".proto"));
            if (protoFiles != null && protoFiles.length > 0) {
                for (File file : protoFiles) {
                    ProtoFile protoFile = ProtoParser.parseUtf8(file);
                    String packageName = protoFile.getPackageValue();
                    if (protoFile.getOptions().containsKey("java_package")) {
                        packageName = protoFile.getOptions().get("java_package");
                    }
                    if (packageName == null || packageName.isEmpty()) {
                        throw new MojoExecutionException("Please set package for " + file.getName());
                    }
                    for (ProtoService protoService : protoFile.getServices().values()) {
                        File targetDir = new File(outputDir, packageName.replaceAll("\\.", File.separator));
                        if (!targetDir.exists()) {
                            targetDir.mkdirs();
                        }
                        File interfaceFile = new File(targetDir, protoService.getName() + ".java");
                        FileUtils.fileWrite(interfaceFile, "UTF-8", protoService.toReactiveServiceDefinition(packageName));
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to execute plugin", e);
        }
    }

}
