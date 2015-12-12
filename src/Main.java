import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import pucp.edu.classifier.AZEsp;
import pucp.edu.classifier.DoAll;
import pucp.edu.classifier.fapero;
import pucp.edu.cohmetrixesp.metrics.MetricsEngine;
import pucp.edu.cohmetrixesp.structs.CohParagraph;
import pucp.edu.cohmetrixesp.utils.ParagraphSplitter;
import edu.upc.freeling.Maco;
import edu.upc.freeling.MacoOptions;
import edu.upc.freeling.Splitter;
import edu.upc.freeling.Tokenizer;
import edu.upc.freeling.Util;

public class Main {
	private static final String FREELINGDIR = "/usr/local";
	private static final String DATA = FREELINGDIR + "/share/freeling/";
	private static final String LANG = "es";

	static public void main(String[] args) {
		
		fapero fap = new fapero();
		fap.setVisible(true);
		
		//DoAll doAll = new DoAll();
		//doAll.now();
		//final MetricsEngine engine = MetricsEngine.getInstance();
		//final AZEsp classifier = AZEsp.getInstance();
		
		//lo siguiente es para crear el arff, OJO crea un arff x texto
		//para entrenar el clasificar es necesario que se cree un solo arff con todos los textos analizados
		//SE COMENTA porque el arff YA FUE CREADO
		/*
		engine.processNewData(
				"./files/in.txt",
				"./files/unlabeled.arff");
		*/
		
		//lo siguiente recibe el texto ingresado por el usuario y utiliza la funcion creada anteriormente
		//para crear su arff SIN EL ATRIBUTO CATEGORIA (clase principal)
		//se debe adaptar como servicio
		//ArrayList<String> listaOraciones = engine.getListaOraciones();
		
		//String clase = classifier.classify(); //se obtiene "labeled.arff"
		//classifier.obtainCategories(listaOraciones);//se obtiene "out.txt"
	}
}