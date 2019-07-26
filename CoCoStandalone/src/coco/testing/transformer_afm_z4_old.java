package coco.testing;


import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Params;

import coco.util.AntExecutor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class transformer_afm_z4_old {
	public static int max_dom = 100000000;
	public static double percentage = 0.05;
	//public static JSONArray variables = new JSONArray();
	//public static JSONArray constraints = new JSONArray();
	//public static JSONArray optimizations = new JSONArray();
	public static HashMap optimizations_hash = new HashMap();
	public static HashMap <String,BoolExpr> Features = new HashMap();
	public static HashMap Ids = new HashMap();
	public static ArrayList<String> Relationships = new ArrayList();
	public static ArrayList<String> Attributes = new ArrayList();
	public static ArrayList<String> Constraints = new ArrayList();
	public static boolean test = true;
	public static ArrayList<String> Attributes_def = new ArrayList();
	public static ArrayList<String> Decision_Rules = new ArrayList();
	public static ArrayList<String> CrossModelConstraints = new ArrayList();
	public static String Relationships_id = "Relationships";
	public static String Attributes_id = "Attributes";
	public static String Constraints_id = "Constraints";
	public static ArrayList<String> Constraints_selected = new ArrayList();
	public static Map<String, ArrayList> Attributes_Gen = new HashMap();
	public static Map<String, ArrayList> Totalizer_Gen = new HashMap();
	static Map operaciones = new HashMap();
	
	
	
	static Context ctx;
	private static Optimize solver;// Solver solver;
	private Optimize opt;
	private static List<BoolExpr> CTCs = new ArrayList();
	private static List<BoolExpr> TreeStructure = new ArrayList();
	static ArrayList <BoolExpr> params = new ArrayList();
	static private List<IntExpr> FeatureAtts = new ArrayList();
	static private HashMap<String,IntExpr> Atts = new HashMap();
	
	
	public transformer_afm_z4_old() {
		super();
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		ctx = new Context(cfg);
		solver = ctx.mkOptimize();// ctx.mkSolver();
		opt = ctx.mkOptimize();
		transformer_afm_z4_old.totalizador_SC = ctx.mkIntConst("totalizados_SC");
		
		
		operaciones.put("lt", "<");
		operaciones.put("leq", "<=");
		operaciones.put("eq", "=");
		operaciones.put("gt", ">=");
		operaciones.put("geq", ">");

		optimizations_hash = new HashMap();
		Features = new HashMap();
		Ids = new HashMap();
		Relationships = new ArrayList();
		Attributes = new ArrayList();
		Constraints = new ArrayList();
		Attributes_def = new ArrayList();
		Decision_Rules = new ArrayList();
		CrossModelConstraints = new ArrayList();
		Relationships_id = "Relationships";
		Attributes_id = "Attributes";
		Constraints_id = "Constraints";
		Constraints_selected = new ArrayList();
		Attributes_Gen = new HashMap();
		Totalizer_Gen = new HashMap();

	}

	public static void main(String ar[]) {
		

	}

	public static String namedef = "";

	public static void Generate_Model(String a, String namemodel) {
		if (!test) {
			String[][] properties1 = new String[2][2];
			properties1[0][0] = "coco2afm";
			properties1[0][1] = a;
			properties1[1][0] = "cocoModel";
			properties1[1][1] = a + "/" + namemodel + ".xmi";
			AntExecutor antExecutor1 = new AntExecutor("workflow/build-fama2cocoM2M.xml", properties1);
		} else {
			Relationships = new ArrayList();
			Attributes = new ArrayList();
			Constraints = new ArrayList();
			feature_leafs = new ArrayList();
			namedef = namemodel;
			if (a != null && !a.equals("")) {
				a = readAllBytesJava7(a);

				a = a.replaceAll("\n", "");
				a = a.replaceAll("\\%Relationships", "\\%Relationships;");
				a = a.replaceAll("\\%Attributes", "\\%Attributes;");
				a = a.replaceAll("\\%Constraints", "\\%Constraints;");
				a = a.replaceAll("\\'", "");
				String[] afm = a.split("%");

				for (int i = 0; i < afm.length; i++) {
					// String[] afm2 = afm[i].split(System.getProperty("line.separator"));
					String[] afm2 = afm[i].split(";");

					if (afm[i].contains(Relationships_id)) {
						for (int j = 0; j < afm2.length; j++) {
							String obj = afm2[j].trim();
							if (!obj.trim().equals("Relationships") && !obj.trim().equals("")
									&& !Relationships.contains(obj)) {

								Relationships.add(obj);
							}
						}
					}
					if (afm[i].contains(Attributes_id)) {
						for (int j = 0; j < afm2.length; j++) {
							String obj = afm2[j].trim();
							if (!obj.trim().equals("Attributes") && !obj.trim().equals("")) {
								Attributes.add("(" + namedef + ")_" + obj);
							}
						}
					}
					if (afm[i].contains(Constraints_id)) {
						for (int j = 0; j < afm2.length; j++) {
							String obj = afm2[j].trim();
							if (!obj.trim().equals("Constraints") && !obj.trim().equals("")) {
								Constraints.add(obj);
							}
						}
					}
				}

			}
			try {
				getFeatures();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

	public static int[] get_random(int m, int n) {
		int[] salida = new int[n];
		ArrayList numbers = new ArrayList();
		for (int i = 0; i < m; i++) {
			numbers.add(i);
		}
		Collections.shuffle(numbers);
		for (int j = 0; j < n; j++) {
			salida[j] = (int) numbers.get(j);
		}
		return salida;
	}

	public static String getModelData(String b, int tipo_entrada, double perc, int sols_exp,boolean fix) {
		String salida = "";
		if(!test) {
			String[][] properties = new String[2][2];
			properties[0][0] = "cocoModel_DSL";
			properties[0][1] = b;
			properties[1][0] = "coco2Coco";
			properties[1][1] = b.split(".")[0]+ ".xmi";
			if (true) {// entra aca cuando tiene atributos
				AntExecutor antExecutor = new AntExecutor("workflow/build-coco2cocoM2T.xml", properties);
				salida = b.split(".")[0]+ ".xmi";
			}
			String[][] properties1 = new String[2][2];
			properties[0][0] = "cocoModel";
			properties[0][1] =b;
			properties[1][0] = "coco2z3";
			properties[1][1] = b.split(".")[0] + ".txt";
			if (true) {// entra aca cuando tiene atributos
				AntExecutor antExecutor = new AntExecutor("workflow/build-coco2Z3M2T.xml", properties);
				salida =  b.split(".")[0] + ".txt";
			}
			return "";
		}else {
		percentage = perc;
		if (b != null && !b.equals("")) {
			b = readAllBytesJava7(b);
			String[] conf = b.split("Solution_Constraints");
			if (sols_exp == 0) {
				if (conf.length > 1 && b.contains("Solution_Constraints")) {
					String dec_rules = conf[1].split("}")[0];
					String dec_rules1 = dec_rules.split("\\{")[1];
					String[] decRules = dec_rules1.split(System.getProperty("line.separator"));
					for (int i = 0; i < decRules.length; i++) {
						String o = decRules[i].trim();
						if (o != "") {
							Decision_Rules.add(o);
						}
					}
				}
			} else if (sols_exp > 0) {
				for (int i = 0; i < sols_exp; i++) {
					String o = "optimization o" + i + ": minimize Atribute" + i;
					Decision_Rules.add(o);
				}
			}
			if (sols_exp == 0) {
				String[] Atts = null;
				if (conf[0].contains("create")) {
					Atts = conf[0].split("create");
				} else if (conf[2].contains("create")) {
					Atts = conf[2].split("create");
				}

				for (int i = 0; i < Atts.length; i++) {

					if (Atts[i].trim() != "" && Atts[i].contains("measuredIn")) {
						Attributes_def.add(Atts[i].trim());
					}
				}
			} else {
				
				int fix_attributes = sols_exp;
				if(fix)
					fix_attributes = 20;
				for (int i = 0; i < fix_attributes; i++) {
					Attributes_def.add("attribute" + (i + 1) + " : Atribute" + i + " measuredIn \"number\"");
				}
			}
			if (tipo_entrada == 1) {
				HashMap<String, ArrayList> FeatsModels = new HashMap();
				ArrayList<String> modelosa = new ArrayList();
				int index = 0;
				int modelos = 0;
				for (Object key : Features.keySet()) {

					if ((key + "").contains("(") && (key + "").contains(")")) {
						String o = key + "";
						String[] u = o.split("\\(");
						String[] d = u[1].split("\\)_");
						String model = d[0];
						String feat = d[1];
						index++;

						if (FeatsModels.containsKey(model)) {
							FeatsModels.get(model).add(feat);
						} else {
							modelos++;
							modelosa.add(model);
							ArrayList Feats_per_Model = new ArrayList();
							Feats_per_Model.add(feat);
							FeatsModels.put(model, Feats_per_Model);
						}
					}
				}
				if(modelos > 1) {
				int num_feats_cmcs = (int) (index*percentage);
				
					
					for(int w = 0; w< num_feats_cmcs;w++) {
						
						int xx []=get_random(modelos,2);
						
						String curr_model1 = modelosa.get(xx[0]);
						
						int yy []=get_random(FeatsModels.get(curr_model1).size(),2);
						String curr_feat1 = ""+ FeatsModels.get(curr_model1).get(yy[0]);
						String curr_model2 = modelosa.get(xx[1]);
						String curr_feat2 = ""+ FeatsModels.get(curr_model2).get(yy[1]);
						CrossModelConstraints.add(curr_model1+"."+curr_feat1+" REQUIRES "+curr_model2+"."+curr_feat2);
					}
				
				}
			}
			String[] CMCS = b.split("Cross_Model_Constraints");
			if (CMCS.length > 1 && b.contains("Cross_Model_Constraints")) {
				String cmc_rules = CMCS[1].split("}")[0];

				String cmc_rules1 = cmc_rules.split("\\{")[1];

				String[] cmcRules = cmc_rules1.split(System.getProperty("line.separator"));
				for (int i = 0; i < cmcRules.length; i++) {
					String o = cmcRules[i].trim();
					if (o != "") {
						CrossModelConstraints.add(o);
					}
				}
			}

			try {
				//TreeStructure = new ArrayList();
				Atts = new HashMap();
				getOptimizations();
				getAttributes();
				generate_attributes_generic();
				getContraints();
				getDecisionRules();
				getCrossModelConstraints();
				GenerateTreeStructure();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Params p = ctx.mkParams();
			p.add("priority", "pareto");
			solver.setParameters(p);
			
			return solver.toString();
		} else {
			return "";
		}
		}

	}

	private static void getCrossModelConstraints()  {
		for (int i = 0; i < CrossModelConstraints.size(); i++) {
			if (!CrossModelConstraints.get(i).trim().equals("")) {
				String obj = CrossModelConstraints.get(i).trim();
				String[] lados = obj.split(" ");

				String izquierda = lados[0].replaceAll("\\.", "\\)_");
				izquierda = "(" + izquierda;
				String derecha = lados[2].replaceAll("\\.", "\\)_");
				derecha = "(" + derecha.replaceAll(";", "");
				if (lados[1].contains("REQUIRES")) {

					generate_constraints("REQUIRES", izquierda, derecha);
				}
				if (lados[1].contains("EXCLUDES")) {
					generate_constraints("EXCLUDES", izquierda, derecha);
				}
			}
		}
	}

	private static void getDecisionRules()  {
		for (int i = 0; i < Decision_Rules.size(); i++) {

			String obj = Decision_Rules.get(i).trim();
			/*if (obj.contains("hardLimit")) {

				String frst[] = obj.split(":");
				String items[] = frst[1].trim().split(" ");

				try {
					String origen = items[0].trim();
					String valor = items[2].replaceAll(";", "").trim();
					if (operaciones.get(items[1]) != null && Features.get(origen) != null) {
						JSONObject item = new JSONObject();
						JSONArray params = new JSONArray();

						params.put("#" + Features.get(origen));
						params.put(operaciones.get(items[1]));

						params.put((Features.get(valor) != null) ? "#" + Features.get(valor) : Integer.parseInt(valor));
						item.put("type", "arithm");
						item.put("params", params);

						constraints.put(item);
					}

				} catch (Exception e) {
					e.printStackTrace();

				}

			}
			if (obj.contains("selectionState")) {
				String frst[] = obj.split(":");
				String items[] = frst[1].trim().split(" ");
				try {
					String origen = "(" + namedef + ")_" + items[0].trim();
					if (Features.get(origen) != null) {
						JSONObject item = new JSONObject();
						JSONArray params = new JSONArray();

						params.put("#" + Features.get(origen));
						params.put("=");
						String valor = items[1].replaceAll(";", "").trim();
						params.put((valor.equals("mandatory")) ? 1 : 0);
						item.put("type", "arithm");
						item.put("params", params);

						constraints.put(item);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
			*/
		}
	}

	private static void getOptimizations() {
		for (int i = 0; i < Decision_Rules.size(); i++) {
			String obj = Decision_Rules.get(i).trim();
			if (obj.contains("optimization")) {
				String frst[] = obj.split(":");
				String items[] = frst[1].trim().split(" ");
				try {

					String valor = items[1].trim().replaceAll(";", "");
					if (!optimizations_hash.containsKey(valor)) {
						optimizations_hash.put(valor, items[0].trim());
					}
					

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}
	}

	private static void getContraints()  {
		System.out.println(Constraints.size());
		for (int i = 0; i < Constraints.size(); i++) {
			if (!Constraints.get(i).trim().equals("")) {
				String obj = Constraints.get(i).trim();
				String[] lados = obj.split(" ");
				if (lados[1].contains("REQUIRES")) {
					generate_constraints("REQUIRES", "(" + namedef + ")_" + lados[0], "(" + namedef + ")_" + lados[2]);
				}
				if (lados[1].contains("EXCLUDES")) {
					generate_constraints("EXCLUDES", "(" + namedef + ")_" + lados[0], "(" + namedef + ")_" + lados[2]);
				}
			}
		}
	}

	private static void generate_constraints(String type, String right, String left) {
		if(type.trim().equals("REQUIRES")) {
			TreeStructure.add(ctx.mkImplies(ctx.mkEq(Features.get(left), ctx.mkBool(true)),
					ctx.mkEq(Features.get(right), ctx.mkBool(true))));
		}else if(type.trim().equals("EXCLUDES")) {
			TreeStructure.add(ctx.mkNot(ctx.mkAnd(ctx.mkEq(Features.get(left), ctx.mkBool(true)),
					ctx.mkEq(Features.get(right), ctx.mkBool(true)))));
		}
	}

	private static void getAttributes()  {
		//Attributes = new ArrayList();
		
		if (Attributes.size() == 0) {// no encontró atributos
			
			HashMap<Object, ArrayList> Feats = (HashMap) Features.clone();
			for (Object key : feature_leafs) {
				for (int i = 0; i < Attributes_def.size(); i++) {
					String[] frst = Attributes_def.get(i).split(":");
					String[] attr = frst[1].split("measuredIn");
					String attr_feat = "" + key;
					String attr_gen = attr[0].trim();
					String obj = attr_feat + "." + attr_gen + " : Integer[1 to 100], 0, 0";
					Attributes.add(obj);

				}
			}
		}
		for (int i = 0; i < Attributes.size(); i++) {

			String[] frst = Attributes.get(i).split(":");
			String[] attr = frst[0].split("\\.");
			String attr_feat = attr[0].trim();
			String attr_gen = attr[1].trim();
			if (frst[1].contains("Integer")) {
				String[] prim = frst[1].split("Integer\\[");
				String[] def = prim[1].split("\\]");

				String[] minmax = def[0].split("to");
				String mindom = minmax[0].trim();
				String maxdom = minmax[1].trim();
				if (optimizations_hash.containsKey(attr_gen) && optimizations_hash.get(attr_gen).equals("maximize")) {
					String tmpmindom = (Integer.parseInt(maxdom) * (-1)) + "";
					maxdom = (Integer.parseInt(mindom) * (-1)) + "";
					mindom = tmpmindom;
				}

				IntExpr attr_id = generate_attribute(attr_feat, attr_gen, mindom, maxdom);
				if (Attributes_Gen.containsKey(attr_gen)) {
					ArrayList arr = Attributes_Gen.get(attr_gen);
					arr.add(attr_id);
					Attributes_Gen.replace(attr_gen, arr);
				} else {
					ArrayList arr = new ArrayList();
					arr.add(attr_id);
					Attributes_Gen.put(attr_gen, arr);
				}

			} else {
				System.out.println("Attributo no soportado");

			}

		}

	}

	private static void generate_attributes_generic() {
		IntExpr[] arrtemp2 = new IntExpr[Attributes_Gen.size()];
		int index = 0;
		for (Map.Entry<String, ArrayList> entry : Attributes_Gen.entrySet()) {
			String nwindex = "" + getIndex();
			ArrayList<IntExpr> arr = new ArrayList<IntExpr>();
			for (int feats = 0; feats < entry.getValue().size(); feats++) {

					arr.add((IntExpr) entry.getValue().get(feats));
				
			}

			IntExpr[] temp = new IntExpr[arr.size()];
			for (int feats = 0; feats < arr.size(); feats++) {
				temp[feats] = arr.get(feats);
			}
			// aca me toca modificar

			ArithExpr tmp1s =ctx.mkAdd(temp); 
			solver.Assert(ctx.mkEq(Atts.get(entry.getKey()),tmp1s ));
			arrtemp2[index] = Atts.get(entry.getKey());
			if(optimizations_hash.containsKey(Atts.get(entry.getKey())+"".trim())) {
				solver.MkMinimize(Atts.get(entry.getKey()));
			}
			//solver.MkMinimize(Atts.get(entry.getKey()));
			index++;
			
		}
		//solver.Assert(ctx.mkEq(totalizador_SC, ctx.mkAdd(arrtemp2)));
		//solver.MkMinimize(totalizador_SC);
	}
    static private IntExpr totalizador_SC;
	static boolean inicia_optional = false;
	static boolean inicia_alternative_or = false;
	static String feature = "";
	static String type_gate = "";
	static ArrayList feature_leafs = new ArrayList();
	private static void getFeatures() {
		
		for (int i = 0; i < Relationships.size(); i++) {

			String[] ff = Relationships.get(i).split(":");
			String fp = ff[0].trim();
			BoolExpr id_padre;
			if (i == 0) {
				id_padre = root(fp);
				
				
			} else {
				id_padre = not_root(fp);
				
				feature_leafs.remove("(" + namedef + ")_" + fp);
				
			}
			String[] feats_local = ff[1].split(" ");

			inicia_optional = false;
			inicia_alternative_or = false;
			feature = "";
			type_gate = "";
			params = new ArrayList();
			for (String obj : feats_local) {
				if (obj.trim().equals("")) {
					continue;
				}
				// INICIO BLOQUE DE CODIGO DEL OPTIONAL

				if (obj.contains("[") && !obj.contains(",")) {// inicia un optional
					inicia_optional = true;
					if (obj.contains("]")) {
						inicia_optional = false;
						obj = obj.replaceAll("\\[", "");
						feature = obj.replaceAll("\\]", "");
						optional_mandatory("optional", id_padre, not_root(feature));
						feature = "";
					}
					continue;
				}
				if (inicia_optional) {
					if (obj.contains("]")) {// termina un optional
						inicia_optional = false;
						feature += obj.replaceAll("\\]", "");
						optional_mandatory("optional", id_padre, not_root(feature));
						feature = "";
					} else {// mantiene un optional
						feature += obj;
					}
					continue;
				}
				// FIN BLOQUE DE CODIGO DEL OPTIONAL
				// INICIO BLOQUE DE CODIGO DEL ALTERNATIVE OR
				if (obj.contains("[") && obj.contains(",")) {// inicia un alternative
					inicia_alternative_or = true;

					String max = obj.split("\\,")[1];
					if (max.contains("{")) {
						String[] temp1 = max.split("\\{");
						max = temp1[0].replaceAll("\\]", "");
						if (temp1.length > 1 && !temp1[1].trim().equals("")) {
							String temp2 = temp1[1].trim().replaceAll("\\{", "");
							params.add(not_root(temp2));
						}
					} else {
						max = max.replaceAll("\\]", "");
					}
					type_gate = (Integer.parseInt(max.trim()) > 1) ? "or" : "alternative";
					continue;
				}
				if (inicia_alternative_or) {
					if (obj.contains("}")) {// termina un alternative
						inicia_alternative_or = false;
						obj = obj.replaceAll("\\}", "").trim();
						if (!obj.trim().equals("")) {
							params.add(not_root(obj));

						}
						alternative_or(type_gate, params,  id_padre);
						feature = "";
						params = new ArrayList();
						type_gate = "";

					} else {// mantiene un optional
						obj = obj.replaceAll("\\{", "");
						params.add(not_root(obj.trim()));
					}
					continue;
				}
				// FIN BLOQUE DE CODIGO DEL ALTERNATIVE OR
				// SI NO ENTRA A NADA ES MANDATORY
				
				optional_mandatory("mandatory", id_padre, not_root(obj.trim()));

			}

		}

	}
	static List totalVars = new ArrayList();
	static List Feats = new ArrayList();
	private static IntExpr generate_attribute(String attr_feat, String attr_gen, String mindom, String maxdom) {
		
		String name = attr_feat + "." + attr_gen;
		IntExpr sss = ctx.mkIntConst(name);// propAttributeTypes[j]);
		try {
			

			
			totalVars.add(sss);

			IntExpr lower = ctx.mkInt(mindom);
			IntExpr higher = ctx.mkInt(maxdom);
			IntExpr defaultv = ctx.mkInt(mindom);
			BoolExpr c1 = ctx.mkLe(sss, higher);
			BoolExpr c2 = ctx.mkGe(sss, defaultv);
			BoolExpr q = ctx.mkAnd(c1, c2);
			BoolExpr feature = Features.get(attr_feat);
			if(feature != null) {
			BoolExpr imp1 = ctx.mkImplies(ctx.mkEq(feature, ctx.mkBool(true)), q);
			BoolExpr imp2 = ctx.mkImplies(ctx.mkEq(feature, ctx.mkBool(false)), ctx.mkEq(sss, ctx.mkInt(0)));
			solver.Assert(ctx.mkAnd(imp1,imp2));
			if (!FeatureAtts.contains(sss))
				FeatureAtts.add(sss);
			
			if (!Feats.contains(feature)) {
				Feats.add(feature);
			}
			if (!Atts.containsKey(attr_gen)) {
				IntExpr att = ctx.mkIntConst(attr_gen);
				Atts.put(attr_gen, att);
				
				totalVars.add(att);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sss;
	}

	private static void alternative_or(String type_gate, ArrayList<BoolExpr> involvedFeatures,  BoolExpr id_padre) {
		if(type_gate.equals("alternative")){
			
			BoolExpr parent = id_padre;
			BoolExpr[] children = new BoolExpr[involvedFeatures.size()];
			for (int j = 0; j < children.length; j++) {
				children[j] = ctx.mkEq(involvedFeatures.get(j ), ctx.mkBool(true));// features.get(involvedFeatures.get(j
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
		}else {
			
				BoolExpr parent = id_padre;
				BoolExpr[] children = new BoolExpr[involvedFeatures.size()];

				for (int j = 0; j < children.length; j++) {

					children[j] = ctx.mkEq(involvedFeatures.get(j ), ctx.mkBool(true));

				}
				TreeStructure.add(ctx.mkImplies(ctx.mkEq(parent, ctx.mkBool(true)), ctx.mkOr(children)));
		}

	}

	private static void optional_mandatory(String mandatory, BoolExpr id_padre, BoolExpr id_hijo) {
		
		String condicion = "";
		if (mandatory == "mandatory") {
			try {
				CTCs.add(ctx.mkAnd(id_padre, id_hijo));
				TreeStructure.add(ctx.mkAnd(
						ctx.mkImplies(ctx.mkEq(id_hijo, ctx.mkBool(true)),
								ctx.mkEq(id_padre, ctx.mkBool(true))),
						ctx.mkImplies(ctx.mkEq(id_padre, ctx.mkBool(true)),
								ctx.mkEq(id_hijo, ctx.mkBool(true)))));
			} catch (Exception e) {
				System.out.println("constraint paila:" + id_padre + "::::" + id_hijo);
			}
		}else {//escenario del OPTIONAL
			try {
				CTCs.add(ctx.mkAnd(id_padre, id_hijo));
				/*TreeStructure.add(ctx.mkOr(
						ctx.mkImplies(ctx.mkEq(id_padre, ctx.mkBool(true)),
								ctx.mkEq(id_hijo, ctx.mkBool(false))),
						ctx.mkImplies(ctx.mkEq(id_padre, ctx.mkBool(true)),
								ctx.mkEq(id_hijo, ctx.mkBool(true)))));
				*/
				TreeStructure.add(ctx.mkImplies(ctx.mkEq(id_hijo, ctx.mkBool(true)),
						ctx.mkEq(id_padre, ctx.mkBool(true))));
			} catch (Exception e) {
				System.out.println("constraint paila:" + id_padre + "::::" + id_hijo);
			}
		}

	}

	private static BoolExpr root(String name) {
		int nwindex = getIndex();
		name = "(" + namedef + ")_" + name;
		try {
			

			if (!Features.containsKey(name)) {
				BoolExpr x = ctx.mkBoolConst(name);
				TreeStructure.add(ctx.mkEq(x, ctx.mkBool(true)));
				Features.put(name, x);
				return x;
			} else {
				return Features.get(name);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}

	}

	/*private static String[] cons_reif(String name, int size) {
		name = "(" + namedef + ")_" + name;
		String[] salida = new String[2];
		int nwindex = getIndex();

		try {
			name = "SUM_OR_" + name;
			JSONObject item = new JSONObject();
			item.put("type", "ivar");
			item.put("name", name);
			item.put("id", "#" + nwindex);
			item.put("dom", "{0.." + size + "}");
			if (!Features.containsKey(name)) {
				variables.put(item);
				Features.put(name, "#" + nwindex);
				String nwindex2 = reif();
				salida[0] = nwindex + "";
				salida[1] = nwindex2;

			} else {
				salida[0] = (Features.get(name) + "").replaceAll("\\#", "");
				salida[1] = "" + (Integer.parseInt((Features.get(name) + "").replaceAll("\\#", "")) + 1);

			}
			return salida;
		} catch (Exception e) {
			salida[0] = "error";
			salida[1] = e.getMessage();
			return salida;

		}

	}
*/
	static int contador_reif = 1;
/*
	private static String reif() {
		contador_reif++;
		String nwindex2 = "" + getIndex();
		try {

			JSONObject item2 = new JSONObject();
			item2.put("type", "bvar");
			item2.put("name", "REIF_" + contador_reif);
			item2.put("id", "#" + nwindex2);
			if (!Features.containsKey("REIF_" + contador_reif)) {
				variables.put(item2);
				Features.put("REIF_" + contador_reif, "#" + nwindex2);

			}

			String nwindex3 = "" + getIndex();
			item2 = new JSONObject();
			item2.put("type", "not");
			item2.put("name", "not(" + "REIF_" + contador_reif + ")");
			item2.put("id", "#" + nwindex3);
			item2.put("of", "#" + nwindex2);
			if (!Features.containsKey("not(" + "REIF_" + contador_reif + ")")) {
				variables.put(item2);
				Features.put(nwindex2, nwindex3);
				return nwindex2;
			}

		} catch (Exception e) {
			System.out.println("REIF ERROR");
			return "error";
		}
		return nwindex2;
	}
*/
	private static BoolExpr not_root(String name) {
		int nwindx = getIndex();
		name = "(" + namedef + ")_" + name;
		feature_leafs.add(name);
		try {
			if (!Features.containsKey(name)) {
				BoolExpr x = ctx.mkBoolConst(name);
				Features.put(name, x);
				return x;
			} else {

				return Features.get(name);
			}
		} catch (Exception e) {
			return null;
		}
	}
	public static void GenerateTreeStructure() {
		BoolExpr Estructura[] = new BoolExpr[TreeStructure.size()];
		for (int i = 0; i < TreeStructure.size(); i++) {
			Estructura[i] = TreeStructure.get(i);
		}
		solver.Assert(ctx.mkAnd(Estructura));
	}
	static int index = 0;

	private static int getIndex() {
		index++;
		return index;
	}

	private static void print_everything() {
		for (int i = 0; i < Attributes.size(); i++) {
			System.out.println(Attributes.get(i));

		}
		for (int i = 0; i < Relationships.size(); i++) {
			System.out.println(Relationships.get(i));

		}
		for (int i = 0; i < Constraints.size(); i++) {
			System.out.println(Constraints.get(i));

		}
		for (int i = 0; i < Decision_Rules.size(); i++) {
			System.out.println(Decision_Rules.get(i));

		}
		for (int i = 0; i < Attributes_def.size(); i++) {
			System.out.println(Attributes_def.get(i));

		}
	}

	private static String readAllBytesJava7(String filePath) {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
