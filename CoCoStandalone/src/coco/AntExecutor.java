package coco;

import java.io.File;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class AntExecutor {
	private String[][] properties;
	private Project project;
	
	public AntExecutor(String pathAnt, String afm2cocoModel, String cocoModel) {
		File file = new File(pathAnt);
		project = new Project();
		project.init();
		
		DefaultLogger logger = new DefaultLogger();
		logger.setErrorPrintStream(System.err);
		logger.setOutputPrintStream(System.err);
		logger.setMessageOutputLevel(Project.MSG_INFO);
		
		project.setProperty("ant.file", file.getAbsolutePath());
		project.setProperty("afm2cocoModel", afm2cocoModel);
		project.setProperty("cocoModel", cocoModel);
		project.addBuildListener(logger);
		project.fireBuildStarted();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		project.addReference("ant.projectHelper", helper);
		helper.parse(project, file);
		project.executeTarget(project.getDefaultTarget());
	}
	
	public AntExecutor(String pathAnt, String[][] properties) {
		this.properties = properties;
		File file = new File(pathAnt);
		project = new Project();
		project.init();
		
		DefaultLogger logger = new DefaultLogger();
		logger.setErrorPrintStream(System.err);
		logger.setOutputPrintStream(System.err);
		logger.setMessageOutputLevel(Project.MSG_INFO);
		
		project.setProperty("ant.file", file.getAbsolutePath());
		setProperties();
		project.addBuildListener(logger);
		project.fireBuildStarted();
		
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		project.addReference("ant.projectHelper", helper);
		helper.parse(project, file);
		project.executeTarget(project.getDefaultTarget());
	}
	
	private void setProperties(){
		if(properties != null) {
			for(int i = 0; i < properties.length; i++) {
				project.setProperty(properties[i][0], properties[i][1]);
				System.out.println("Propiedades! " + properties[i][0] + properties[i][1]);
			}
		}
	}
	
}
