package coco;

import java.io.File;

import coco.generators.CSPGenerator;
import coco.generators.IGenerator;
import coco.modifiers.AddFaMaFMModifier;
import coco.modifiers.IModifier;
import coco.util.AntExecutor;
import coco.util.FileManager;
import coco.util.Util;
import coco.util.XtextModelManager;

public class Main {
	public static void main(String args[]) throws Exception {
		int value = 6;

		switch (value) {
		case 0:
			testChangingModels();
			break;
		case 1:
			setConfigurationConstraints();
			break;
		case 2:
			generateChocoModel();
			break;
		case 3: 
			generateChocoModelLib();
			break;
		case 4:
			verifyFileExistence();
			break;
		case 5:
			testChangingModels_v2();
			break;
		case 6:
			//generateafm2cocofile("C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\splot/DRUPALv3.afm");
			//generateafm2cocofile("C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\splot/FeatureModel_Decisional_SPLOT.afm");
			generateafm2cocofile("C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\modelosGenerados\\experimento1\\40\\famaValidation0.afm");

			
		}

	}
	private static void generateafm2cocofile(String ruta) {
		String famaPath = ruta;
		//"C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\fama/ACVFinal1.afm";
		IModifier modifier = new AddFaMaFMModifier();
		modifier.modifyFSG("", famaPath, true);
	}
	private static void verifyFileExistence() {  
		String xmiPath = "models/test.xmi";
		String famaPath = "models/test.afm";
		File file = new File(
				"C:\\Users\\Asistente\\Documents\\InvestIT_PL\\modelosBase\\FeatureModel_Decisional_FAMA.afm");
		File famaFile = new File(famaPath);

		FileManager manager = new FileManager();
		manager.copyFile(file, famaFile);

		IModifier modifier = new AddFaMaFMModifier();
		modifier.modifyFSG(xmiPath, famaPath, true);
	}

	private static void generateChocoModelLib() {
		IGenerator generator = new CSPGenerator();
		generator.generateConfigurationProgram("models/test.xmi", "models/Test.java");
	}

	public static void generateChocoModel() {
		for (int i = 10; i < Util.RUNS_NUMBER; i++) {
			FileManager fileManager = new FileManager();

			File cocoRuns = new File(Util.PATH_RUNS + i + "/coco" + i + ".xmi");
			File cocoProject = new File(Util.PATH_PROJECT_MODELS + "/coco" + i + ".xmi");
			File chocoRuns = new File(Util.PATH_RUNS + i + "/CoCoModelDefault" + i + ".java");
			File chocoProject = new File(Util.PATH_PROJECT_MODELS + "/CoCoModelDefault" + i + ".java");
			fileManager.copyFile(cocoRuns, cocoProject);

			// Workflow responsible of CMC random generation
			String[][] properties = new String[2][2];
			properties[0][0] = "cocoModel";
			properties[0][1] = "coco" + i + ".xmi";
			properties[1][0] = "cocoCP";
			properties[1][1] = "CoCoModelDefault" + i + ".java";
			AntExecutor antExecutor = new AntExecutor("workflow/fama2coco/build-coco2chocoM2T.xml", properties);
			System.out.println(chocoRuns.getAbsolutePath());
			System.out.println(chocoProject.getAbsolutePath());
			fileManager.copyFile(chocoProject, chocoRuns);
		}
	}

	public static void setConfigurationConstraints() {
		for (int i = 0; i < Util.RUNS_NUMBER; i++) {
			FileManager fileManager = new FileManager();

			File configSkeleton = new File(Util.PATH_CONFIG_SKELETON);
			File configProject = new File(Util.PATH_PROJECT_MODELS + "/configConstraints.test7");
			File cocoRuns = new File(Util.PATH_RUNS + i + "/coco" + i + ".xmi");
			File cocoProject = new File(Util.PATH_PROJECT_MODELS + "/coco" + i + ".xmi");

			fileManager.copyFile(configSkeleton, configProject);
			fileManager.copyFile(cocoRuns, cocoProject);

			// Loading Xtext models as Ecore models
			XtextModelManager xtextManager = new XtextModelManager(
					Util.PATH_PROJECT_MODELS + "/configConstraints.test7");
			xtextManager.loadXtextModelAsEcoreCoCo();

			// Workflow responsible of parsing configuration constraints
			String[][] properties = new String[2][2];
			properties[0][0] = "cocoModel";
			properties[0][1] = "coco" + i + ".xmi";
			properties[1][0] = "cocoDSLModel";
			properties[1][1] = "configConstraints.test7";
			AntExecutor antExecutor = new AntExecutor("workflow/fama2coco/build-cocoDSL2cocoM2M.xml", properties);

			fileManager.copyFile(cocoProject, cocoRuns);
		}

	}

	public static void testChangingModels_v2() {

		System.out.println("punto ctrl3");
		XtextModelManager xtextManager = new XtextModelManager("C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\fama\\FeatureModel17.afm2coco");
		xtextManager.loadXtextModelAsEcoreAfm2CoCo();
		System.out.println("punto ctrl4");
		String[][] properties1 = new String[2][2];
		properties1[0][0] = "afm2cocoModel";
		properties1[0][1] = "C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\fama\\FeatureModel17.afm2coco";
		properties1[1][0] = "cocoModel";
		properties1[1][1] = "C:\\Users\\Asistente\\Documents\\InvestIT_SPL\\CoCoStandalone\\models\\fama\\CoCo123123.xmi";
		AntExecutor antExecutor1 = new AntExecutor("workflow/build-fama2cocoM2M.xml", properties1);
		System.out.println("punto ctrl5");

	}

