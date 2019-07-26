package coco.statistics;

import java.io.*; // for File
import java.util.*; // for Scanner

import org.chocosolver.parser.json.JSON;

import coco.modifiers.AddFaMaFMModifier;
import coco.modifiers.IModifier;
import coco.testing.MainTransformedDynamic_v2;
import z3.*;


public class FileCrawler {
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

	public FileCrawler(String directory, int heuristica, int tipo, String ruta_salida, String ruta_sols, int algoritmo,
			int tipo_entrada,int num_sols) throws FileNotFoundException {
		super();
		FileCrawler.ruta_salida = ruta_salida;
		FileCrawler.num_sols = num_sols;
		FileCrawler.algoritmo = algoritmo;
		FileCrawler.ruta_sols = ruta_sols;
		File sol = new File(ruta_sols);
		FileCrawler.ruta_sols = sol.getAbsolutePath();
		FileCrawler.tipo_entrada = tipo_entrada;
		// pw = new PrintWriter(new File(ruta_salida + "/Results.csv"));
		// System.out.println("FM;heuristica;limitado;Solutions;Building;Resolution
		// Time;Nodes;Backtracks;;Fails;Restarts\n");
		// pw.write("FM;time_prods;time_sols;tiem \n");

		System.out.println("done!");
		if (tipo < 0) {
			System.out.println("Parametro 'tipo' invalido. debe ser mayor a 0");
			return;
		}
		if (heuristica != 1 && heuristica != 0 && heuristica != 2 && heuristica != 3) {
			System.out.println("Parametro 'heuristica' invalido (0,1,2,3,4)");
			return;
		}
		FileCrawler.heuristica = heuristica;
		FileCrawler.tipo = tipo;
		System.out.println(directory);
		directory = directory.replace("\\", "___");
		File f = new File(directory);
		System.out.println("_________________________________"+f.toString());
		crawl(f);
		//crawl(f);
		// pw.close();

		// TODO Auto-generated constructor stub
	}

