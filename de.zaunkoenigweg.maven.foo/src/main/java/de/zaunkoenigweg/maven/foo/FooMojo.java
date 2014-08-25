package de.zaunkoenigweg.maven.foo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.plexus.build.incremental.BuildContext;

@Mojo(name="generateFooClasses")
public class FooMojo extends AbstractMojo {

	@Component
	private BuildContext plexusBuildContext;

	@Component
	private MavenProject mavenProject;
	
	@Parameter(defaultValue="${project.basedir}/src/main/resources/foo.txt")
	private File fooFile; 
	
	@Parameter(defaultValue="${project.build.directory}/foo")
	private File fooClassFolder;
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		try {
			File file = new File("/home/nikolaus/temp", String.format("Hugo%s.java", (new Date()).getTime()));
			PrintWriter printWriter = new PrintWriter(file);
			printWriter.println("test");
			printWriter.flush();
			printWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		getLog().info("I am the new Foo-Plugin");
//		getLog().info(String.format("This is where I expect the Foo-definition to be found: %s", this.fooFile));
//		getLog().info(String.format("This is where I put the classes: %s", this.fooClassFolder));
//
//		mavenProject.addCompileSourceRoot(this.fooClassFolder.getAbsolutePath());
//		
//		if (this.plexusBuildContext.hasDelta(fooFile)) {
//			try {
////				FileUtils.cleanDirectory(fooClassFolder);
//				OutputStream fileOutputStream = plexusBuildContext.newFileOutputStream(new File(this.fooClassFolder, String.format("Hugo%s.java", (new Date()).getTime())));
//				PrintWriter printWriter = new PrintWriter(fileOutputStream);
//				printWriter.println("Hallo");
//				printWriter.flush();
//				fileOutputStream.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
	}
	
	

}
