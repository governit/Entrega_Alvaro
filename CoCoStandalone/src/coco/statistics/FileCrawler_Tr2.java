package coco.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import coco.testing.LoadEvaluator_choco;
import coco.testing.Validator_FaMa;
import coco.testing.transformer_afm_choco;
import coco.testing.transformer_afm_z3;//COMENTARIO_Z3
import coco.util.Comparer;
import z3.LoadEvaluator_z3;//COMENTARIO_Z3

public class FileCrawler_Tr2 {
	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		System.out.print("Directory to crawl? ");
		String directoryName = console.nextLine();

		File f = new File(directoryName);
		crawl(f);
	}

	static int heuristica;
	static int tipo;
	// static PrintWriter pw;
	static String archivo_corrido = "";
	static String salida_fija = "";
	static String ruta_sols = "";
	static String ruta_salida = "";
	static int num_sols = 1;
	static int algoritmo = 0;
	static int tipo_entrada = 0;
	static String resultados = "";
	static BufferedWriter writer;
	static int scs = 2;
	static boolean fix ;
	static int tiempo;
	static int relacion_tiempo_num_sols;
static double perc;
	public FileCrawler_Tr2(String directory, int heuristica, int tipo, String ruta_salida, String ruta_sols,
			int algoritmo, int tipo_entrada, int num_sols,double perc,int scs,boolean fix,int relacion_tiempo_num_sols,int tiempo) throws FileNotFoundException {
		super();
		FileCrawler_Tr2.fix = fix;
		FileCrawler_Tr2.scs = scs;
		FileCrawler_Tr2.perc = perc;
		FileCrawler_Tr2.ruta_salida = ruta_salida;
		FileCrawler_Tr2.num_sols = num_sols;
		FileCrawler_Tr2.algoritmo = algoritmo;
		FileCrawler_Tr2.ruta_sols = ruta_sols;
		File sol = new File(ruta_sols);
		FileCrawler_Tr2.ruta_sols = sol.getAbsolutePath();
		FileCrawler_Tr2.tipo_entrada = tipo_entrada;
		this.tiempo=tiempo;
		this.relacion_tiempo_num_sols = relacion_tiempo_num_sols;

		System.out.println("done!");
		if (tipo < 0) {
			System.out.println("Parametro 'tipo' invalido. debe ser mayor a 0");
			return;
		}
		if (heuristica != 1 && heuristica != 0 && heuristica != 2 && heuristica != 3) {
			System.out.println("Parametro 'heuristica' invalido (0,1,2,3,4)");
			return;
		}
		FileCrawler_Tr2.heuristica = heuristica;
		FileCrawler_Tr2.tipo = tipo;
		System.out.println(directory);
		directory = directory.replace("\\", "___");
		File f = new File(directory);

		crawl(f);
		// crawl(f);
		// pw.close();

		// TODO Auto-generated constructor stub
	}

	private static int crawl(File f) {
		int salida = 0;
		System.out.println("control1");
		transformer_afm_choco transformer_choco = new transformer_afm_choco();
		System.out.println("control2");
		transformer_afm_z3 transformer_z3 = new transformer_afm_z3();////COMENTARIO_Z3
		Validator_FaMa vf = new Validator_FaMa();
		if (f.isDirectory()) {
			salida = 0;
			String ruta_afm = "";
			String ruta_coco = "";

			File[] subFiles = f.listFiles();

			String name = "";
			if (tipo_entrada == 2) {
				ruta_coco = ruta_sols;

			}
			String ruta_json = "";
			String ruta_alterna = "";

			for (int i = 0; i < subFiles.length; i++) {

				if (subFiles[i].getName().toLowerCase().contains(".afm2coco") ) {//&& !subFiles[i].getName().toLowerCase().contains("2coco")) {
					ruta_afm = f.getAbsolutePath() + "/" + subFiles[i].getName();
					
					//boolean valido = vf.Validate(ruta_afm);
					
					
					//if(!valido) {
					//	System.out.println("MODELO NO VALIDO");
					//	return -1000;
					//}
					if (tipo_entrada == 1) {
						name = subFiles[i].getName().split("\\.")[0];
					} else {
						String archivo = subFiles[i].getName().split("\\.")[0];
						String[] ruta = f.getAbsolutePath().split("\\\\");
						int tam = ruta.length;
						name = scs+"_"+ ruta[tam - 2] + "_" + ruta[tam - 1] + "_" + archivo;
					}
					
					ruta_json = (algoritmo == 1 || algoritmo == 0) ? ruta_salida + "/" + name + "LANG_choco.json"
							: (algoritmo == 2 ) ? ruta_salida + "/" + name + "LANG_z3.txt" : "";
					if( algoritmo == 0) {
						ruta_alterna = ruta_salida + "/" + name + "LANG_z3.txt" ;
					}
					try {
						if (algoritmo == 1 || algoritmo == 0) {// choco

							transformer_choco.Generate_Model(ruta_afm, name);

						}
						if (algoritmo == 2 || algoritmo == 0) {
							transformer_z3.Generate_Model(ruta_afm, name);//COMENTARIO_Z3
						}
					} catch (Exception e) {

						System.out.println("error en solver o impresiones");
						e.printStackTrace();
						// ruta_conversion);
					}
				}
				if (tipo_entrada == 1) {
					if (subFiles[i].getName().toLowerCase().contains(".test7")) {
						ruta_coco = f.getAbsolutePath() + "/" + subFiles[i].getName();
					}
				}
			}
			try {
				if (algoritmo == 1 || algoritmo == 0) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(ruta_json));
					writer.write(transformer_afm_choco.getModelData(ruta_coco, tipo_entrada,perc,scs,fix));
					writer.close();
				}
				if (algoritmo == 2 || algoritmo == 0) {
					BufferedWriter writer = new BufferedWriter(new FileWriter(ruta_alterna));
					writer.write(transformer_afm_z3.getModelData(ruta_coco, tipo_entrada,perc,scs,fix));//COMENTARIO_Z3
					writer.close();
				}

				
			} catch (Exception e) {
				e.printStackTrace();
			}
			long ini = System.nanoTime();
			String resultados = "";
			String salidachoco1="";
			String salidachoco2="";
			String salidaz3="";
			if (algoritmo == 1 || algoritmo == 0) {
				/*
				LoadEvaluator_choco eva = new LoadEvaluator_choco();

				String[] salida_stats = eva.LoadEvaluator(ruta_json, num_sols,true,vf.getElements());
				resultados += salida_stats[1]+"\n";
				salidachoco1 = salida_stats[0];
				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(ruta_salida + "/" + name + "SOLS_choco.csv"));
					writer.write(salida_stats[0]);
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				*/
				LoadEvaluator_choco eva2 = new LoadEvaluator_choco();
				
				String[] salida_stats2 = eva2.LoadEvaluator(ruta_json, num_sols,true,vf.getElements(),relacion_tiempo_num_sols,tiempo);
				resultados += name+";"+salida_stats2[1]+"\n";
				salidachoco2 = salida_stats2[0];
				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(ruta_salida + "/" + name + "SOLS_choco2.csv"));
					writer.write(salida_stats2[0]);
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			if (algoritmo == 2 || algoritmo == 0) {
				//COMENTARIO_Z3
				LoadEvaluator_z3 eva = new LoadEvaluator_z3();

				String[] salida_stats = LoadEvaluator_z3.LoadEvaluator(ruta_alterna, num_sols,vf.getElements(),relacion_tiempo_num_sols,tiempo);
				resultados += name+";"+salida_stats[1]+"\n";
				salidaz3 = salida_stats[0];
				try {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(ruta_salida + "/" + name + "SOLS_z3.csv"));
					writer.write(salida_stats[0]);
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//Comparer comparer = new Comparer();
			//comparer.EvaluateSim(salidachoco1,salidachoco2);
			//comparer.EvaluateSim(salidachoco1,salidaz3);
			long tiempo_solucion = System.nanoTime() - ini;
			String perc1 = (perc*100)+"";
			
			//resultados += perc1+"_" +name + ";" + tiempo_solucion + "\n"; //EXPERIMENTO 2222
			//resultados += perc1+"_" + name + ";" + tiempo_solucion + "\n"; //EXPERIMENTO 2222
			//resultados = "name;solver;num_sols;num_feats;tiempo\n"+resultados; 
			//resultados += name + ";" + tiempo_solucion + "\n";
			try {
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(ruta_salida + "/resultados_tiempo.csv", true));
				writer.write(resultados);
				writer.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return salida;
	}
}
