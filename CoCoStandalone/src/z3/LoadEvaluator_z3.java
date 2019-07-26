package z3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.*;

public class LoadEvaluator_z3 {
	private static Optimize solver;// Solver solver;
	private static Optimize opt;

	public static void main(String[] a) throws IOException {

	}

	public static String[] LoadEvaluator(String url, int num_sols, ArrayList<String> elements, int relacion,
			int tiempo) {
		File f = new File(url);
		String salida[] = null;
		try {
			BufferedReader r = new BufferedReader(new FileReader(f));
			String line;
			String tot = "";
			while ((line = r.readLine()) != null) {
				tot += line;
			}
			salida = solveCSP(tot, num_sols, elements, relacion, tiempo);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return salida;

	}

	public static String[] solveCSP(String bench,int limit_solution,ArrayList<String>elements,int relacion, int tiempo) {
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		Context ctx = new Context(cfg);
		String[ ] blocks = bench.split("\\(");
		BoolExpr[] expr = ctx.parseSMTLIB2String(bench, null, null, null, null);
		String salida = "";
		System.out.println("leyendo_modelo_z");
		solver = ctx.mkOptimize();// ctx.mkSolver();
		solver.Add(expr);
		for(int i = 0;i< blocks.length;i++) {
			if(blocks[i].contains("minimize")) {
				String name = blocks[i].split(" ")[1];
				name = name.substring(0, name.length()-1);
				solver.MkMinimize(ctx.mkIntConst(name));

			}
			if(blocks[i].contains("maximize")) {
				String name = blocks[i].split(" ")[1];
				name = name.substring(0, name.length()-1);
				solver.MkMaximize(ctx.mkIntConst(name));
				
			}
			
		}
		Params p = ctx.mkParams();
		p.add("priority", "pareto");
		solver.setParameters(p);
		String[] salidas = { "", "" };
		String header= "";
		boolean valid = false;
		int solutions = 0;
		try {
			long ini = System.nanoTime();

			long tiempo_solucion = 0;
			
			int components = 0;
			long time = (tiempo)*1000000000L;
			long tini = System.nanoTime(); 
			ArrayList arr = new ArrayList();
			System.out.println("modelo_leido_z+"+limit_solution);
			while (Status.SATISFIABLE == solver.Check() && !valid) {
				//&& (((System.nanoTime()-tini)<time && limit_solution > 1) || (limit_solution == 1 && solutions < 1))) {

				
				//System.out.print("-");
				if (solutions == 0) {
					tiempo_solucion = System.nanoTime() - ini;
					ini = System.nanoTime();
				}
				

				
				Model model = solver.getModel();

				FuncDecl[] x = model.getConstDecls();
				String doc = "";
				for (int i = 0; i < elements.size(); i++) {
					Expr e = (Expr)ctx.mkIntConst(elements.get(i));
					Expr v = model.evaluate(e, false);
					String valor = (v.toString().equals("0") || v.toString().equals("1"))?v.toString():"0";
					if (solutions == 0) {
						
						header+=elements.get(i)+";"; 
					}
					doc +=  valor + ";";	
					
				}
				if(!arr.contains(doc)) {
					solutions++;
					System.out.print(".");
					arr.add(doc);
					salida+=doc+"\n";
				}
				switch(relacion) {
				case 0:
					if((System.nanoTime()-tini) >=time || limit_solution >= solutions ) {
					valid = true;
					
				}
					break;
				case 1:
					if((System.nanoTime()-tini) >=time ) {
						valid = true;
						
					}
					break;
				case 2:
					if(limit_solution >= solutions ) {
						valid = true;
						
					}
					break;
				case 3:
					if((System.nanoTime()-tini) >=time && limit_solution >= solutions ) {
						valid = true;
						
					}
					break;
				}
				
			}
			System.out.print("solver_resueltoz3");
			salida = header+"\n"+ salida;

			if (solutions == 0) {
				System.out.println("Modelo no es satisfiable");
			}

			long tiempo_productos = System.nanoTime() - ini;
			salidas[0] = salida;
			double analize = tiempo_productos; // 1000000000.0;
			double solution = tiempo_solucion; // 1000000000.0;
			salidas[1] = "z3;"+solutions + ";" + elements.size() + ";" + solution;
		}catch(

	Exception e)
	{
			System.out.println("error");
			e.printStackTrace();
		}return salidas;
}}
