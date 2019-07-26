package coco.testing;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
//import org.chocosolver.solver.constraints.ICF;
//import org.chocosolver.solver.constraints.IntConstraintFactory;
//import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.objective.ParetoOptimizer;
import org.chocosolver.solver.variables.IntVar;


//import choco.kernel.model.Model;

//import org.chocosolver.solver.search.solution.Solution;
import org.chocosolver.solver.Solution;

//import com.ibm.icu.util.Measure;

public class MainTransformedDynamic_v2 {
	private Model model;
	private IntVar[] totalVars;
	private List<IntVar> features_def = new <IntVar>ArrayList();

	private String[] propOptionalFeatures;
	private String[] propMandatoryFeatures;
	private String[] propAttributeTypes;
	private String[][] propFeatureAttributes;

	private String[][] propMandatoryTCs;
	private String[][] propOptionalTCs;
	private List<String>[] propOrTCs;
	private List<String>[] propAlternativeTCs;
	private String[][] propRequiresCTCs;
	private String[][] propExcludesCTCs;
	private String[][] propForcesCMCs;
	private String[][] propProhibitsCMCs;
	private HashMap<String, IntVar> features;

	private HashMap<String, IntVar> featureAttrAttributes;

	private Properties properties;
	private int limit_solution = 10;
	ByteArrayOutputStream baos;
	PrintStream ps;
	PrintStream old = System.out;

	public static void main(String[] a) {

		MainTransformedDynamic_v2 d = new MainTransformedDynamic_v2("", -10);
	}

