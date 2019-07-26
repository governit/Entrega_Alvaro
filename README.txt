1. Crear proyecto java.
2. Incluir al mismo nivel de la carpeta "src" las carpetas que están en el archivo libs.zip (libs, metamodels, transformation, workflow)
3. Incluir en el buildpath del proyecto todos los .jar de la carpeta "libs" y de la carpeta "libs/z3-4.8.5-x64-win/bin"
4. Incluir en la variable de entorno "path" la referencia a la ruta de jdk en uso (ej. C:\Program Files\Java\jdk-10.0.1\bin)
5. Incluir en la carpeta "bin" de la ruta del jdk los dll "bin/libz3.dll" y "bin/libz3java.dll"
6. Reinicar el computador
7. Comprobar en el CMD que el sistema encuentre los dll como el comando "where libz3java.dll"
	C:\Users\Alvaro>where libz3java.dll
	C:\Program Files\Java\jdk-10.0.1\bin\libz3java.dll
8. Si no funcion, verificar la instalación de visual studio.
9. En el proyecto crear una clase que llame a la subclase MainClass
	package haber;
	import coco.util.MainClass;
	public class test {
		public static void main(String ar[]) {
		MainClass x = new MainClass();
		}
	}
