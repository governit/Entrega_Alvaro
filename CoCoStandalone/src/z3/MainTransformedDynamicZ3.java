package z3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.microsoft.z3.*;

//import com.ibm.icu.util.Measure;

public class MainTransformedDynamicZ3 {
	private Optimize solver;// Solver solver;
	private Optimize opt;
	private int contFeatures;
	private int numsols;
	private String[] propOptionalFeatures;
	private String[] propMandatoryFeatures;
	private String[] propAttributeTypes;
	private IntExpr totalizador_SC;
	// private IntExpr[][] propFeatureAttributes;
	private List<IntExpr> FeatureAtts = new ArrayList();
	private List<IntExpr> Atts = new ArrayList();;

	private int tipo = 0;
	// private String[][] propFeatureAttributes0;
	// private String[][] propFeatureAttributes1;
	private String[][] propMandatoryTCs;
	private String[][] propOptionalTCs;
	private List<String>[] propOrTCs;
	private List<String>[] propAlternativeTCs;
	private String[][] propRequiresCTCs;
	private String[][] propExcludesCTCs;
	private String[][] propForcesCMCs;
	private String[][] propProhibitsCMCs;
	private HashMap<String, BoolExpr> features;

	private List<BoolExpr> CTCs = new ArrayList();
	private List<BoolExpr> TreeStructure = new ArrayList();
	// private HashMap<String, IntVar> featureAttrAttributes0;
	// private HashMap<String, IntVar> featureAttrAttributes1;
	private HashMap<String, BoolExpr> featureAttrAttributes;

	private Properties properties;
	private int limit_solution = 10;

	Context ctx;

