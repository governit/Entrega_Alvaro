package coco;

public class Util {
	public final static int FEATURES = 320;
	public final static int RUNS_NUMBER = 50;
	
	public final static String PATH_AFM2COCO = "C:/Users/Lina8a/Documents/job/asistencia/tesis/pruebas/2-afm2coco-models/" + Util.FEATURES + "/";
	public final static String PATH_COCO_SKELETON = "C:/Users/Lina8a/Documents/job/asistencia/tesis/pruebas/3-skeletons/cocoModel.xmi";
	public final static String PATH_CONFIG_SKELETON = "C:/Users/Lina8a/Documents/job/asistencia/tesis/pruebas/3-skeletons/configConstraints.test7";
	public final static String PATH_RUNS = "C:/Users/Lina8a/Documents/job/asistencia/tesis/pruebas/4-runs/" + Util.FEATURES + "/";
	public final static String PATH_PROJECT_MODELS = "models/fama";
	public final static String PATH_METAMODELOS = "C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\metamodels";
	
	public final static int[][] RUNS = 
			new int[][]{{19},{24},{9},{4},{28},{15},{26},{21},{14},{2},
		{1, 2},{11, 15},{7, 29},{0, 19},{3, 15},{7, 22},{9, 13},{21, 25},{1, 16},{11, 13},
		{2, 8, 17},{3, 9, 12},{6, 10, 14},{7, 13, 24},{1, 15, 25},{5, 21, 26},{4, 12, 28},{2, 20, 25},{6, 15, 23},{13, 24, 29},
		{19, 23, 24, 26},{5, 6, 23, 25},{2, 15, 19, 24},{5, 12, 17, 26},{20, 21, 22, 25},{5, 12, 15, 23},{2, 6, 8, 24},{9, 15, 18, 23},{1, 14, 16, 19},{4, 5, 13, 26},
		{6, 9, 12, 18, 22},{1,  7, 12, 22, 26},{0, 1, 2, 6, 17},{4, 11, 21, 23, 27},{0, 1, 10, 11, 23},{9, 13, 14, 20, 26},{2, 3, 17, 21, 22},{14, 20, 24, 25, 27},{5, 14, 16, 20, 27},{15, 21, 24, 26, 28}};
}
