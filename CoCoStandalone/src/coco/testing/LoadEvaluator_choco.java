package coco.testing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.microsoft.z3.Context;

import org.chocosolver.parser.json.JSON;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.criteria.Criterion;
import org.chocosolver.solver.objective.ParetoOptimizer;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.solver.search.limits.TimeCounter;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.Solution;

//import com.ibm.icu.util.Measure;

public class LoadEvaluator_choco {
	private Model model;
	private IntVar[] totalVars;
	private IntVar[] totalModel;
	private HashMap optim = new HashMap();
	private Properties properties;
	private int limit_solution = 10;
	ByteArrayOutputStream baos;
	PrintStream ps;
	PrintStream old = System.out;

	public static void main(String[] a) {

		
	}

	public void simple_Ex() {
		Model model = new Model();
		IntVar a = model.intVar("a", -2, 0, false);
		IntVar b = model.intVar("b", -3, 2, false);
		IntVar c = model.intVar("c", -5, 2, false);
		model.arithm(a, "+", b, "=", c).post();

		// create an object that will store the best solutions and remove dominated ones
		ParetoOptimizer po = new ParetoOptimizer(Model.MINIMIZE, new IntVar[] { a, b });
		Solver solver = model.getSolver();
		solver.plugMonitor(po);

		// optimization
		while (solver.solve())
			;

		// retrieve the pareto front
		List<Solution> paretoFront = po.getParetoFront();
		System.out.println("The pareto front has " + paretoFront.size() + " solutions : ");
		for (Solution s : paretoFront) {
			System.out.println("a = " + s.getIntVal(a) + " and b = " + s.getIntVal(b));
		}
	}

	public String[] LoadEvaluator(String url, int numsols,boolean way,ArrayList<String>elements,int relacion, int tiempo) {
		limit_solution = numsols;
		model = new Model();
		File file = new File(url);
		System.out.println(url);
		getOptimizations(url);
		return solveCSP(file,way,elements,relacion,tiempo);

	}

