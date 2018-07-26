package es.uvigo.esei.dai.hybridserver;


import java.io.File;
import java.io.FileNotFoundException;




public class Launcher {
	private static Configuration config;

	public static void main(String[] args) throws FileNotFoundException {

		HybridServer h = null;
		if (args.length == 1) {


			XMLConfigurationLoader loader = new XMLConfigurationLoader();
			File archivo = new File(args[0]);
			try {
				config = loader.load(archivo);
				h = new HybridServer(config);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (args.length == 0) {
			h = new HybridServer();

			
		} else {
			System.out.println("Error: Hay más de un parámetro para inicializar el HybridServer");
		}
		h.start();
	}

	
}
