package coco.generators;

import java.io.File;

import coco.util.AntExecutor;

public class CSPGenerator implements IGenerator {
	
	private String xmiPath;
	private String targetPath;
	
	public CSPGenerator() {
		
	}
	
	@Override
	public void generateConfigurationProgram(String xmiPath, String targetPath) {
		this.xmiPath = xmiPath;
		this.targetPath = targetPath;
		
		File cocoModel = new File(xmiPath);
		File chocoModel = new File(targetPath);
		
		String[][] properties = new String[2][2];
		properties[0][0] = "cocoModel";
		properties[0][1] = cocoModel.getPath();
		properties[1][0] = "cocoCP";
		properties[1][1] = chocoModel.getPath();
		
		AntExecutor antExecutor = new AntExecutor("workflow/build-coco2chocoM2T.xml", properties);	
		antExecutor.executeAnt();
	}

	@Override
	public boolean runConfigurationProgram() {
		boolean run = false;
		
		if(this.targetPath != null){
			File chocoModel = new File(this.targetPath);
			
			String[][] properties = new String[1][2];
			properties[0][0] = "cocoCP";
			properties[0][1] = chocoModel.getPath();
			
			AntExecutor antExecutor = new AntExecutor("workflow/build-javaExecution.xml", null);	
			antExecutor.executeAnt();
		}
		
		return run;
	}

}