	public MainTransformedDynamic_v2(String path, int tipo) {

		model = new Model();

		baos = new ByteArrayOutputStream();
		ps = new PrintStream(baos);

		features = new HashMap<String, IntVar>();
		featureAttrAttributes = new HashMap<String, IntVar>();
		try {
			properties = new Properties();
			InputStream stream = new FileInputStream(path);
			properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		readProperties();
		initializeCSP();

		// }
	}

	public void readProperties() {
		// --------------------------------------------
		// Features
		// --------------------------------------------
		readPropFeatures();
		// --------------------------------------------
		// Feature Attributes
		// Atributes
		// --------------------------------------------
		readAtributes();
		// readPropFeatureAttrsAttributes0();
		// readPropFeatureAttrsAttributes1();
		readPropFeatureAttrsAttributes();

		// --------------------------------------------
		// Tree Constraints
		// --------------------------------------------
		readPropMandatoryOptionalTCs();
		readPropOrTCs();
		readPropAlternativeTCs();

		// --------------------------------------------
		// Cross-Tree Constraints
		// --------------------------------------------
		readPropCTCs();

		// --------------------------------------------
		// Cross-Model Constraints
		// --------------------------------------------
		readPropCMCs();
	}

	public void readPropFeatures() {
		int mandatoryFeatures = Integer.parseInt(properties.getProperty("mandatoryFeaturesNum").trim());
		int optionalFeatures = Integer.parseInt(properties.getProperty("optionalFeaturesNum").trim());

		// contFeatures =
		// Integer.parseInt(properties.getProperty("featuresNum").trim());
		ArrayList validador_mandatoryFeats = new ArrayList();
		ArrayList validador_optionalFeats = new ArrayList();

		for (int i = 0; i < mandatoryFeatures; i++) {
			if (!validador_mandatoryFeats.contains(properties.getProperty("mandatoryFeature" + i).trim())) {
				validador_mandatoryFeats.add(properties.getProperty("mandatoryFeature" + i).trim());

			}
		}

		for (int i = 0; i < optionalFeatures; i++) {
			if (!validador_optionalFeats.contains(properties.getProperty("optionalFeature" + i).trim())) {
				validador_optionalFeats.add(properties.getProperty("optionalFeature" + i).trim());

			}
		}
		propOptionalFeatures = new String[validador_optionalFeats.size()];

		propMandatoryFeatures = new String[validador_mandatoryFeats.size()];
		for (int i = 0; i < validador_mandatoryFeats.size(); i++) {
			propMandatoryFeatures[i] = (String) validador_mandatoryFeats.get(i);
		}

		for (int i = 0; i < validador_optionalFeats.size(); i++) {
			propOptionalFeatures[i] = (String) validador_optionalFeats.get(i);
		}
	}

	public void readAtributes() {
		int attributeTypesNum = Integer.parseInt(properties.getProperty("attributeTypesNum").trim());
		propAttributeTypes = new String[attributeTypesNum];
		for (int i = 0; i < attributeTypesNum; i++) {

			propAttributeTypes[i] = properties.getProperty("attributeType" + i).trim();
		}
	}

	public void readPropFeatureAttrsAttributes() {
		List propFeatureAttributesDynamic = new ArrayList();
		String[] temp = new String[4];
		propFeatureAttributes = new String[(propMandatoryFeatures.length + propOptionalFeatures.length)
				* propAttributeTypes.length][4];
		for (int j = 0; j < propAttributeTypes.length; j++) {

			for (int i = 0; i < propMandatoryFeatures.length; i++) {

				String key = propMandatoryFeatures[i] + "." + propAttributeTypes[j];
				try {
					String value = properties.getProperty(key).trim();
					String[] array = value.split(",");
					temp[0] = key;// propMandatoryFeatures[i];
					temp[1] = array[0];
					temp[2] = array[1];
					temp[3] = array[2];
					propFeatureAttributesDynamic.add(temp.clone());
				} catch (Exception e) {

				}
			}
		}
		for (int j = 0; j < propAttributeTypes.length; j++) {

			for (int i = propMandatoryFeatures.length; i < propOptionalFeatures.length
					+ propMandatoryFeatures.length; i++) {

				String key = propOptionalFeatures[i - propMandatoryFeatures.length] + "." + propAttributeTypes[j];
				try {
					String value = properties.getProperty(key).trim();
					String[] array = value.split(",");
					temp[0] = key;// propMandatoryFeatures[i];
					temp[1] = array[0];
					temp[2] = array[1];
					temp[3] = array[2];

					propFeatureAttributesDynamic.add(temp.clone());
				} catch (Exception e) {

				}
			}
		}
		propFeatureAttributes = new String[propFeatureAttributesDynamic.size()][4];
		for (int j = 0; j < propFeatureAttributesDynamic.size(); j++) {
			temp = (String[]) propFeatureAttributesDynamic.get(j);
			propFeatureAttributes[j][0] = temp[0];
			propFeatureAttributes[j][1] = temp[1];
			propFeatureAttributes[j][2] = temp[2];
			propFeatureAttributes[j][3] = temp[3];
		}

	}

	public void readPropMandatoryOptionalTCs() {
		int ands = Integer.parseInt(properties.getProperty("andsNum").trim());
		int mandatoryAnds = Integer.parseInt(properties.getProperty("mandatoryAndsNum").trim());
		int optionalAnds = Integer.parseInt(properties.getProperty("optionalAndsNum").trim());
		int countMandatory = 0;
		int countOptional = 0;

		propMandatoryTCs = new String[mandatoryAnds][2];
		propOptionalTCs = new String[optionalAnds][2];

		for (int i = 0; i < ands; i++) {
			String value = properties.getProperty("and" + i).trim();
			boolean isMandatory = value.endsWith("*");
			String[] array = value.split("\\*");

			if (isMandatory) {
				String[] features = array[0].split(":");
				propMandatoryTCs[countMandatory][0] = features[0];
				propMandatoryTCs[countMandatory][1] = features[1];
				countMandatory++;
			} else {
				String[] features = array[0].split(":");
				propOptionalTCs[countOptional][0] = features[0];
				propOptionalTCs[countOptional][1] = features[1];
				countOptional++;
			}
		}
	}

	public void readPropOrTCs() {
		int ors = Integer.parseInt(properties.getProperty("orsNum").trim());
		propOrTCs = new List[ors];

		for (int i = 0; i < ors; i++) {
			String value = properties.getProperty("or" + i);
			if (value != null) {
				value = value.trim();
				String[] array = value.split(":");
				if (array.length > 1) {
					String[] features = array[1].split(",");

					propOrTCs[i] = new ArrayList<String>();
					propOrTCs[i].add(array[0]);

					for (int j = 0; j < features.length; j++) {
						propOrTCs[i].add(features[j]);
					}
				}
			}
		}
	}

	public void readPropAlternativeTCs() {
		int alts = Integer.parseInt(properties.getProperty("altsNum").trim());
		propAlternativeTCs = new List[alts];

		for (int i = 0; i < alts; i++) {
			String value = properties.getProperty("alt" + i).trim();
			System.out.println(value);
			String[] array = value.split(":");
			String[] features = array[1].split(",");

			propAlternativeTCs[i] = new ArrayList<String>();
			propAlternativeTCs[i].add(array[0]);

			for (int j = 0; j < features.length; j++) {
				propAlternativeTCs[i].add(features[j]);
			}
		}
	}

	public void readPropCTCs() {
		int ctcs = Integer.parseInt(properties.getProperty("ctcsNum").trim());
		int requiresCtcs = Integer.parseInt(properties.getProperty("requiresCtcsNum").trim());
		int excludesCtcs = Integer.parseInt(properties.getProperty("excludesCtcsNum").trim());
		int countRequires = 0;
		int countExcludes = 0;

		propRequiresCTCs = new String[requiresCtcs][2];
		propExcludesCTCs = new String[excludesCtcs][2];

		for (int i = 0; i < ctcs; i++) {
			String value = properties.getProperty("ctc" + i).trim();
			boolean isMandatory = value.endsWith("*");
			String[] array = value.split("\\*");

			if (isMandatory) {
				String[] features = array[0].split(":");
				propRequiresCTCs[countRequires][0] = features[0];
				propRequiresCTCs[countRequires][1] = features[1];
				countRequires++;
			} else {
				String[] features = array[0].split(":");
				propExcludesCTCs[countExcludes][0] = features[0];
				propExcludesCTCs[countExcludes][1] = features[1];
				countExcludes++;
			}
		}
	}

	public void readPropCMCs() {
		int ctcs = Integer.parseInt(properties.getProperty("cmcsNum").trim());
		int forcesCmcs = Integer.parseInt(properties.getProperty("forcesCmcsNum").trim());
		int prohibitsCmcs = Integer.parseInt(properties.getProperty("prohibitsCmcsNum").trim());
		int countForces = 0;
		int countProhibits = 0;

		propForcesCMCs = new String[forcesCmcs][2];
		propProhibitsCMCs = new String[prohibitsCmcs][2];

		for (int i = 0; i < ctcs; i++) {
			String value = properties.getProperty("cmc" + i).trim();
			boolean isMandatory = value.endsWith("*");
			String[] array = value.split("\\*");

			if (isMandatory) {
				String[] features = array[0].split(":");
				propForcesCMCs[countForces][0] = features[0];
				propForcesCMCs[countForces][1] = features[1];
				countForces++;
			} else {
				String[] features = array[0].split(":");
				propProhibitsCMCs[countProhibits][0] = features[0];
				propProhibitsCMCs[countProhibits][1] = features[1];
				countProhibits++;
			}
		}
	}

	public void initializeCSP() {
		// --------------------------------------------
		// Features
		// --------------------------------------------
		initializeOptionalFeatures();
		initializeMandatoryFeatures();

		// --------------------------------------------
		// Feature Attributes
		// --------------------------------------------

		initializeFeatureAttributes();
		// --------------------------------------------
		// Tree Constraints
		// --------------------------------------------
		initializeAllMandatoryTCs();
		initializeAllOptionalTCs();
		initializeAllOrTCs();

		initializeAllAlternativeTCs();

		// --------------------------------------------
		// Cross-Tree Constraints
		// --------------------------------------------
		initializeAllRequiresCTCs();
		initializeAllExcludesCTCs();

		// --------------------------------------------
		// Cross-Model Constraints
		// --------------------------------------------
		// .....initializeAllForcesCMCs();
		// .....initializeAllProhibitsCMCs();

	}

	public void initializeOptionalFeatures() {
		for (int i = 0; i < propOptionalFeatures.length; i++) {
			// BoolVar feature = model.boolVar(propOptionalFeatures[i]);//
			// VariableFactory.bool(propOptionalFeatures[i],
			// solver);
			IntVar feature = model.intVar(propOptionalFeatures[i], 0, 1, false);
			features_def.add(feature);
			features.put(propOptionalFeatures[i], feature);
			// propOptionalFeatures[i] + "\", solver);");
		}
	}

	public void initializeMandatoryFeatures() {

		for (int i = 0; i < propMandatoryFeatures.length; i++) {

			// BoolVar feature = model.boolVar(propMandatoryFeatures[i], true);// (BoolVar)
			// VariableFactory.fixed(propMandatoryFeatures[i],
			// 1, solver);
			IntVar feature = model.intVar(propMandatoryFeatures[i], 1);
			features_def.add(feature);
			features.put(propMandatoryFeatures[i], feature);
		}
	}

	public void initializeFeatureAttributes() {

		@SuppressWarnings("unused")

		List<IntVar> varsAtributeList = new ArrayList<IntVar>(featureAttrAttributes.values());
		IntVar[] varsAtribute = new IntVar[featureAttrAttributes.values().size()];
		List<IntVar> totalAtributeTemp = new ArrayList<IntVar>();
		List varsAtributeTemp = new ArrayList();
		IntVar[] total = new IntVar[propAttributeTypes.length];
		for (int i = 0; i < varsAtributeList.size(); i++) {
			varsAtribute[i] = varsAtributeList.get(i);
		}
		System.out.println("____"+propAttributeTypes.length);
		for (int j = 0; j < propAttributeTypes.length; j++) {
			varsAtributeTemp.clear();
			for (int i = 0; i < propFeatureAttributes.length; i++) {

				String featureName = propFeatureAttributes[i][0];
				IntVar specificAtribute = null;
				int minval = Integer.parseInt(propFeatureAttributes[i][2]);
				int maxval = Integer.parseInt(propFeatureAttributes[i][3]);
				int defaultValue = Integer.parseInt(propFeatureAttributes[i][1]);
				if (featureName != null) {
					

					if (defaultValue == 0) {
						featureAttrAttributes.put(featureName,
								// VariableFactory.enumerated(featureName, new int[] { 0, 0 }, solver));
								specificAtribute = model.intVar(featureName, 0, 0, true));
					} else {
						featureAttrAttributes.put(featureName,
								// VariableFactory.enumerated(featureName, new int[] { 0, maxval }, solver));
								specificAtribute = model.intVar(featureName, 0, maxval, false));
					}
				}
				if (featureName.split("\\.")[1].equals(propAttributeTypes[j].toString()) && specificAtribute != null) {

					varsAtributeTemp.add(specificAtribute);
					IntVar feature_1 = features.get(featureName.split("\\.")[0]);
					// model.ifThen(model.arithm(feature_1, "=", 0), model.arithm(specificAtribute,
					// "=", 0));
					model.ifThenElse(model.arithm(feature_1, "=", 0), model.arithm(specificAtribute, "=", 0),
							model.arithm(specificAtribute, ">=", defaultValue)); 
					System.out.println("specificAtribute..."+specificAtribute);
					model.arithm(specificAtribute, ">=", specificAtribute);
					// model.ifThen(model.arithm(feature_1, "=", 1), model.arithm(specificAtribute,
					// ">", minval));
					totalAtributeTemp.add(specificAtribute);
				}

			}
			IntVar[] varsAtributeTemp2 = new IntVar[varsAtributeTemp.size()];
			for (int ww = 0; ww < varsAtributeTemp.size(); ww++) {
				varsAtributeTemp2[ww] = (IntVar) varsAtributeTemp.get(ww);
			}
			IntVar totalAtribute = model.intVar(propAttributeTypes[j], 0, 1000000, false);
			model.sum(varsAtributeTemp2, "=", totalAtribute).post();
			totalAtributeTemp.add(totalAtribute);
			total[j] = totalAtribute;
		}
		IntVar total_Def = model.intVar("TOTALIZADOR", 0, 1000000, false);
		model.sum(total, "=", total_Def).post();
		totalAtributeTemp.add(total_Def);
		totalVars = new IntVar[totalAtributeTemp.size()+ this.features_def.size()];
		for (int i = 0; i < totalAtributeTemp.size(); i++) {
			totalVars[i] = totalAtributeTemp.get(i);
		}
		for (int i = 0; i < this.features_def.size(); i++) {
			totalVars[i + totalAtributeTemp.size()] = features_def.get(i);
		}

	}

	public void initializeAllMandatoryTCs() {
		for (int i = 0; i < propMandatoryTCs.length; i++) {
			IntVar parent = features.get(propMandatoryTCs[i][0]);
			IntVar child = features.get(propMandatoryTCs[i][1]);
			initializeMandatoryTC(parent, child);
		}
	}

	public void initializeAllOptionalTCs() {
		for (int i = 0; i < propOptionalTCs.length; i++) {
			IntVar parent = features.get(propOptionalTCs[i][0]);
			IntVar child = features.get(propOptionalTCs[i][1]);
			initializeOptionalTC(parent, child);
		}
	}

	public void initializeMandatoryTC(IntVar parent, IntVar child) {

		//model.ifThen(model.arithm(parent, "=", 1), model.arithm(child, "=", 1));
		model.arithm(child, "=", parent).post();
		// constraint = IntConstraintFactory.arithm(parent, "=", child);
		// constraint.setName(Utilities.MANDATORY_TC); solver.post(constraint);

	}

	public void initializeOptionalTC(IntVar parent, IntVar child) {

		//model.arithm(parent, ">=", child);
		//model.ifThen(model.arithm(child, "=", 1), model.arithm(parent, "=", 1));
		model.arithm(parent,">=",child).post();

	}

	public void initializeAllOrTCs() {
		for (int i = 0; i < propOrTCs.length; i++) {
			List<String> involvedFeatures = propOrTCs[i];
			if (involvedFeatures != null) {
				IntVar parent = features.get(involvedFeatures.get(0));
				IntVar[] children = new IntVar[involvedFeatures.size() - 1];

				for (int j = 0; j < children.length; j++) {
					children[j] = features.get(involvedFeatures.get(j + 1));
				}

				initializeOrTC(parent, children);
			}
		}
	}

	public void initializeOrTC(IntVar parent, IntVar[] children) {
		IntVar sumOr = model.intVar("SumOr" + parent.getName() + children.length+"TC", 0, children.length, false);

		model.sum(children, "=", sumOr).post();
		Constraint constraint1 = model.arithm(sumOr, ">=", 1);
		//constraint1.setName(Utilities.OR_TC);
		Constraint constraint0 = model.arithm(sumOr, "=", 0);
		//constraint0.setName(Utilities.OR_TC);
		Constraint constraint = model.arithm(parent, "=", 1);
		model.ifThenElse(constraint, constraint1, constraint0);

	}

	public void initializeAllAlternativeTCs() {
		for (int i = 0; i < propAlternativeTCs.length; i++) {
			List<String> involvedFeatures = propAlternativeTCs[i];
			IntVar parent = features.get(involvedFeatures.get(0));
			IntVar[] children = new IntVar[involvedFeatures.size() - 1];

			for (int j = 0; j < children.length; j++) {
				children[j] = features.get(involvedFeatures.get(j + 1));
			}

			initializeAlternativeTC(parent, children);
		}
	}

	public void initializeAlternativeTC(IntVar parent, IntVar[] children) {
		// IntVar sumAlt = VariableFactory.enumerated("sumAltroot1_root", 0, 1, solver);
		IntVar sumAlt = model.intVar("SumAlt" + parent + children.length+"TC", 0, 1, false);
		// BoolVar[] varsAlt = new BoolVar[children.length];

		// for (int i = 0; i < children.length; i++) {
		// varsAlt[i] = (BoolVar) children[i];
		// }
		// solver.post(IntConstraintFactory.sum(varsAlt, sumAlt));
		model.sum(children, "=", sumAlt).post();

		Constraint constraint1 = model.arithm(sumAlt, "=", 1);
		//constraint1.setName(Utilities.XOR_TC);
		Constraint constraint0 = model.arithm(sumAlt, "=", 0);
		//constraint0.setName(Utilities.XOR_TC);
		Constraint constraint = model.arithm(parent, "=", 1);

		model.ifThenElse(constraint, constraint1, constraint0);
	}

	public void initializeAllRequiresCTCs() {
		for (int i = 0; i < propRequiresCTCs.length; i++) {
			IntVar feature1 = features.get(propRequiresCTCs[i][0]);
			IntVar feature2 = features.get(propRequiresCTCs[i][1]);
			initializeRequiresCTC(feature1, feature2);
		}
	}

	public void initializeRequiresCTC(IntVar feature1, IntVar feature2) {
		model.arithm(feature1, "<=", feature2).post();

	}

	public void initializeAllExcludesCTCs() {
		for (int i = 0; i < propExcludesCTCs.length; i++) {
			IntVar feature1 = features.get(propExcludesCTCs[i][0]);
			IntVar feature2 = features.get(propExcludesCTCs[i][1]);
			initializeExcludesCTC(feature1, feature2);
		}
	}

	public void initializeExcludesCTC(IntVar feature1, IntVar feature2) {
		model.arithm(feature1, "+", feature2, "<=", 1).post();
		;

	}
	public String[] solveCSP(int heuristica,  int numsols) {
		
		ParetoOptimizer po = new ParetoOptimizer(Model.MINIMIZE, totalVars);
		
		Solver solver = model.getSolver();
		solver.plugMonitor(po);
		int numero = 0;
		//System.out.println("PTOINICIAL3::_"+System.nanoTime());

		while (solver.solve() && numero < numsols) {
			numero++;
		}
		// retrieve the pareto front
		List<Solution> paretoFront = po.getParetoFront();

		String salida = "SOLUCIONES:" + paretoFront.size() + "\nCOMPONENTES:" + (totalVars.length-1) + "\n";

		for (Solution s : paretoFront) {
			salida += "PUNTO_DE_DIVISION_SALIDA\n";
			for (int j = 0; j < totalVars.length; j++) {
				// if(s.getIntVal(totalVars[j])==1) {
				if(!totalVars[j].getName().equals("TOTALIZADOR")) {
				String valor = totalVars[j].getName() + ":" + s.getIntVal(totalVars[j]);
				salida += valor + "\n";
				}
			}
			continue;
		}

		//System.out.println("PTOINICIAL5::_"+System.nanoTime());
		String[] x = { salida, "" };

		return x;

	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	

}
