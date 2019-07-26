package coco.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttributedFeatureModel;

public class FAMAValidator {
	
	public FAMAValidator(){
		
	}
	
	public int generateRandomNumber(){
		int number = (int) (Math.random()*30);
		return number;
	}
	
	public boolean validateAfmFM(String path){
		System.out.println(path);
		QuestionTrader trader = new QuestionTrader();
		GenericAttributedFeatureModel fm = (GenericAttributedFeatureModel) trader.openFile(path);
		trader.setVariabilityModel(fm);
		
		ValidQuestion valid = (ValidQuestion) trader.createQuestion("Valid");
		trader.ask(valid);
		return valid.isValid();
	}
	
	public void preprocessAfmFM(String path, int features){
		try{			
			File file = new File(path);
			List<String> text = new ArrayList<String>();
			
			FileReader fReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fReader);
			String line = bReader.readLine();
			
			if(!line.equals("%Features")) {
				String newFilePath = file.getPath().substring(0, (file.getPath().lastIndexOf(".") + 1)) + "afm2coco";
				File newFile = new File(newFilePath);
				
				while(line != null) {
					text.add(line);
					line = bReader.readLine();
				}
				fReader.close();
				bReader.close();
				
				FileWriter fWriter = new FileWriter(file);
				BufferedWriter bWriter = new BufferedWriter(fWriter);
				String append = "root, ";
				
				for(int i = 1; i < features; i++) {
					append += "F" + i + ", ";
				}
				
				bWriter.write("%Features");
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
				
				if(file.renameTo(newFile)){
					System.out.println("File " + newFile.getName() + " have been processed.");
				}
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void writeCoCoModel(String path) {
		try{
			File file = new File(path);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			
			bWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			bWriter.write("<coCoMM:CoCo xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			bWriter.write("    xmlns:coCoMM=\"http://www.example.org/coCoMM\" xsi:schemaLocation=\"http://www.example.org/coCoMM coCoMM.ecore\"/>");

			bWriter.flush();
			bWriter.close();
			fWriter.close();
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
