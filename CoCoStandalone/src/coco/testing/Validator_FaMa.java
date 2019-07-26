package coco.testing;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityElement;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.*;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
public class Validator_FaMa {
	static long totalSum = 0;
	static ArrayList elements = new ArrayList();
	
	public Validator_FaMa() {
		super();
		this.elements = new ArrayList();
	}


	public static long getTotalSum() {
		return totalSum;
	}


	public static void setTotalSum(long totalSum) {
		Validator_FaMa.totalSum = totalSum;
	}


	public static ArrayList getElements() {
		return elements;
	}


	public static void setElements(ArrayList elements) {
		Validator_FaMa.elements = elements;
	}


	public static void main(String[] args) {
		double prom = 0;
		//for(int i =0; i< 1;i++){
		// 1. Instantiate QuestionTrader
		long startTime = System.currentTimeMillis();
		//System.out.println("Inicia:" + startTime);
		// The main class is instantiated
		boolean val = Validate("C:/Users/Asistente/Documents/PAPER_DEMO/INPUT/Ontologia.afm");
		System.out.println(val);
		totalSum += (System.currentTimeMillis() - startTime);

		System.out.println("Tiempo ejecucion:" + totalSum);
		

	}
	
	
	public static boolean Validate(String url) {
		QuestionTrader qt = new QuestionTrader();
		String name = url.split("/")[1].split("\\.")[0];
		VariabilityModel fm = qt.openFile(url);

		qt.setVariabilityModel(fm);

		//////// VALID QUESTION + NUMBER PRODUCTS QUESTION /////////
		ValidQuestion vq = (ValidQuestion) qt.createQuestion("Valid");
		try {
		qt.ask(vq);
		
		FAMAAttributedFeatureModel featureModel = (FAMAAttributedFeatureModel) fm;
		
		Iterator<VariabilityElement> eleme = (Iterator<VariabilityElement>) featureModel.getElements().iterator();
		for (int i = 0; eleme.hasNext(); i++) {
			VariabilityElement feature = (VariabilityElement) eleme.next();
			
			//System.out.println(feature.getName());
			elements.add("("+name+")_"+feature.getName());
		}
		
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		if (vq.isValid()) {
			
			return true;
		}else {
			return false;
		}
		}

	
}
