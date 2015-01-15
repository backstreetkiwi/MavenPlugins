package de.zaunkoenigweg.m2e.pojomaker.core;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class PojoMakerBuildParticipant extends MojoExecutionBuildParticipant {

    private static final String CONFIG_PARAM_POJO_DEFINITION_FILE = "pojoDefinitionFile";
    private static final String CONFIG_PARAM_OUTPUT_DIRECTORY = "pojoClassesFolder";

    private static final String GOAL_GENERATE_POJO_CLASSES = "generatePojoClasses";

    public PojoMakerBuildParticipant(MojoExecution execution) {
        super(execution, true);
    }

    @Override
    public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception {

        final MojoExecution execution = getMojoExecution();

        if (execution == null) {
            return null;
        }

        if (!GOAL_GENERATE_POJO_CLASSES.equals(execution.getGoal())) {
            return null;
        }

        final IMaven maven = MavenPlugin.getMaven();
        final MavenProject mavenProject = getMavenProjectFacade().getMavenProject();
        final BuildContext buildContext = getBuildContext();

        final File outputDirectory = maven.getMojoParameterValue(mavenProject, execution, CONFIG_PARAM_OUTPUT_DIRECTORY, File.class, new NullProgressMonitor());
        final File pojoDefFile = maven.getMojoParameterValue(mavenProject, execution, CONFIG_PARAM_POJO_DEFINITION_FILE, File.class, new NullProgressMonitor());

        if (outputDirectory == null) {
            return null;
        }

        if (pojoDefFile == null || !pojoDefFile.exists() || !pojoDefFile.isFile()) {
            return null;
        }

        mavenProject.addCompileSourceRoot(outputDirectory.getAbsolutePath());

        if (!isMojoExecutionRequired(kind, buildContext, pojoDefFile)) {
            return null;
        }

        setTaskName(monitor);

        final Set<IProject> result = super.build(kind, monitor);

        if (outputDirectory != null && outputDirectory.exists()) {
            buildContext.refresh(outputDirectory);
        }

        return result;
    }

    /**
     * Mojo Execution is required ...
     * 
     * <ul>
     * <li>... during FULL or CLEAN builds</li>
     * <li>... otherwise only if the Pojo definition file has changed</li>
     * </ul>
     * 
     * @param kind see {@link MojoExecutionBuildParticipant#build(int, IProgressMonitor)}
     * @param buildContext Plexus Build Context
     * @param pojoDefinitionFile File containing POJO definitions
     * @return Is Mojo Execution required?
     */
    private boolean isMojoExecutionRequired(int kind, BuildContext buildContext, File pojoDefinitionFile) {
        if (MojoExecutionBuildParticipant.CLEAN_BUILD == kind || MojoExecutionBuildParticipant.FULL_BUILD == kind) {
            return true;
        }

        Scanner pojoFileScanner = buildContext.newScanner(pojoDefinitionFile);
        String[] includedFiles = null;
        if (pojoFileScanner != null) {
            pojoFileScanner.scan();
            includedFiles = pojoFileScanner.getIncludedFiles();
        }

        if (includedFiles != null && includedFiles.length > 0) {
            return true;
        }

        return false;
    }

    /**
     * Set appropriate task name for Eclipse progress monitor.
     * 
     * @param monitor Eclipse progress monitor
     * @throws CoreException
     */
    private void setTaskName(IProgressMonitor monitor) throws CoreException {
        if (monitor != null) {
            final String taskName = String.format("PojoMaker M2E: Invoking %s on %s", getMojoExecution().getMojoDescriptor().getFullGoalName(),
                    getMavenProjectFacade().getProject().getName());
            monitor.setTaskName(taskName);
        }
    }

}
