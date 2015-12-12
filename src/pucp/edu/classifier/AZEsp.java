package pucp.edu.classifier;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.classifiers.Classifier;
//import weka.classifiers.functions.SMO;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class AZEsp {
	private static AZEsp instance = new AZEsp();
	private Classifier classifier;
	private Instances trainData;
	public ArrayList<String> listaCategorias;

	String routePath = "./files/classifier.arff";
	
	private AZEsp () {
		try {
			 DataSource source = new DataSource(routePath);
			 trainData = source.getDataSet();
			 trainData.setClassIndex(trainData.numAttributes() - 1);
			 classifier = new NaiveBayes();
			 classifier.buildClassifier(trainData);
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}
	
	static public AZEsp getInstance() {
		return instance;
	}
	
	public String classifyText() throws Exception {
		
		//se clasifica el texto, usando el arff creado anteriormente (en engine.processNewData)
		
		Instances unlabeled = new Instances(
                new BufferedReader(
                  new FileReader("./files/unlabeled.arff")));
		
		unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
		Instances labeled = new Instances(unlabeled);
		
		listaCategorias = new ArrayList<String>();
		for (int i = 0; i < unlabeled.numInstances(); i++) {
		   double clsLabel = classifier.classifyInstance(unlabeled.instance(i));
		   labeled.instance(i).setClassValue(clsLabel);
		   
		   listaCategorias.add(labeled.instance(i).stringValue(unlabeled.numAttributes()-1));
		   //System.out.print(listaCategorias.get(i) + ", ");
		}
		
		BufferedWriter writer = new BufferedWriter(
                 new FileWriter("./files/labeled.arff"));
		writer.write(labeled.toString());
		writer.newLine();
		writer.flush();
		writer.close();
		
		return "Se clasificó con éxito";
	}
	
	public String classify() {
		String clase = "error";
		try {
			 clase = classifyText();
		} catch (Exception e) {
			System.out.println("Error en el clasificador");
			e.printStackTrace();
		}
		return clase;
	}

	public void obtainCategories(ArrayList<String> listaOraciones)
	{
		try {
			BufferedWriter writer = new BufferedWriter(
			        new FileWriter("./files/out.txt"));
			
			//el tamaño de la lista de oraciones DEBE coincidir con el de la lista de categorias
			for(int i = 0;i < listaOraciones.size();i++) {
				if (listaCategorias.get(i).equals("B")) {
					writer.write("---Contexto---\n");
					writer.write(listaOraciones.get(i));
					writer.newLine(); writer.newLine();
				}
				if (listaCategorias.get(i).equals("G")) {
					writer.write("---Brecha---\n");
					writer.write(listaOraciones.get(i));
					writer.newLine(); writer.newLine();
				}
				if (listaCategorias.get(i).equals("P")) {
					writer.write("---Propósito---\n");
					writer.write(listaOraciones.get(i));
					writer.newLine(); writer.newLine();
				}
				if (listaCategorias.get(i).equals("M")) {
					writer.write("---Metodología---\n");
					writer.write(listaOraciones.get(i));
					writer.newLine(); writer.newLine();
				}
				if (listaCategorias.get(i).equals("R")) {
					writer.write("---Resultado---\n");
					writer.write(listaOraciones.get(i));
					writer.newLine();writer.newLine();
				}
				if (listaCategorias.get(i).equals("C")) {
					writer.write("---Conclusión---\n");
					writer.write(listaOraciones.get(i));
					writer.newLine(); writer.newLine();
				}
			}
			
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