	public void getOptimizations(String f) {
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(f)) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONObject modelo = (JSONObject) obj;
			JSONArray arr = (JSONArray) modelo.get("optimizations");
			for (Object o : arr) {
				JSONObject jsonObject = (JSONObject) o;
				for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					optim.put(jsonObject.get(key), key);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String[] solveCSP(File f, boolean way,ArrayList<String>elements,int relacion, int tiempo) {
		model = new Model();
		
		model = new Model();

		System.out.println("leyendo_modelo_c_"+way);
		model = JSON.readInstance(f);
		
		
		Variable[] vars = model.getVars();
		List<IntVar> intVars = new ArrayList();
		List<IntVar> intModel = new ArrayList();
		
		
		for (int i = 0; i < vars.length; i++) {
			if (!vars[i].toString().contains("REIF_") && !vars[i].toString().contains("SUM_OR_")) {
				try {
					if (!("" + vars[i]).contains(".") && !("" + vars[i]).contains("SUM_OR") && ((IntVar) vars[i]).getDomainSize() > 2
							&& optim.containsKey(vars[i].toString().split("=")[0].trim())) {
						intVars.add((IntVar) vars[i]);
					}
					intModel.add((IntVar) vars[i]);
				} catch (Exception e) {
					System.out.println("var not int:" + vars[i]);
				}
			}
		}
		totalVars = new IntVar[intVars.size()];
		for (int i = 0; i < totalVars.length; i++) {

			totalVars[i] = intVars.get(i);

		}
		totalModel = new IntVar[intModel.size()];
		for (int i = 0; i < totalModel.length; i++) {

			totalModel[i] = intModel.get(i);

		}

		String salida = "";
		int solss = 0;
		String [] tmp = new String [elements.size()];
		for (int j = 0; j < elements.size(); j++) {

			salida += elements.get(j) + ";";

		}
		salida += "\n";
		long time = tiempo * 1000000000L;
		long ini = System.nanoTime();
		long tiempo_solucion = 0;
		
		//System.out.println("modelo_leido_c_"+way);
		ini = System.nanoTime();
		if (way) {
			//Criterion solcpt = new TimeCounter(model, time);
			Criterion solcpt = new SolutionCounter(model, limit_solution);
			System.out.println("modelo_leido_c_"+way+"____"+limit_solution);
			List<Solution> solutions = model.getSolver().findParetoFront(totalModel, Model.MINIMIZE, solcpt);
			ArrayList<String> arr = new ArrayList<String>();
			
			for (Solution s : solutions) {
				ini = System.nanoTime();
				solss++;
				System.out.print(".");
				String doc = "";
				tmp = new String [elements.size()];
				for (int j = 0; j < totalModel.length; j++) {
					//if (!totalModel[j].getName().equals("TOTALIZADOR")) {
						
						int value = (optim.containsKey(totalModel[j].getName())
								&& optim.get(totalModel[j].getName()).equals("maximize"))
										? s.getIntVal(totalModel[j]) * -1
										: s.getIntVal(totalModel[j]);
						if(elements.indexOf(totalModel[j].getName())>= 0) {
							tmp[elements.indexOf(totalModel[j].getName())] = value+"";
						}
						

					//}
				}
				for( int j = 0;j< tmp.length;j++) {
					doc += tmp[j] + ";";
				}
				if (!arr.contains(doc)) {
					solss++;
					System.out.print(".");
					arr.add(doc);
					salida += doc + "\n";
				}
				tiempo_solucion = System.nanoTime() - ini;
				continue;

			}
		} else {
			long tini = System.nanoTime();
			ParetoOptimizer po = new ParetoOptimizer(Model.MINIMIZE, totalModel);
			Solver solver = model.getSolver();
			solver.plugMonitor(po);

			// optimization
			System.out.println("modelo_leido_c_"+way);
			/*if(limit_solution == 1) {
				solver.solve();
			}else {
				
			}*/
			boolean valid = false; 
			while (solver.solve() && !valid) {
				

			// retrieve the pareto front
			List<Solution> paretoFront = po.getParetoFront();
			ArrayList<String> arr = new ArrayList<String>();
			for (Solution s : paretoFront) {
				ini = System.nanoTime();

				//System.out.print(".");
				String doc = "";
				for (int j = 0; j < totalModel.length; j++) {
					//if (!totalModel[j].getName().equals("TOTALIZADOR")) {
						
						int value = (optim.containsKey(totalModel[j].getName())
								&& optim.get(totalModel[j].getName()).equals("maximize"))
										? s.getIntVal(totalModel[j]) * -1
										: s.getIntVal(totalModel[j]);
						if(elements.indexOf(totalModel[j].getName())>= 0) {
							tmp[elements.indexOf(totalModel[j].getName())] = value+"";
						}

					//}
				}
				for( int j = 0;j< tmp.length;j++) {
					doc += tmp[j] + ";";
				}
				if (!arr.contains(doc)) {
					solss++;
					System.out.print(".");
					arr.add(doc);
					salida += doc + "\n";
				}
				tiempo_solucion = System.nanoTime() - ini;
			}
			switch(relacion) {
			case 0:
				if((System.nanoTime()-tini) >=time || limit_solution >= solss ) {
				valid = true;
				
			}
				break;
			case 1:
				if((System.nanoTime()-tini) >=time ) {
					valid = true;
					
				}
				break;
			case 2:
				if(limit_solution >= solss ) {
					valid = true;
					
				}
				break;
			case 3:
				if((System.nanoTime()-tini) >=time && limit_solution >= solss ) {
					valid = true;
					
				}
				break;
			}
			}

		}

		/*
		 * System.out.println("vars_extraidas:"+intModel.size()); ParetoOptimizer po =
		 * new ParetoOptimizer(Model.MINIMIZE, totalVars); Solver solver =
		 * model.getSolver(); System.out.println("solver_generado");
		 * solver.showStatistics(); solver.plugMonitor(po); int numero = 0;
		 * System.out.println("pareto_plugeado:"+limit_solution); String salida = "id,";
		 * int solss = 0; for (int j = 0; j < totalModel.length; j++) {
		 * 
		 * //int value = (optim.containsKey(totalModel[j].getName()) &&
		 * optim.get(totalModel[j].getName()).equals("maximize"))?s.getIntVal(totalModel
		 * [j])*-1:s.getIntVal(totalModel[j]); salida+= totalModel[j].getName() + ",";
		 * 
		 * 
		 * } salida += "\n"; while (solver.solve()) {// && numero < limit_solution) {
		 * System.out.print("."); numero++;
		 * 
		 * // retrieve the pareto front List<Solution> paretoFront =
		 * po.getParetoFront(); int index_sol = 0; for (Solution s : paretoFront) {
		 * index_sol++; System.out.print("."); solss++;
		 * salida+="solv:"+numero+"pareto:"+index_sol+","; // salida +=
		 * "PUNTO_DE_DIVISION_SALIDA\n"; for (int j = 0; j < totalModel.length; j++) {
		 * if (!totalModel[j].getName().equals("TOTALIZADOR")) { int value =
		 * (optim.containsKey(totalModel[j].getName()) &&
		 * optim.get(totalModel[j].getName()).equals("maximize")) ?
		 * s.getIntVal(totalModel[j]) * -1 : s.getIntVal(totalModel[j]); salida += value
		 * + ","; // salida += valor + "\n";
		 * 
		 * } } salida += "\n"; //continue; } //salida += "\n"; }
		 */
		
		System.out.println("solver_resuelto");
		double solution = tiempo_solucion; // 1000000000.0;
		String[] x = { salida,"choco_"+way+";"+solss + ";" + elements.size() + ";" + solution  };

		return x;

	}

	public LoadEvaluator_choco() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
