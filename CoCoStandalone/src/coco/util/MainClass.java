package coco.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import coco.statistics.FileCrawler;
import coco.statistics.FileCrawler_Tr2;

public class MainClass {
	public static void main(String e[]) {
		System.gc();
		MainClass x = new MainClass();

	}

	public MainClass() {
 
		Properties prop = new Properties();
		InputStream input = null;

		try {
			String ruta_config = "./transformations/config.properties";
			input = new FileInputStream(ruta_config);

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			String ruta = prop.getProperty("ruta");
			String ruta_salida = prop.getProperty("ruta_salida");
			int algoritmo = Integer.parseInt(prop.getProperty("algoritmo"));
			String heu = "1";//prop.getProperty("heuristica");
			String ruta_sols = prop.getProperty("ruta_solution_constraints");
			int limitado = 0;//Integer.parseInt(prop.getProperty("limitado"));
			int tipo_entrada = Integer.parseInt(prop.getProperty("tipo_entrada"));
			int num_sols = Integer.parseInt(prop.getProperty("num_sols"));
			String experiment = prop.getProperty("experiment").trim();
			int tiempo = Integer.parseInt(prop.getProperty("tiempo"));
			int relacion_tiempo_num_sols = Integer.parseInt(prop.getProperty("relacion_tiempo_num_sols"));
			

			int heuristica = 0;
			switch (heu) {
			case "1":
				heuristica = Util.HEURISTIC_CD;
				break;
			case "2": 
				heuristica = Util.HEURISTIC_CrossC;
				break;
			case "3":
				heuristica = Util.HEURISTIC_20C;
				break;
			default:
				heuristica = Util.HEURISTIC_WD;

			}
			int []sols = null;
			boolean fix = false;
			String [] dirs1 = null;
			String [] dirs2 = null;
			double [] exp2 = null;
			switch(experiment) {
			case "1":
				sols = new int[1];
				sols[0] = 2;
				fix = false;
				String [] dirs1_tmp = {"40","80","160","320","640","1280"};
				dirs1 = dirs1_tmp;
				 String [] dirs2_tmp = {"0","1","2","10","11","12","20","21","22","30","31","32","40","41","42"};
				 dirs2 = dirs2_tmp;
				 double [] exp2_tmp = {0,05};
				 exp2 = exp2_tmp;
				break;
			case "2":
				sols = new int[1];
				sols[0] = 2;
				fix = false;
				String [] dirs1_tmp1 = {"40"};
				dirs1 = dirs1_tmp1;
				 String [] dirs2_tmp1 = {"10","11","12","20","21","22","30","31","32","40","41","42"};
				 dirs2 = dirs2_tmp1;
				 double [] exp2_tmp1 = {0.05,0.1,0.15,0.2,0.25};
				 exp2 = exp2_tmp1;
				 break;
			case "3":
				int [] sols_tmp = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
				sols = sols_tmp;
				fix = false;
				String [] dirs1_tmp2 = {"40"};
				dirs1 = dirs1_tmp2;
				 String [] dirs2_tmp2 = {"0","1","2","10","11","12","20","21","22","30","31","32","40","41","42"};
				 dirs2 = dirs2_tmp2;
				 double [] exp2_tmp2 = {0.05};
				 exp2 = exp2_tmp2;
				 break;
			case "4":
				int [] sols_tmp1 = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
				sols = sols_tmp1;
				fix = true;
				String [] dirs1_tmp3 = {"40"};
				dirs1 = dirs1_tmp3;
				 String [] dirs2_tmp3 = {"0","1","2","10","11","12","20","21","22","30","31","32","40","41","42"};
				 dirs2 = dirs2_tmp3;
				 double [] exp2_tmp3 = {0.05};
				 exp2 = exp2_tmp3;
				 break;
			}

			
			if(tipo_entrada == 2) {
			for(String i : dirs1) {
				for(String j : dirs2) {
					for (double perc: exp2) {
					String nwruta = ruta+"/"+i+"/"+j;
					System.out.println(nwruta);
					System.gc();
					for(int scs : sols) {
					FileCrawler_Tr2 x = new FileCrawler_Tr2(nwruta, heuristica, limitado, ruta_salida, ruta_sols, algoritmo,tipo_entrada,num_sols,perc,scs,fix,relacion_tiempo_num_sols,tiempo);
					}
					}
				}	
			}
			}else if(tipo_entrada == 1) {
			FileCrawler_Tr2 x = new FileCrawler_Tr2(ruta, heuristica, limitado, ruta_salida, ruta_sols, algoritmo,tipo_entrada,num_sols,0,0,fix,relacion_tiempo_num_sols,tiempo);
			}/*else if(tipo_entrada == 3) {
				FileCrawler x = new FileCrawler(ruta, heuristica, limitado, ruta_salida, ruta_sols, algoritmo,tipo_entrada,num_sols);
				
			}*/

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
