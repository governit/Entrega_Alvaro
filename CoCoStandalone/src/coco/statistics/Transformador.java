package coco.statistics;

import coco.util.AntExecutor;
import coco.util.XtextModelManager;

public class Transformador {
	int dato = 0;

	public Transformador() {
		super();
	}
public static String splot_2_xmi(String ruta, String archivo) {
		
	XtextModelManager xtextManager = new XtextModelManager(ruta+"\\"+archivo);
	xtextManager.loadXtextModelAsEcoresplot2CoCo();
		String[][] properties1 = new String[2][2];
		properties1[0][0] = "splotModel";
		properties1[0][1] = ruta+"\\"+archivo;
		properties1[1][0] = "cocoModel";
		String[] new_name = archivo.split("\\.");
		properties1[1][1] = ruta+"/"+new_name[0]+".xmi";
		AntExecutor antExecutor1 = new AntExecutor("workflow/build-splot2cocoM2M.xml", properties1);
		return ruta+"/"+new_name[0]+".xmi";
		
	}
public static String afm2coco_2_xmi(String ruta, String archivo,String ruta_salida, String archivo_salida) {

	XtextModelManager xtextManager = new XtextModelManager(ruta+"\\"+archivo);

	xtextManager.loadXtextModelAsEcoreAfm2CoCo();

	String[][] properties1 = new String[2][2];
	properties1[0][0] = "afm2cocoModel";
	properties1[0][1] = ruta+"\\"+archivo;
	properties1[1][0] = "cocoModel";
	String[] new_name = archivo.split("\\.");
	properties1[1][1] = ruta_salida+"/"+archivo_salida;

	AntExecutor antExecutor1 = new AntExecutor("workflow/build-fama2cocoM2M.xml", properties1);
	return properties1[1][1];
	
}
	public static String afm2coco_2_xmi(String ruta, String archivo) {
		
		XtextModelManager xtextManager = new XtextModelManager(ruta+"\\"+archivo);
		xtextManager.loadXtextModelAsEcoreAfm2CoCo();
		String[][] properties1 = new String[2][2];
		properties1[0][0] = "afm2cocoModel";
		properties1[0][1] = ruta+"\\"+archivo;
		properties1[1][0] = "cocoModel";
		String[] new_name = archivo.split("\\.");
		properties1[1][1] = ruta+"/"+new_name[0]+".xmi";
		AntExecutor antExecutor1 = new AntExecutor("workflow/build-fama2cocoM2M.xml", properties1);
		return ruta+"/"+new_name[0]+".xmi";
		
	}
	public static void generador_SolutionC(String ruta, String archivo, String ruta_sols) {
		//String ruta_metamodelo = Util.PATH_METAMODELOS + "/configConstraints.test7";
		String ruta_metamodelo = ruta_sols;
		XtextModelManager xtextManager = new XtextModelManager(ruta_metamodelo);
		xtextManager.loadXtextModelAsEcoreCoCo();
		String[][] properties = new String[2][2];
		properties[0][0] = "cocoModel";
		properties[0][1] = ruta+"\\"+archivo;
		properties[1][0] = "cocoDSLModel";
		properties[1][1] = ruta_metamodelo;
		AntExecutor antExecutor = new AntExecutor("workflow/fama2coco/build-cocoDSL2cocoM2M.xml", properties);
		
	}
	public static String coco2properties(String ruta , String archivo) {
		String[][] properties = new String[2][2];
		properties[0][0] = "cocoModel";
		properties[0][1] = ruta;
		properties[1][0] = "cocoCP";
		properties[1][1] = archivo+".properties";
		if(true) {//entra aca cuando tiene atributos
		AntExecutor antExecutor = new AntExecutor("workflow/build-coco2propertiesM2T.xml", properties);
		}else {
			
//			AntExecutor antExecutor = new AntExecutor("workflow/build-coco2propertiesM2T2_old2.xml", properties);
			AntExecutor antExecutor = new AntExecutor("workflow/build-coco2propertiesM2T2_old3.xml", properties);
			
		}
		return "workflow/"+archivo+".properties";
	}


}
