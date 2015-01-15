package de.zaunkoenigweg.m2e.pojomaker.core;

import java.io.File;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenProjectUtils;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.AbstractSourcesGenerationProjectConfigurator;
import org.eclipse.m2e.jdt.IClasspathDescriptor;

/**
 * Configures projects that use pojomaker-maven-plugin.
 * 
 * @author Nikolaus Winter
 */
public class PojoMakerProjectConfigurator extends AbstractSourcesGenerationProjectConfigurator {

    private static final String CONFIG_PARAM_OUTPUT_DIRECTORY = "pojoClassesFolder";
    private static final String GOAL_GENERATE_POJO_CLASSES = "generatePojoClasses";

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution, IPluginExecutionMetadata executionMetadata) {
        return new PojoMakerBuildParticipant(execution);
    }

    /**
     * Configures JDT project classpath.
     */
    @Override
    public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath, IProgressMonitor monitor) throws CoreException {

        IMavenProjectFacade facade = request.getMavenProjectFacade();
        MavenProject mavenProject = facade.getMavenProject();
        IProject project = facade.getProject();

        addNature(request.getProject(), JavaCore.NATURE_ID, monitor);

        for (MojoExecution execution : getMojoExecutions(request, monitor)) {
            if (GOAL_GENERATE_POJO_CLASSES.equals(execution.getGoal())) {
                File outputDirectory = maven.getMojoParameterValue(mavenProject, execution, CONFIG_PARAM_OUTPUT_DIRECTORY, File.class,
                        new NullProgressMonitor());
                IPath relativeSourcePath = MavenProjectUtils.getProjectRelativePath(project, outputDirectory.getAbsolutePath());
                classpath.addSourceEntry(project.getFullPath().append(relativeSourcePath), facade.getOutputLocation(), true);
            }
        }
    }

}
