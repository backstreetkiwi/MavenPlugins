package de.zaunkoenigweg.maven.pojomaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generatePojoClasses", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class PojoMakerMojo extends AbstractMojo {

    @Component
    private MavenProject mavenProject;

    @Parameter
    private File pojoDefinitionFile;

    @Parameter
    private File pojoClassesFolder;

    @SuppressWarnings("unchecked")
    public void execute() throws MojoExecutionException, MojoFailureException {

        if (this.pojoDefinitionFile == null) {
            throw new MojoExecutionException("Es wurde kein POJO-File (Attribut pojoDefinitionFile) angegeben.");
        }

        if (!this.pojoDefinitionFile.exists()) {
            throw new MojoExecutionException(String.format("Das POJO-File '%s' existiert nicht.", this.pojoDefinitionFile));
        }

        if (this.pojoClassesFolder == null) {
            throw new MojoExecutionException("Es wurde kein Folder für die POJO-Klassen (Attribut pojoClassesFolder) angegeben.");
        }

        List<String> pojoDefinitionList = null;
        try {
            pojoDefinitionList = FileUtils.readLines(this.pojoDefinitionFile, "UTF-8");
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Das POJO-File '%s' konnte nicht gelesen werden.", this.pojoDefinitionFile), e);
        }

        if (pojoDefinitionList.isEmpty()) {
            throw new MojoExecutionException(String.format("Das POJO-File '%s' enthält keine Einträge.", this.pojoDefinitionFile));
        }

        getLog().info(String.format("Das POJO-File '%s' enthält %d Einträge.", this.pojoDefinitionFile, pojoDefinitionList.size()));

        this.pojoClassesFolder.mkdirs();
        try {
            FileUtils.cleanDirectory(this.pojoClassesFolder);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Das POJO-Class-Verzeichnis '%s' konnte nicht geleert werden.", this.pojoClassesFolder), e);
        }
        for (String pojoName : pojoDefinitionList) {
            createPojoClass(pojoName);
        }

        mavenProject.addCompileSourceRoot(this.pojoClassesFolder.getAbsolutePath());
    }

    private void createPojoClass(String name) {
        try {
            OutputStream fileOutputStream = new FileOutputStream(new File(this.pojoClassesFolder, String.format("%s.java", name)));
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.println(String.format("public class %s {", name));
            printWriter.println("}");
            printWriter.println("//" + new Date());
            printWriter.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
