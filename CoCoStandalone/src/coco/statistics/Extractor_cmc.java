package coco.statistics;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class Extractor_cmc {
	public static void main(String[] args) {
		try {
			String xx = extractor_cmc(
					"C:/Users/Asistente/Documents/InvestIT_SPL/CoCoStandalone/models/modelosGenerados/experimento1/40/11/coco11.xmi");
			append_cmc("C:/Users/Asistente/Documents/InvestIT_SPL/CoCoStandalone/models/modelosGenerados/experimento1/40/11/FeatureModel11.xmi", xx);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Extractor_cmc() {
	}

	public static String append_cmc(String path, String cmc) throws Exception {
		String salida = "";
		String separador = "</featureModels>";
		String texto = "";
		String cont = "";
		FileReader lector = new FileReader(path);

		// El contenido de lector se guarda en un BufferedReader
		BufferedReader contenido = new BufferedReader(lector);
		while ((texto = contenido.readLine()) != null) {
			cont += " " + texto;
		}
		String[] objetos = cont.split(separador);
		for (int i = 0; i < objetos.length; i++) {
			if (i < objetos.length - 2) {
				salida += "" + objetos[i] + " </featureModels> ";

			} else if (i == objetos.length - 2) {
				salida += "" + objetos[i] + " </featureModels> " + cmc;
			} else {
				salida += "" + objetos[i];
			}
		}
		Object myFoo;
		FileWriter fooWriter = new FileWriter(path, false); // true to append
        // false to overwrite.
fooWriter.write(salida);
fooWriter.close();
		return salida;
	}

	public static String extractor_cmc(String path) throws Exception {
		String salida = "";

		
		String texto = "";
		String cont = "";
		FileReader lector = new FileReader(path);

		// El contenido de lector se guarda en un BufferedReader
		BufferedReader contenido = new BufferedReader(lector);
		while ((texto = contenido.readLine()) != null) {
			cont += " " + texto;
		}
		String[] objetos = cont.split("cmConstraints");
		for (int i = 0; i < objetos.length - 1; i++) {
			if (i > 1 && i < objetos.length - 2) {
				salida += "cmConstraints" + objetos[i] + "  ";

			} else if (i == 1) {
				salida += "<cmConstraints" + objetos[i];
			} else if (i == objetos.length - 2) {
				salida += "cmConstraints" + objetos[i] + "cmConstraints>";
			}
		}
		return salida;
	}
}
