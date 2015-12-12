package pucp.edu.classifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import pucp.edu.classifier.AZEsp;
import pucp.edu.cohmetrixesp.metrics.MetricsEngine;
import pucp.edu.cohmetrixesp.structs.CohParagraph;
import pucp.edu.cohmetrixesp.utils.ParagraphSplitter;
import edu.upc.freeling.Maco;
import edu.upc.freeling.MacoOptions;
import edu.upc.freeling.Splitter;
import edu.upc.freeling.Tokenizer;
import edu.upc.freeling.Util;
import java.util.ArrayList;

public class DoAll {
	private static final String FREELINGDIR = "/usr/local";
	private static final String DATA = FREELINGDIR + "/share/freeling/";
	private static final String LANG = "es";
	
	public void now () {
		final MetricsEngine engine = MetricsEngine.getInstance();
		final AZEsp classifier = AZEsp.getInstance();
		
		engine.processNewData("./files/in.txt","./files/unlabeled.arff");
		
		ArrayList<String> listaOraciones = engine.getListaOraciones();
		
		String clase = classifier.classify(); //se obtiene "labeled.arff"
		classifier.obtainCategories(listaOraciones);//se obtiene "out.txt"
	}	
}
