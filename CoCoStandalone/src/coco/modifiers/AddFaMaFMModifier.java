package coco.modifiers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import coco.util.Util;

public class AddFaMaFMModifier implements IModifier{

	private String xmiPath;
	private String famaPath;
	
	public AddFaMaFMModifier(){
		
	}
	
	@Override
	public void modifyFSG(String xmiPath, String famaPath, boolean existing) {
		this.xmiPath = xmiPath;
		this.famaPath = famaPath;
		
		//File cocoModel = new File(this.xmiPath);
		File afm2cocoModel = createAfm2CoCoModel();
		/*
		//Loading Xtext models as Ecore models
		XtextModelManager xtextManager = new XtextModelManager(afm2cocoModel.getPath());
		xtextManager.loadXtextModelAsEcoreAfm2CoCo();
		System.out.println("AFM2COCO: " + afm2cocoModel.getPath());
		
		//Workflow responsible of translating FaMa models
		//to CoCo models.
		String[][] properties = new String[2][2];
		properties[0][0] = "afm2cocoModel";
		properties[0][1] = afm2cocoModel.getPath();
		properties[1][0] = "cocoModel";
		properties[1][1] = cocoModel.getPath();
		
		String workflow;
		if(existing){
			workflow = "workflow/build-fama2coco-existingModelM2M.xml";
		}
		else{
			workflow = "workflow/build-fama2cocoM2M.xml";
		}
		
		AntExecutor antExecutor = new AntExecutor(workflow, properties);
		antExecutor.executeAnt();
		*/
	}
	
	/**
	 * Creates an afm2coco file based on the fama model path. 
	 * The %Features line is also added. If the file cannot 
	 * be created it returns null.
	 * @return File - file for the afm2coco model
	 */
	private File createAfm2CoCoModel(){
		File afm2cocoModel = null;
		
		if(this.famaPath != null) {
			int index = famaPath.lastIndexOf(".");
			String afm2cocoPath = famaPath.substring(0, index) + Util.AFM2COCO_EXTENSION;
			afm2cocoModel = new File(afm2cocoPath);

			try {
				//Read fama model and create the fama2coco model
				List<String> text = new ArrayList<String>();
				FileReader fReader = new FileReader(this.famaPath);
				BufferedReader bReader = new BufferedReader(fReader);
				String line = bReader.readLine();
				
				//Copy fama model content
				List<String> features = new ArrayList<String>();
				boolean relationships = true;
				
				while(line != null) {
					text.add(line);
					
					if(line.equals(Util.FAMA_ATTRIBUTES_SEC)){
						relationships = false;
					}
					
					//Store model features
					if(relationships && !line.equals(Util.FAMA_RELATIONSHIPS_SEC)){
						List<String> temp = new ArrayList<String>();
						Pattern pattern = Pattern.compile("[a-zA-Z0-9_]*");
						Matcher matcher = pattern.matcher(line);
						
						while(matcher.find()){
							Pattern patternNumbers = Pattern.compile("^\\d+$");
							Matcher matcherNumbers = patternNumbers.matcher(matcher.group().trim());
							
							if(!matcher.group().trim().isEmpty() && !matcherNumbers.find()){
								temp.add(matcher.group());
							}
							
						}
						features.addAll(temp);
					}
					
					line = bReader.readLine();
				}
				
				fReader.close();
				bReader.close();
				
				//Create %Features section
				String append = appendFeaturesToAfm2CoCoModel(features);
				FileWriter fWriter = new FileWriter(afm2cocoModel);
				BufferedWriter bWriter = new BufferedWriter(fWriter);
				
				//Write afm2coco model
				bWriter.write(Util.AFM2COCO_FEATURES_SEC);
				bWriter.newLine();
				bWriter.write(append);
				bWriter.newLine();
				
				for(int i = 0; i < text.size(); i++) {
					bWriter.newLine();
					bWriter.write(text.get(i));
				}
				
				bWriter.flush();
				bWriter.close();
				fWriter.close();
			} 
			
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		return afm2cocoModel;
	}

	/**
	 * Append the features section to the afm2coco model,
	 * based on the list of features received as parameter.
	 * @param features - List with features' names
	 * @return features section with the list of features
	 */
	private String appendFeaturesToAfm2CoCoModel(List<String> features){
		String append = "";
		
		for(int i = 0; i < features.size(); i++) {
			append += features.get(i) + ", ";
		}
		
		return append;
	}	
}
