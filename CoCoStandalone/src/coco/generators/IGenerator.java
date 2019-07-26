package coco.generators;

public interface IGenerator {
	public void generateConfigurationProgram(String xmiPath, String targetPath);
	public boolean runConfigurationProgram();
}