	public MainTransformedDynamicZ3(String path, int tipo, int numsols) {
		// --------------------------------------------
		// Class Attributes
		// --------------------------------------------
		int N = 100;
		this.tipo = tipo;
		// 1. Modelling part
		this.numsols = numsols;
		// solver = new Solver();
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		
		ctx = new Context(cfg);
		solver = ctx.mkOptimize();// ctx.mkSolver();
		opt = ctx.mkOptimize();
		features = new HashMap<String, BoolExpr>();
		// featureAttrAttributes0 = new HashMap<String, IntVar>();
		// featureAttrAttributes1 = new HashMap<String, IntVar>();
		featureAttrAttributes = new HashMap<String, BoolExpr>();
		this.totalizador_SC = ctx.mkIntConst("totalizados_SC");
		try {
			properties = new Properties();
			InputStream stream = new FileInputStream(path);
			properties.load(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// --------------------------------------------
		// Read properties
		// --------------------------------------------
		readProperties();

		// --------------------------------------------
		// Initialize CSP
		// --------------------------------------------
		initializeCSP();
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
		SCSolver();
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
		contFeatures = propMandatoryFeatures.length + propOptionalFeatures.length;
	}

	public void readAtributes() {
		int attributeTypesNum = Integer.parseInt(properties.getProperty("attributeTypesNum").trim());
		propAttributeTypes = new String[attributeTypesNum];
		for (int i = 0; i < attributeTypesNum; i++) {
			propAttributeTypes[i] = properties.getProperty("attributeType" + i).trim();
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
			String value = properties.getProperty("or" + i).trim();
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

	public void readPropAlternativeTCs() {
		int alts = Integer.parseInt(properties.getProperty("altsNum").trim());
		propAlternativeTCs = new List[alts];

		for (int i = 0; i < alts; i++) {
			String value = properties.getProperty("alt" + i).trim();
			String[] array = value.split(":");
			String[] features = array[1].split(",");

			propAlternativeTCs[i] = new ArrayList<String>();
			propAlternativeTCs[i].add(array[0]);

			for (int j = 0; j < features.length; j++) {
				propAlternativeTCs[i].add(features[j]);
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
		initializeOptionalFeatures();// OK Z3
		initializeMandatoryFeatures();// OK Z3
		initializeAllMandatoryTCs();
		initializeAllOptionalTCs();
		initializeAllOrTCs();
		initializeAllAlternativeTCs();
		GenerateTreeStructure();
	}

	/********** ATTRIBUTES DEFINITION ************/
	List totalVars = new ArrayList();
	List Feats = new ArrayList();

	public void readPropFeatureAttrsAttributes() {

		for (int j = 0; j < propAttributeTypes.length; j++) {

			for (int i = 0; i < propMandatoryFeatures.length; i++) {

				String key = propMandatoryFeatures[i] + "." + propAttributeTypes[j];
				try {
					String value = properties.getProperty(key).trim();
					String[] array = value.split(",");

					IntExpr sss = ctx.mkIntConst(key);// propAttributeTypes[j]);
					totalVars.add(sss);
					IntExpr lower = ctx.mkInt(array[1]);
					IntExpr higher = ctx.mkInt(array[2]);
					IntExpr defaultv = ctx.mkInt(array[0]);
					BoolExpr c1 = ctx.mkLe(sss, higher);
					BoolExpr c2 = ctx.mkGe(sss, defaultv);
					BoolExpr q = ctx.mkAnd(c1, c2);
					BoolExpr feature = ctx.mkBoolConst(propMandatoryFeatures[i]);
					solver.Assert(ctx.mkAnd(ctx.mkImplies(ctx.mkEq(feature, ctx.mkBool(true)), q),
							ctx.mkImplies(ctx.mkEq(feature, ctx.mkBool(false)), ctx.mkEq(sss, ctx.mkInt(0)))));
					if (!FeatureAtts.contains(sss))
						FeatureAtts.add(sss);
					IntExpr att = ctx.mkIntConst(propAttributeTypes[j]);
					if (!Feats.contains(feature)) {
						Feats.add(feature);
					}
					if (!Atts.contains(att) && Atts.size() < numsols + 1)
						Atts.add(att);
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

					IntExpr sss = ctx.mkIntConst(key);// propAttributeTypes[j]);
					totalVars.add(sss);

					IntExpr lower = ctx.mkInt(array[1]);
					IntExpr higher = ctx.mkInt(array[2]);
					IntExpr defaultv = ctx.mkInt(array[0]);
					BoolExpr c1 = ctx.mkLe(sss, higher);
					BoolExpr c2 = ctx.mkGe(sss, defaultv);
					BoolExpr q = ctx.mkAnd(c1, c2);
					BoolExpr feature = ctx.mkBoolConst(propOptionalFeatures[i - propMandatoryFeatures.length]);
					solver.Assert(ctx.mkAnd(ctx.mkImplies(ctx.mkEq(feature, ctx.mkBool(true)), q),
							ctx.mkImplies(ctx.mkEq(feature, ctx.mkBool(false)), ctx.mkEq(sss, ctx.mkInt(0)))));
					if (!FeatureAtts.contains(sss))
						FeatureAtts.add(sss);
					IntExpr att = ctx.mkIntConst(propAttributeTypes[j]);
					if (!Feats.contains(feature)) {
						Feats.add(feature);
					}
					if (!Atts.contains(att)) {
						Atts.add(att);
						totalVars.add(att);
					}
				} catch (Exception e) {

				}
			}
		}

	}

	/********** ATTRIBUTES DEFINITION ************/
	/******************************
	 * FEATURES DEFINITION
	 *********************************/

	public void initializeOptionalFeatures() {
		for (int i = 0; i < propOptionalFeatures.length; i++) {
			BoolExpr x = ctx.mkBoolConst(propOptionalFeatures[i]);
			features.put(propOptionalFeatures[i], x);
			totalVars.add(x);
		}
	}

	public void initializeMandatoryFeatures() {

		for (int i = 0; i < propMandatoryFeatures.length; i++) {
			BoolExpr x = ctx.mkBoolConst(propMandatoryFeatures[i]);
			if (i == 0)
				TreeStructure.add(ctx.mkEq(x, ctx.mkBool(true)));
			features.put(propMandatoryFeatures[i], x);
			totalVars.add(x);
		}

	}

	/******************************
	 * FEATURES DEFINITION
	 *********************************/
	/******************************
	 * TREE DEFINITION
	 *********************************/
	public void initializeAllMandatoryTCs() {
		for (int i = 0; i < propMandatoryTCs.length; i++) {
			try {
				CTCs.add(ctx.mkAnd(features.get(propMandatoryTCs[i][0]), features.get(propMandatoryTCs[i][1])));
				TreeStructure.add(ctx.mkAnd(
						ctx.mkImplies(ctx.mkEq(features.get(propMandatoryTCs[i][1]), ctx.mkBool(true)),
								ctx.mkEq(features.get(propMandatoryTCs[i][0]), ctx.mkBool(true))),
						ctx.mkImplies(ctx.mkEq(features.get(propMandatoryTCs[i][0]), ctx.mkBool(true)),
								ctx.mkEq(features.get(propMandatoryTCs[i][1]), ctx.mkBool(true)))));
			} catch (Exception e) {
				System.out.println("constraint paila:" + propMandatoryTCs[i][0] + "::::" + propMandatoryTCs[i][1]);
			}
		}
	}

	public void initializeAllOptionalTCs() {
		for (int i = 0; i < propOptionalTCs.length; i++) {
			try {
				CTCs.add(ctx.mkAnd(features.get(propOptionalTCs[i][0]), features.get(propOptionalTCs[i][1])));
				TreeStructure.add(ctx.mkImplies(ctx.mkEq(features.get(propOptionalTCs[i][1]), ctx.mkBool(true)),
						ctx.mkEq(features.get(propOptionalTCs[i][0]), ctx.mkBool(true))));
			} catch (Exception e) {
				System.out.println("constraint paila:" + propOptionalTCs[i][0] + "::::" + propOptionalTCs[i][1]);
			}
		}
	}

	public void initializeAllOrTCs() {
		for (int i = 0; i < propOrTCs.length; i++) {
			List<String> involvedFeatures = propOrTCs[i];
			if (involvedFeatures != null) {
				BoolExpr parent = features.get(involvedFeatures.get(0));
				BoolExpr[] children = new BoolExpr[involvedFeatures.size() - 1];

				for (int j = 0; j < children.length; j++) {

					children[j] = ctx.mkEq(features.get(involvedFeatures.get(j + 1)), ctx.mkBool(true));

				}
				TreeStructure.add(ctx.mkImplies(ctx.mkEq(parent, ctx.mkBool(true)), ctx.mkOr(children)));

				// initializeOrTC(parent, children);}
				// ctx.mkOr(parent, children[0]);
			}

		}
	}

	public void initializeAllAlternativeTCs() {// PENDIENTE POR PROBAR!!!!
		for (int i = 0; i < propAlternativeTCs.length; i++) {
			List<String> involvedFeatures = propAlternativeTCs[i];
			BoolExpr parent = features.get(involvedFeatures.get(0));
			BoolExpr[] children = new BoolExpr[involvedFeatures.size() - 1];
			for (int j = 0; j < children.length; j++) {
				children[j] = ctx.mkEq(features.get(involvedFeatures.get(j + 1)), ctx.mkBool(true));// features.get(involvedFeatures.get(j
			}
			BoolExpr primera_parte = ctx.mkImplies(ctx.mkEq(parent, ctx.mkBool(true)), ctx.mkOr(children));
			ArrayList<BoolExpr> lista = new <BoolExpr>ArrayList();
			for (int j = 0; j < children.length; j++) {
				for (int w = 0; w < children.length; w++) {
					if (j < w) {
						BoolExpr temp = ctx.mkNot(ctx.mkAnd(ctx.mkEq(children[j], ctx.mkBool(true)),
								ctx.mkEq(children[w], ctx.mkBool(true))));
						if (!lista.contains(temp))
							lista.add(temp);

					}
				}
			}
			BoolExpr[] temp = new BoolExpr[lista.size()];
			for (int h = 0; h < lista.size(); h++) {
				temp[h] = lista.get(h);
			}
			TreeStructure.add(ctx.mkAnd(primera_parte, ctx.mkAnd(temp)));

		}
	}

	public void GenerateTreeStructure() {
		BoolExpr Estructura[] = new BoolExpr[TreeStructure.size()];
		for (int i = 0; i < TreeStructure.size(); i++) {
			Estructura[i] = TreeStructure.get(i);
		}
		solver.Assert(ctx.mkAnd(Estructura));
	}

	/******************************
	 * TREE DEFINITION
	 *********************************/
	/******************************
	 * CONSTRAINTS DEFINITION
	 *********************************/
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
				TreeStructure.add(ctx.mkImplies(ctx.mkEq(ctx.mkBoolConst(features[0]), ctx.mkBool(true)),
						ctx.mkEq(ctx.mkBoolConst(features[1]), ctx.mkBool(true))));
			} else {
				String[] features = array[0].split(":");
				propExcludesCTCs[countExcludes][0] = features[0];
				propExcludesCTCs[countExcludes][1] = features[1];
				countExcludes++;
				TreeStructure.add(ctx.mkNot(ctx.mkAnd(ctx.mkEq(ctx.mkBoolConst(features[0]), ctx.mkBool(true)),
						ctx.mkEq(ctx.mkBoolConst(features[1]), ctx.mkBool(true)))));

			}
		}
	}

	/******************************
	 * CONSTRAINTS DEFINITION
	 *********************************/
	/******************************
	 * SOLUTIONC DEFINITION
	 *********************************/
	public void SCSolver() {
		int minimizers = 0;
		int maximizers = 0;
		try {
			minimizers = Integer.parseInt(properties.getProperty("SCMinimizers").trim());
			maximizers = Integer.parseInt(properties.getProperty("SCMaximizers").trim());
		} catch (Exception e) {

		}
		int minimize = 0;
		//System.out.print(",,,,"+Atts.size());
		IntExpr[] arrtemp2 = new IntExpr[Atts.size()];

		for (int j = 0; j < Atts.size(); j++) {
			minimize = 0;
			ArrayList<IntExpr> arr = new ArrayList<IntExpr>();
			for (int feats = 0; feats < FeatureAtts.size(); feats++) {
				String[] temp = FeatureAtts.get(feats).toString().split("\\.");
				if (temp[1].equals(Atts.get(j).toString())) {
					arr.add(FeatureAtts.get(feats));
				}
			}

			IntExpr[] temp = new IntExpr[arr.size()];
			for (int feats = 0; feats < arr.size(); feats++) {
				temp[feats] = arr.get(feats);
			}
			// aca me toca modificar

			
			solver.Assert(ctx.mkEq(Atts.get(j), ctx.mkAdd(temp)));
			arrtemp2[j] = Atts.get(j);
			if (minimizers == 0 && maximizers == 0) {// escenario para experimentacion cuando no trae configurados los
				solver.MkMinimize(Atts.get(j));
				/*
				 * // sc for (int minim = 0; minim < 4; minim++) { }
				 */
				minimize = 1;
			} else {
				for (int minim = 0; minim < minimizers; minim++) {
					String minimizer = properties.getProperty("SCMinimize" + minim).trim();
					if (Atts.get(j).toString().equals(minimizer)) {
						solver.MkMinimize(Atts.get(j));
						minimize = 1;
						break;
					}
				}
				for (int maxim = 0; maxim < maximizers; maxim++) {
					String maximizer = properties.getProperty("SCMaximize" + maxim).trim();
					if (propAttributeTypes[j].equals(maximizer)) {

						solver.MkMaximize(Atts.get(j));
						minimize = 2;
						break;
					}
				}
			}
			for (int feats = 0; feats < arr.size(); feats++) {
				if (minimize == 1)
					solver.MkMinimize(arr.get(feats));
				else if (minimize == 2)
					solver.MkMaximize(arr.get(feats));

			}

		}
		solver.Assert(ctx.mkEq(totalizador_SC, ctx.mkAdd(arrtemp2)));
		solver.MkMinimize(totalizador_SC);
	}

	/******************************
	 * SOLUTIONC DEFINITION
	 *********************************/
	
	public String[] solvez3(int numsols) {
		Params p = ctx.mkParams();
		p.add("priority", "pareto");
		solver.setParameters(p);
		String[] salidas = { "", "" };
		String soluciones = "";
		String salida = "";

		try {
			long ini = System.nanoTime();

			long tiempo_solucion = 0;
			int solutions = 0;
			
			while (Status.SATISFIABLE == solver.Check() && solutions <= numsols) { 
			//if(Status.SATISFIABLE == solver.Check()) {
			if (solutions == 0) {
					tiempo_solucion = System.nanoTime() - ini;
					ini = System.nanoTime();
				}
				salida += "PUNTO_DE_DIVISION_SALIDA\n";

				solutions++;
				Model model = solver.getModel();
				for (int i = 0; i < totalVars.size(); i++) {
					
					Expr v = model.evaluate((Expr) totalVars.get(i), false);
					String valor = (v.toString().equals("false")) ? "0"
							: (v.toString().equals("true")) ? "1" : v.toString();

					salida += totalVars.get(i) + ":" + valor + "\n";
				}

			}
			salida = "SOLUCIONES:" + solutions + "\nCOMPONENTES:" + totalVars.size() + "\n" + salida;

			if (solutions == 0) {
				System.out.println("Modelo no es satisfiable");
			}

			long tiempo_productos = System.nanoTime() - ini;
			salidas[0] = soluciones;
			double analize = tiempo_productos / 1000000000.0;
			double solution = tiempo_solucion / 1000000000.0;
			salidas[1] = analize + ";" + solution + ";" + (analize + solution);
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}

		String[] x = { salida, solver.toString() };
		return x;
	}

	private Model check(Context ctx2, BoolExpr boolExpr, Status satisfiable) {
		// TODO Auto-generated method stub
		return null;
	}

}