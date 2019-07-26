package coco.testing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import coco.util.AntExecutor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class transformer_afm_choco {
	public static int max_dom = 100000000;
	public static double percentage = 0.05;
	public static JSONArray variables = new JSONArray();
	public static JSONArray constraints = new JSONArray();
	public static JSONArray optimizations = new JSONArray();
	public static HashMap optimizations_hash = new HashMap();
	public static HashMap Features = new HashMap();
	public static boolean test = true;
	public static HashMap Ids = new HashMap();
	public static ArrayList<String> Relationships = new ArrayList();
	public static ArrayList<String> Attributes = new ArrayList();
	public static ArrayList<String> Constraints = new ArrayList();
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

	public transformer_afm_choco() {
		super();
		operaciones.put("lt", "<");
		operaciones.put("leq", "<=");
		operaciones.put("eq", "=");
		operaciones.put("gt", ">=");
		operaciones.put("geq", ">");
		variables = new JSONArray();
		constraints = new JSONArray();
		optimizations = new JSONArray();
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

	public static String Generate_Model(String a, String namemodel) {
		
		String salida = "";
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
			Attributes_def = new ArrayList();
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

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject model = new JSONObject();
			salida = model.toString();
		}
		return salida;

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

	public static String getModelData(String b, int tipo_entrada, double perc, int sols_exp, boolean fix) {
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
			properties[1][0] = "coco2Choco";
			properties[1][1] = b.split(".")[0] + ".json";
			if (true) {// entra aca cuando tiene atributos
				AntExecutor antExecutor = new AntExecutor("workflow/build-coco2ChocoM2T.xml", properties);
				salida =  b.split(".")[0] + ".json";
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
				if (fix)
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
						//System.out.println(key);

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
				if (modelos > 1 && percentage > 0) {
					int num_feats_cmcs = (int) (index * percentage);

					for (int w = 0; w < num_feats_cmcs; w++) {

						int xx[] = get_random(modelos, 2);

						String curr_model1 = modelosa.get(xx[0]);

						int yy[] = get_random(FeatsModels.get(curr_model1).size(), 2);
						String curr_feat1 = "" + FeatsModels.get(curr_model1).get(yy[0]);
						String curr_model2 = modelosa.get(xx[1]);
						String curr_feat2 = "" + FeatsModels.get(curr_model2).get(yy[1]);
						CrossModelConstraints
								.add(curr_model1 + "." + curr_feat1 + " REQUIRES " + curr_model2 + "." + curr_feat2);
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
				getOptimizations();
				getAttributes(sols_exp);
				generate_attributes_generic();
				getContraints();
				getDecisionRules();
				getCrossModelConstraints();
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject model = new JSONObject();
			try {
				model.put("name", "Model-0");
				model.put("variables", variables);
				model.put("constraints", constraints);
				model.put("optimizations", optimizations);
			} catch (Exception e) {
				e.printStackTrace();

			}
			return model.toString();
		} else {
			return "";
		}
		}

	}

	private static void getCrossModelConstraints() throws JSONException {
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

	private static void getDecisionRules() throws JSONException {
		for (int i = 0; i < Decision_Rules.size(); i++) {

			String obj = Decision_Rules.get(i).trim();
			if (obj.contains("hardLimit")) {

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

		}
	}

	private static void getOptimizations() throws JSONException {
		for (int i = 0; i < Decision_Rules.size(); i++) {
			String obj = Decision_Rules.get(i).trim();
			if (obj.contains("optimization")) {
				String frst[] = obj.split(":");
				String items[] = frst[1].trim().split(" ");
				try {

					JSONObject item = new JSONObject();
					String valor = items[1].trim().replaceAll(";", "");
					item.put(items[0].trim(), valor);
					if (!optimizations_hash.containsKey(valor)) {
						optimizations_hash.put(valor, items[0].trim());
					}
					optimizations.put(item);

				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}
	}

	private static void getContraints() throws JSONException {
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
		String r1 = reif();
		try {
			if (Features.get(right) != null) {
				JSONObject item = new JSONObject();
				JSONObject subitem = new JSONObject();
				JSONArray params = new JSONArray();
				item.put("type", "reif");
				item.put("by", "#" + r1);

				subitem.put("type", "arithm");
				params.put("#" + Features.get(right));
				params.put("=");
				params.put(1);
				subitem.put("params", params);
				item.put("of", subitem);
				constraints.put(item);
			}
		} catch (Exception e) {

		}
		String r2 = reif();
		try {
			if (Features.get(left) != null) {
				JSONObject item = new JSONObject();
				JSONObject subitem = new JSONObject();
				JSONArray params = new JSONArray();
				item.put("type", "reif");
				item.put("by", "#" + r2);

				subitem.put("type", "arithm");
				params.put("#" + Features.get(left));
				params.put("=");
				params.put((type.equals("REQUIRES")) ? 1 : 0);
				subitem.put("params", params);
				item.put("of", subitem);
				constraints.put(item);
			}
		} catch (Exception e) {

		}
		try {// reif que relaciona la negacion del hijo con la del padre
			JSONObject item = new JSONObject();
			JSONArray params = new JSONArray();
			params.put("#" + r2);
			params.put(">=");
			params.put("#" + r1);
			item.put("type", "arithm");
			item.put("params", params);

			constraints.put(item);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private static void getAttributes(int sols_exp) throws JSONException {
		if (sols_exp > 0) {
			Attributes = new ArrayList();
		}
		if (Attributes.size() == 0) {// no encontró atributos

			HashMap<Object, ArrayList> Feats = (HashMap) Features.clone();
			for (Object key : feature_leafs) {
				for (int i = 0; i < Attributes_def.size(); i++) {
					String[] frst = Attributes_def.get(i).split(":");
					String[] attr = frst[1].split("measuredIn");
					String attr_feat = "" + key;
					String attr_gen = attr[0].trim();
					String obj = attr_feat + "." + attr_gen + " : Integer[0 to 100], 0, 0";
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

				String attr_id = generate_attribute(attr_feat, attr_gen, mindom, maxdom);
				if (Attributes_Gen.containsKey(attr_gen)) {
					ArrayList arr = Attributes_Gen.get(attr_gen);
					if (!arr.contains(attr_id)) {
						arr.add(attr_id);
						Attributes_Gen.replace(attr_gen, arr);
					}
				} else {
					ArrayList arr = new ArrayList();
					if (!arr.contains(attr_id)) {
						arr.add(attr_id);
						Attributes_Gen.put(attr_gen, arr);
					}
				}

			} else {
				System.out.println("Attributo no soportado");

			}

		}

	}

	private static void generate_attributes_generic() {
		JSONArray params_atts = new JSONArray();
		for (Map.Entry<String, ArrayList> entry : Attributes_Gen.entrySet()) {
			String nwindex = "" + getIndex();
			try {
				JSONObject item = new JSONObject();
				item.put("type", "ivar");
				item.put("name", entry.getKey());
				item.put("id", "#" + nwindex);
				item.put("dom", "{-" + max_dom + ".." + max_dom + "}");

				if (!Features.containsKey(entry.getKey())) {
					variables.put(item);

					Features.put(entry.getKey(), nwindex);

				} else {
					nwindex = Features.get(entry.getKey()) + "";
				}

			} catch (Exception e) {

			}
			params_atts.put("#" + nwindex);
			try {
				JSONObject item = new JSONObject();
				item.put("type", "sum");
				JSONArray params = new JSONArray();
				for (int i = 0; i < entry.getValue().size(); i++) {
					params.put("#" + entry.getValue().get(i));
				}
				params.put("#" + nwindex);
				JSONArray params2 = new JSONArray();
				params2.put(params);
				params2.put(params.length() - 1);
				params2.put("=");
				params2.put(0);
				item.put("params", params2);
				constraints.put(item);
			} catch (Exception e) {

			}
		}
		/*
		String nwindex = "" + getIndex();
		try {
			JSONObject item = new JSONObject();
			item.put("type", "ivar");
			item.put("name", "TOTALIZADOR");
			item.put("id", "#" + nwindex);
			item.put("dom", "{-" + max_dom + ".." + max_dom + "}");

			if (Features.containsKey("TOTALIZADOR")) {
				System.out.println("ERROR NO CONTROLADO");
			} else {
				variables.put(item);
			}

		} catch (Exception e) {

		}
		try {
			JSONObject item = new JSONObject();
			item.put("type", "sum");
			JSONArray params = new JSONArray();
			params_atts.put("#" + nwindex);
			JSONArray params2 = new JSONArray();
			params2.put(params_atts);
			params2.put(params_atts.length() - 1);
			params2.put("=");
			params2.put(0);
			item.put("params", params2);
			constraints.put(item);
		} catch (Exception e) {

		}
		*/
	}

	static boolean inicia_optional = false;
	static boolean inicia_alternative_or = false;
	static String feature = "";
	static String type_gate = "";
	static JSONArray params = new JSONArray();
	static ArrayList feature_leafs = new ArrayList();

	private static void getFeatures() throws JSONException {
		for (int i = 0; i < Relationships.size(); i++) {

			String[] ff = Relationships.get(i).split(":");
			String fp = ff[0].trim();
			String id_padre = "";
			if (i == 0) {
				id_padre = root(fp);
				if (id_padre.contains("#") || id_padre.contains("error")) {
					System.out.println("viene con erroorrrrrrrr:::" + id_padre + ".." + fp);
				}
			} else {
				id_padre = not_root(fp);
				feature_leafs.remove("(" + namedef + ")_" + fp);
				if (id_padre.contains("#") || id_padre.contains("error")) {
					System.out.println("viene con erroorrrrrrrr::222:" + id_padre + ".." + fp);
				}
			}
			String[] feats_local = ff[1].split(" ");

			inicia_optional = false;
			inicia_alternative_or = false;
			feature = "";
			type_gate = "";
			params = new JSONArray();
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
							params.put("#" + not_root(temp2));
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
							params.put("#" + not_root(obj));

						}
						alternative_or(type_gate, params, cons_reif(fp, params.length()), id_padre);
						feature = "";
						params = new JSONArray();
						type_gate = "";

					} else {// mantiene un optional
						obj = obj.replaceAll("\\{", "");
						params.put("#" + not_root(obj.trim()));
					}
					continue;
				}
				// FIN BLOQUE DE CODIGO DEL ALTERNATIVE OR
				// SI NO ENTRA A NADA ES MANDATORY

				optional_mandatory("mandatory", id_padre, not_root(obj.trim()));

			}

		}

	}

	private static String generate_attribute(String attr_feat, String attr_gen, String mindom, String maxdom) {
		String name = attr_feat + "." + attr_gen;
		String nwindex = "" + getIndex();
		boolean creado = false;
		try {
			JSONObject item = new JSONObject();
			item.put("type", "ivar");
			item.put("name", name);
			item.put("id", "#" + nwindex);
			if (Integer.parseInt(mindom) < 0) {
				item.put("dom", "{" + mindom + "..0}");
			} else {
				item.put("dom", "{0.." + maxdom + "}");
			}

			if (!Features.containsKey(name)) {
				variables.put(item);
				Features.put(name, "#" + nwindex);
				creado = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String r1 = reif();// reif que niega al padre
		if (creado) {
			try {
				if (Features.get(attr_feat) != null) {
					JSONObject item = new JSONObject();
					JSONObject subitem = new JSONObject();
					JSONArray params = new JSONArray();
					item.put("type", "reif");
					item.put("by", "#" + r1);

					subitem.put("type", "arithm");
					params.put("#" + Features.get(attr_feat));
					params.put("=");
					params.put(0);
					subitem.put("params", params);
					item.put("of", subitem);
					constraints.put(item);
				}

			} catch (Exception e) {

			}

			String r2 = reif();// reif que niega al atributo
			try {
				JSONObject item = new JSONObject();
				JSONObject subitem = new JSONObject();
				JSONArray params = new JSONArray();
				item.put("type", "reif");
				item.put("by", "#" + r2);

				subitem.put("type", "arithm");
				params.put("#" + nwindex);
				params.put("=");
				params.put(0);
				subitem.put("params", params);
				item.put("of", subitem);
				constraints.put(item);

			} catch (Exception e) {

			}
			String r3 = reif();// reif que dice que va a ser mayor la minimo del dom
			try {
				JSONObject item = new JSONObject();
				JSONObject subitem = new JSONObject();
				JSONArray params = new JSONArray();
				item.put("type", "reif");
				item.put("by", "#" + r3);

				subitem.put("type", "arithm");
				params.put("#" + nwindex);
				params.put(">=");
				params.put(Integer.parseInt(mindom.trim()));
				subitem.put("params", params);
				item.put("of", subitem);
				constraints.put(item);

			} catch (Exception e) {

			}
			try {// reif que relaciona la negacion del hijo con la del padre
				JSONObject item = new JSONObject();
				JSONArray params = new JSONArray();
				params.put("#" + r2);
				params.put(">=");
				params.put("#" + r1);
				item.put("type", "arithm");
				item.put("params", params);

				constraints.put(item);

			} catch (Exception e) {
				e.printStackTrace();

			}
			try {// reif que relaciona el reif que asigna un valor minimo al atributo con la
					// negación del primer reif
				if (Features.get(r1) != null) {
					JSONObject item = new JSONObject();
					JSONArray params = new JSONArray();
					params.put("#" + r3);
					params.put(">=");
					params.put("#" + Features.get(r1));
					item.put("type", "arithm");
					item.put("params", params);

					constraints.put(item);
				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		} else {
			nwindex = ("" + Features.get(name)).replaceAll("#", "").trim();
		}
		return nwindex;
	}

	private static void alternative_or(String type_gate, JSONArray atts, String[] id_sum, String id_padre) {

		try {
			int tamanio = atts.length();
			atts.put("#" + id_sum[0]);
			JSONObject item = new JSONObject();
			JSONArray params = new JSONArray();
			item.put("type", "sum");
			params.put(atts);
			params.put(tamanio);
			params.put("=");
			params.put(0);
			item.put("params", params);
			constraints.put(item);
		} catch (Exception e) {

		}
		String interm =reif(); 
		try {
			JSONObject item = new JSONObject();
			JSONObject subitem = new JSONObject();
			JSONArray params = new JSONArray();
			item.put("type", "reif");
			item.put("by", "#" + interm);//291

			subitem.put("type", "arithm");
			params.put("#" + id_padre);
			params.put("=");
			params.put(1);
			subitem.put("params", params);
			item.put("of", subitem);
			constraints.put(item);

		} catch (Exception e) {

		}
		try {
			JSONObject item = new JSONObject();
			JSONObject subitem = new JSONObject();
			JSONArray params = new JSONArray();
			item.put("type", "reif");
			item.put("by", "#" + id_sum[1]);

			subitem.put("type", "arithm");
			params.put("#" + id_sum[0]);
			params.put((type_gate.equals("alternative")) ? "=" : ">=");
			params.put(1);
			subitem.put("params", params);
			item.put("of", subitem);
			constraints.put(item);

		} catch (Exception e) {

		}

		try {
			JSONObject item = new JSONObject();
			JSONArray params = new JSONArray();
			item.put("type", "arithm");
			params.put("#" + id_sum[1]);//14
			params.put((type_gate.equals("alternative")) ? "=" : ">=");
			params.put("#" + interm);//16
			item.put("params", params);
			constraints.put(item);

		} catch (Exception e) {

		}
		String interm2 =reif(); 
		try {
			JSONObject item = new JSONObject();
			JSONObject subitem = new JSONObject();
			JSONArray params = new JSONArray();
			item.put("type", "reif");
			item.put("by", "#" + interm2);//300

			subitem.put("type", "arithm");
			params.put("#" + id_sum[0]);
			params.put("=");
			params.put(0);
			subitem.put("params", params);
			item.put("of", subitem);
			constraints.put(item);

		} catch (Exception e) {

		}
		
		try {
			JSONObject item = new JSONObject();
			JSONArray params = new JSONArray();
			item.put("type", "arithm");
			params.put("#" + interm2);//300
			params.put(">=");
			params.put("#" + (Integer.parseInt(interm)+1));//293
			item.put("params", params);
			constraints.put(item);

		} catch (Exception e) {

		}
	}

	private static void optional_mandatory(String mandatory, String id_padre, String id_hijo) {
		String condicion = "";
		if (mandatory == "mandatory")
			condicion = "=";
		else
			condicion = "<=";
		try {
			JSONObject item = new JSONObject();
			JSONArray params = new JSONArray();
			params.put("#" + id_hijo);
			params.put(condicion);
			params.put("#" + id_padre);
			item.put("type", "arithm");
			item.put("params", params);

			constraints.put(item);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private static String root(String name) {
		int nwindex = getIndex();
		name = "(" + namedef + ")_" + name;
		try {
			JSONObject item = new JSONObject();
			item.put("type", "ivar");
			item.put("name", name);
			item.put("id", "#" + nwindex);
			item.put("dom", "{1}");

			if (!Features.containsKey(name)) {
				variables.put(item);
				Features.put(name, nwindex);
				return nwindex + "";
			} else {
				return "" + Features.get(name);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "error";

		}

	}

	private static String[] cons_reif(String name, int size) {
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

	static int contador_reif = 1;

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

	private static String not_root(String name) {
		int nwindx = getIndex();
		name = "(" + namedef + ")_" + name;
		feature_leafs.add(name);

		try {
			JSONObject item = new JSONObject();
			item.put("type", "bvar");
			item.put("name", name);
			item.put("id", "#" + nwindx);
			if (!Features.containsKey(name)) {
				variables.put(item);
				Features.put(name, nwindx);
				return "" + nwindx;
			} else {

				return "" + Features.get(name);
			}
		} catch (Exception e) {
			return "error";
		}
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
