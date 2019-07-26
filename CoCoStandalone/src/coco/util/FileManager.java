package coco.util;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.util.FileUtils;

public class FileManager {
	
	public FileManager(){
		
	}
	
	public boolean copyFile(File source, File target){
		boolean copied = false;
		
		try {
			FileUtils utils = FileUtils.getFileUtils();
			utils.copyFile(source, target);
			System.out.println("copiado");
			copied = true;
		} 
		
		catch (IOException e) {
			return copied;
		}
		
		return copied;
	}
}