	// Prints the given file/directory and any files/directories inside it,
	// starting with zero indentation.
	// Precondition: f != null and f exists
	public static void crawl(File f) {
		try {
			System.out.println(resultados);

			writer = new BufferedWriter(
					new FileWriter(ruta_salida + "/resultados_" + System.currentTimeMillis() + "_tiempo.csv"));
			crawl(f, "");
			writer.close();
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	static int numero = 0;

	// This recursive "helper" method prints the given file/directory and any
	// files/directories inside it at the given level of indentation.
	// Precondition: f != null and f exists
	private static int crawl(File f, String indent) {
		int salida = 0;
		Transformador tr = new Transformador();
		if (f.isDirectory()) {
			salida = 0;

			// recursive case: directory
			// print everything in the directory
			File[] subFiles = f.listFiles();

			indent += "    ";
			String cmc = "";

			if (tipo_entrada == 3) {
				Extractor_cmc extractor = new Extractor_cmc();
				String arch_destino = extraer_ruta(f.getAbsolutePath());
				String ruta_destino = f.getAbsolutePath();

				salida_fija = "";
				for (int i = 0; i < subFiles.length; i++) {// ITERACION 0 PARA GENERAR EL AFM2COCO
					if (subFiles[i].getName().toLowerCase().contains(".afm")
							&& !subFiles[i].getName().toLowerCase().contains(".afm2coco")
							&& !subFiles[i].getName().toLowerCase().contains(".csv")) {
						IModifier modifier = new AddFaMaFMModifier();
						modifier.modifyFSG("", f.getAbsolutePath() + "/" + subFiles[i].getName(), true);
						salida_fija = f.getAbsolutePath() + "/" + subFiles[i].getName();
						archivo_corrido = subFiles[i].getName();
					}

				}
				for (int i = 0; i < subFiles.length; i++) {// PRIMERA ITERACION PARA ENCONTRAR EL XMI Y EXTRAER LOS CMC
					int sal = crawl(subFiles[i], indent);
					if (sal == 2 && !subFiles[i].getName().contains(arch_destino)
							&& subFiles[i].getName().contains("coco")) {
						String archivo_xmi = subFiles[i].getName();
						try {

							cmc = Extractor_cmc.extractor_cmc(f.getAbsolutePath() + "/" + subFiles[i].getName());

							break;
						} catch (Exception e) {
							System.out.println("error en extractor: continuemos por favor:" + ruta_destino);
						}

					}
				}
				// en este punto ya cuenta con los cmc de la carpeta especificada.
				boolean encontro = false;
				subFiles = f.listFiles();
				String ruta_conversion = ruta_destino + "/" + arch_destino;
				for (int i = 0; i < subFiles.length; i++) {// SEGUNDA ITERACION PARA ENCONTRA LOS AFM2COCO Y
															// CONVERTIRLOS
					int sal = crawl(subFiles[i], indent);
					if (sal == 1) {
						// DESDE ACA DEBE LLAMAR A LA CLASE DEL MAIN
						try {
							Transformador.afm2coco_2_xmi(f.getAbsolutePath(), subFiles[i].getName(), ruta_destino,
									arch_destino);
							encontro = true;
						} catch (Exception e) {
							System.out.println("Archivo corrupto.");
						}

					}

				}
				if (encontro) {
					try {
						Transformador.generador_SolutionC(ruta_destino, arch_destino, ruta_sols);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						System.out.println("error en el dsl... continuemos por favor:" + ruta_conversion);
					}
					try {
						Extractor_cmc.append_cmc(ruta_conversion, cmc);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						System.out.println("error en el append... continuemos por favor:" + ruta_conversion);
					}

					// try {
					String[] arch_destino_props = arch_destino.split("\\.");
					String ruta_pro = "";
					//try {
						ruta_pro = Transformador.coco2properties(ruta_conversion, arch_destino_props[0]);
						System.out.println("R1"+ruta_pro);
						System.out.println("R1"+ruta_conversion);
						System.out.println("R1"+arch_destino_props[0]);
						crawl1(new File(ruta_pro));
					//} catch (Exception e) {
					//	System.out.println("::::" + ruta_conversion + "______" + arch_destino_props[0]);
					//}

				}
				System.out.println("okokokokokokok---"+encontro);
				
			} else {
				if (tipo_entrada == 2) {// z3 lenguaje declarativo y choco
					for (int i = 0; i < subFiles.length; i++) {
						String name = subFiles[i].getName().split("\\.")[0];

						if (algoritmo == 2 || algoritmo == 0) {
							if (subFiles[i].getName().contains("_z3.sol")) {

								long ini = System.nanoTime();

								LoadEvaluator_z3 le = new LoadEvaluator_z3();
								String[] result = {"",""};//le.solveZ3_evaluator(subFiles[i]);
								System.out.println(result[0]);

								long tiempo_solucion = System.nanoTime() - ini;
								resultados += name + ";" + tiempo_solucion + "\n";
							}
						}

						if (algoritmo == 1 || algoritmo == 0) {

							if (subFiles[i].getName().contains("_choco.json")) {
								long ini = System.nanoTime();

								//LoadEvaluator_choco lc = new LoadEvaluator_choco();
								//String[] result = lc.solveCSP(subFiles[i], heuristica, 1, 1);
								//System.out.println(result[0]);

								//long tiempo_solucion = System.nanoTime() - ini;
								//resultados += name + ";" + tiempo_solucion + "\n";
							}
						}

					}
				}
			}
		} else {
			if (f.getName().contains(".afm2coco")) {
				salida = 1;
			}
			if (f.getName().contains(".xmi")) {
				salida = 2;
			}
		}
		try {
			writer.write(resultados);
			writer.flush();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return salida;
	}

	public static HashMap extraer_info(String ruta_conversion) {
		HashMap map = new HashMap();
		String[] doc = ruta_conversion.split("/");
		String pparte = "";
		String sparte = "";
		String[] rutaaaa = doc[0].split("[^a-z0-9]");
		String pnum = "", snum = "";
		for (int w = 0; w < rutaaaa.length; w++) {
			if (w == rutaaaa.length - 2)
				pnum = rutaaaa[w];
			if (w == rutaaaa.length - 1)
				snum = rutaaaa[w];

		}
		for (int w = 0; w < doc.length; w++) {

			if (w < doc.length - 1) {
				pparte += doc[w] + "/";
			} else {
				sparte += doc[w];
			}
		}
		map.put("pparte", pparte);
		map.put("sparte", sparte);
		String[] nomarch = sparte.split("\\.");
		numero++;
		String archivo = numero + "_" + pnum + "_" + snum + "_" + nomarch[0];
		map.put("archivo", archivo);
		return map;

	}

	public static String extraer_ruta(String ruta) {
		String[] doc = ruta.split("/");

		String[] rutaaaa = doc[0].split("[^a-z0-9]");
		String pnum = "", snum = "";
		for (int w = 0; w < rutaaaa.length; w++) {
			if (w == rutaaaa.length - 2)
				pnum = rutaaaa[w];
			if (w == rutaaaa.length - 1)
				snum = rutaaaa[w];

		}
		return "cocoModel_" + pnum + "_" + snum + ".xmi";
	}

	public static void crawl1(File f) {
		if (f.isDirectory()) {

			// recursive case: directory
			// print everything in the directory
			File[] subFiles = f.listFiles();
			for (int j = 3; j < 4; j++) {

				for (int i = 0; i < subFiles.length; i++) {
					String ruta_pro = subFiles[i].getAbsolutePath();
					String name = subFiles[i].getName().split("\\.")[0];

					// System.out.println(name);
					try {
						if (algoritmo == 1 || algoritmo == 0) {// choco
							// System.out.println("punto inicial1");
							long ini = System.nanoTime();
							MainTransformedDynamic_v2 mt = new MainTransformedDynamic_v2(ruta_pro, tipo);
							try {
								JSON.write(mt.getModel(), new File(ruta_salida + "/" + name + "_choco.json"));

								// System.out.println("punto inicial5");
							} catch (Exception e) {
								e.printStackTrace();
							}
							String[] salida_stats = mt.solveCSP(heuristica, num_sols);
							//System.out.println(salida_stats[0]);

							long tiempo_solucion = System.nanoTime() - ini;
							resultados += name + ";" + tiempo_solucion + "\n";
							

						}
						if (algoritmo == 2 || algoritmo == 0) {// z3
							// System.out.println("punto inicial6");
							long ini = System.nanoTime();

							MainTransformedDynamicZ3 mt1 = new MainTransformedDynamicZ3(ruta_pro, tipo, j);
							// System.out.println("punto inicial7");
							String[] salida_stats1 = mt1.solvez3(1);
							long tiempo_solucion = System.nanoTime() - ini;
							resultados += name + ";" + tiempo_solucion + "\n";
							try {

								BufferedWriter writer = new BufferedWriter(
										new FileWriter(ruta_salida + "/" + name + "_z3.sol"));
								// System.out.println("punto inicial9");
								writer.write(salida_stats1[1]);

								writer.close();
								// System.out.println("punto inicial10");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						// Comparer c = new Comparer();
						// c.Prepare(salida_stats1[0],salida_stats[0]);
					} catch (Exception e) {

						System.out.println("error en solver o impresiones");
						e.printStackTrace();
						// ruta_conversion);
					}
				}
			}
		} else {
			String ruta_pro = f.getAbsolutePath();
			String name = f.getName().split("\\.")[0];
			String products_choco="", products_z3="";
			// System.out.println(name);
			try {
				
				if (algoritmo == 1 || algoritmo == 0) {// choco
					// System.out.println("punto inicial1");
					MainTransformedDynamic_v2 mt0 = new MainTransformedDynamic_v2(ruta_pro, tipo);
					// System.out.println("PTOINICIAL1::_"+System.nanoTime());
					try {
						//System.out.println("_________________ll______"+mt0.getModel().toString());
						JSON.write(mt0.getModel(), new File(ruta_salida + "/" + name + "_choco.json"));
						
						// System.out.println("punto inicial5");
					} catch (Exception e) {
						e.printStackTrace();
					}
					long ini = System.nanoTime();
					
					String[] salida_stats = mt0.solveCSP(heuristica,  num_sols);
					//System.out.println(salida_stats[0]);
					products_choco = salida_stats[0];
					try {
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(ruta_salida + "/" + name + "SOLS_choco.txt"));
						writer.write(salida_stats[0]);

						writer.close();
					}catch(Exception e) {
						
					}
					long tiempo_solucion = System.nanoTime() - ini;
					resultados += name + ";" + tiempo_solucion + "\n";
					

				}
				if (algoritmo == 2 || algoritmo == 0) {// z3
					// System.out.println("punto inicial6");
					MainTransformedDynamicZ3 mt1 = new MainTransformedDynamicZ3(ruta_pro, tipo, 1);
					// System.out.println("punto inicial7");
					long ini = System.nanoTime();

					String[] salida_stats1 = mt1.solvez3(1);
					
					products_z3 = salida_stats1[0];
					//System.out.println(salida_stats1[0]);
					long tiempo_solucion = System.nanoTime() - ini;
					resultados += name + ";" + tiempo_solucion + "\n";
					try {

						BufferedWriter writer = new BufferedWriter(
								new FileWriter(ruta_salida + "/" + name + "_z3.sol"));
						writer.write(salida_stats1[1]);

						writer.close();
						writer = new BufferedWriter(
								new FileWriter(ruta_salida + "/" + name + "SOLS_z3.txt"));
						writer.write(salida_stats1[0]);

						writer.close();
						// System.out.println("punto inicial10");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//System.out.println(products_z3+"-"+products_choco);
				//Comparer c = new Comparer();
				//c.Prepare(products_z3,products_choco);
			} catch (Exception e) {

				System.out.println("error en solver o impresiones");
				e.printStackTrace();
				// ruta_conversion);
			}
		}
	}
}