	public static void testChangingModels() {
		try {
			FileManager fileManager = new FileManager();

			for (int i = 0; i < Util.RUNS_NUMBER; i++) {
				System.out.println("I: " + i);
				int[] run = selectRandomModel(i);
				// int[] run = Util.RUNS[i];

				File cocoSkeleton = new File(Util.PATH_COCO_SKELETON);
				File cocoRuns = new File(Util.PATH_RUNS + i + "/coco" + i + ".xmi");
				File cocoProject = new File(Util.PATH_PROJECT_MODELS + "/coco" + i + ".xmi");
				File famaValidationProject = new File(Util.PATH_PROJECT_MODELS + "/famaValidation" + i + ".afm");
				File famaValidationRuns = new File(Util.PATH_RUNS + i + "/famaValidation" + i + ".afm");

				fileManager.copyFile(cocoSkeleton, cocoProject);

				for (int j = 0; j < run.length; j++) {
					// Move input models to project
					File afm2cocoModel = new File(Util.PATH_AFM2COCO + "FeatureModel" + run[j] + ".afm2coco");
					File afm2cocoRuns = new File(Util.PATH_RUNS + i + "/FeatureModel" + run[j] + ".afm2coco");
					File afm2cocoProject = new File(Util.PATH_PROJECT_MODELS + "/FeatureModel" + run[j] + ".afm2coco");
					fileManager.copyFile(afm2cocoModel, afm2cocoProject);

					Thread.sleep(10000);

					// Loading Xtext models as Ecore models
					XtextModelManager xtextManager = new XtextModelManager(
							"models/fama/FeatureModel" + run[j] + ".afm2coco");
					xtextManager.loadXtextModelAsEcoreAfm2CoCo();

					// Workflow responsible of translating FaMa models
					// to CoCo models.
					String[][] properties1 = new String[2][2];
					properties1[0][0] = "afm2cocoModel";
					properties1[0][1] = "FeatureModel" + run[j] + ".afm2coco";
					properties1[1][0] = "cocoModel";
					properties1[1][1] = "coco" + i + ".xmi";
					AntExecutor antExecutor1 = new AntExecutor("workflow/build-fama2cocoM2M.xml", properties1);

					fileManager.copyFile(afm2cocoProject, afm2cocoRuns);
				}

				// Workflow responsible of CMC random generation
				String[][] properties2 = new String[2][2];
				properties2[0][0] = "cocoModel";
				properties2[0][1] = "coco" + i + ".xmi";
				properties2[1][0] = "famaValidationModel";
				properties2[1][1] = "famaValidation" + i + ".afm";
				AntExecutor antExecutor2 = new AntExecutor("workflow/build-cmcGenerationM2M.xml", properties2);

				Thread.sleep(5000);

				// FAMAValidator famaValidator = new FAMAValidator();
				// System.out.println(famaValidator.validateAfmFM(Util.PATH_PROJECT_MODELS +
				// "/famaValidation" + i + ".afm"));

				/*
				 * System.out.print("Modelos: "); for(int j = 0; j < run.length; j++){
				 * System.out.print(run[j] + " "); }
				 */

				fileManager.copyFile(famaValidationProject, famaValidationRuns);
				fileManager.copyFile(cocoProject, cocoRuns);
				// cocoProject.delete();
				famaValidationProject.delete();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Depending on an integer index that denotes the experiment run number, an
	 * array of integers is created with the index of one of the 30 generated random
	 * models. If i >= 0 and i < 10then array.length == 1. Else if i >= 10 and i <
	 * 20 then array.length == 1. Else if i >= 20 and i < 30 then array.length == 1.
	 * Else if i >= 30 and i < 40 then array.length == 1. Else i >= 40 and i <
	 * 50then array.length == 1.
	 * 
	 * @param i:
	 *            integer value with the generated model index.
	 * @return run: array of integers with the models' indexes.
	 */
	private static int[] selectRandomModel(int i) {
		int[] run;

		if (i >= 0 && i < 10) {
			int v1 = (int) (Math.random() * 50.0);
			run = new int[] { v1 };
		} else if (i >= 10 && i < 20) {
			int v1 = (int) (Math.random() * 30);
			int v2 = (int) (Math.random() * 30);
			run = new int[] { v1, v2 };
		} else if (i >= 20 && i < 30) {
			int v1 = (int) (Math.random() * 30);
			int v2 = (int) (Math.random() * 30);
			int v3 = (int) (Math.random() * 30);
			run = new int[] { v1, v2, v3 };
		} else if (i >= 30 && i < 40) {
			int v1 = (int) (Math.random() * 30);
			int v2 = (int) (Math.random() * 30);
			int v3 = (int) (Math.random() * 30);
			int v4 = (int) (Math.random() * 30);
			run = new int[] { v1, v2, v3, v4 };
		} else {
			int v1 = (int) (Math.random() * 30);
			int v2 = (int) (Math.random() * 30);
			int v3 = (int) (Math.random() * 30);
			int v4 = (int) (Math.random() * 30);
			int v5 = (int) (Math.random() * 30);
			run = new int[] { v1, v2, v3, v4, v5 };
		}

		return run;
	}

}
