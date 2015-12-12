package pucp.edu.cohmetrixesp.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pucp.edu.cohmetrixesp.structs.CohText;
import pucp.edu.cohmetrixesp.utils.IFreelingAnalyzer;
import pucp.edu.cohmetrixesp.utils.ImplFreelingAnalyzer;

public class MetricsEngine {
	static private MetricsEngine instance = new MetricsEngine();
	private IFreelingAnalyzer freeling = ImplFreelingAnalyzer.getInstance();
	private RefCohesionAnalyzer ref = RefCohesionAnalyzer.getInstance();
	private ConnectivesAnalyzer con = ConnectivesAnalyzer.getInstance();
	private DescriptiveAnalyzer desc = DescriptiveAnalyzer.getInstance();
	public ArrayList<String> listaOraciones;
	
	public ArrayList<String> getListaOraciones()
	{
		  return this.listaOraciones;
	}
	
	private MetricsEngine(){
	}
	
	static public MetricsEngine getInstance() {
		return instance;
	}
	
	public Map<String, Double> analyze(String text) {
		Map<String, Double> ans = new HashMap<>();
		CohText ctxt = new CohText(text);
		ctxt.analyze(freeling);
		long t = System.currentTimeMillis();
		ref.analyze(ans, ctxt);
		long t1 = System.currentTimeMillis();
		System.out.println(t1 - t);
		con.analyze(ans, ctxt);
		desc.analyze(ans, ctxt);
		return ans;
	}
	
	public void process(String in, String out) {
//		File folder = new File("/home/andre/Dropbox/Coh-Metrix-Esp/Entregables/Tesis 2/Corpus/txt");
		File folder = new File(in);
//		String ansDir = "/home/andre/Dropbox/Coh-Metrix-Esp/Entregables/Tesis 2/Corpus/desc/";
		String ansDir = out;
		File [] files = folder.listFiles();
		int ans = 0;
		for (File f: files) {
			if (f.isFile()) {
				//if(f.getName().startsWith("Texto") && f.getName().endsWith("txt")) {
					ans ++;
					System.out.println(f.getPath());
					File ansFile = new File(ansDir + "Metrics_" + f.getName());
					try {

						if (ansFile.exists()) {}
						else ansFile.createNewFile();

						String text = new String(Files.readAllBytes(Paths
								.get(f.getPath())));
						//System.out.println(text);
						//Map<String, Double> acum = analyze(text);
						FileWriter fw = new FileWriter(ansFile);
						BufferedWriter bfw = new BufferedWriter(fw);
						
						bfw.write("@relation "+f.getName()+"\n\n");
						bfw.write("@attribute tamanho {Corta,Mediana,Larga}\n");
						bfw.write("@attribute localizacion {Primera,Segunda,Mediana,Penúltima,Última}\n");
						//bfw.write("@attribute expresion {B,G,P,M,R,C,noexpr}\n");
						bfw.write("@attribute tiempo {PRE,PAST,FUT,IMP,PRES-CPO,PAST-CPO,FUT-CPO,PRES-CT,PAST-CT,FUT-CT,PRES-CPO-CT,PAST-CPO-CT,FUT-CPO-CT,noverb}\n");
						bfw.write("@attribute voz {PASIVO,ACTIVO,noverb}\n");
						bfw.write("@attribute modal {si,no,noverb}\n\n");
						//bfw.write("@attribute historico {_,B,G,P,M,R,C,S}\n\n");
						bfw.write("@data\n");
						//CohText cotext = new CohText(text);
						//Map<String, Double> ans2 = new HashMap<>();
						//cotext.analyze(freeling);
						//desc.setbfw(bfw);
						//desc.analyze(ans2, cotext);
						
						PostTagger tagger = new PostTagger(text,bfw);
						//desc.numberOfWordsInSentences(cotext);
						//for (Entry<String, Double> entry : acum.entrySet() ) {
							//bfw.write(entry.getKey() +  " " + entry.getValue()  + "\n");
						//}
						bfw.close();
						fw.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

				//}
			}
		}
		System.out.println(ans);
	}

	public void processNewData(String in, String out) {
		File f = new File(in);
		if (f.isFile()) {
			System.out.println(f.getPath());
			File ansFile = new File(out);
			try {

				if (ansFile.exists()) {}
				else ansFile.createNewFile();

				String text = new String(Files.readAllBytes(Paths
						.get(f.getPath())));
				FileWriter fw = new FileWriter(ansFile);
				BufferedWriter bfw = new BufferedWriter(fw);
				
				bfw.write("@relation "+f.getName()+"\n\n");
				bfw.write("@attribute tamanho {Corta,Mediana,Larga}\n");
				bfw.write("@attribute localizacion {Primera,Segunda,Mediana,Penúltima,Última}\n");
				bfw.write("@attribute tiempo {PRES,PAST,FUT,IMP,PRES_CPO,PAST_CPO,FUT_CPO,PRES_CT,PAST_CT,FUT_CT,PRES_CPO_CT,PAST_CPO_CT,FUT_CPO_CT,COND,noverb}\n");
				bfw.write("@attribute voz {PASIVO,ACTIVO,noverb}\n");
				bfw.write("@attribute modal {si,no,noverb}\n");
				bfw.write("@attribute expresion {B,G,P,M,R,C,S,noexpr}\n");
				//bfw.write("@attribute historico {_,B,G,P,M,R,C,S}\n\n");
				bfw.write("@attribute categoria {B,G,P,M,R,C,S}\n\n");
				bfw.write("@data\n");
				
				PostTagger tagger = new PostTagger(text,bfw);
				listaOraciones = tagger.getListaOraciones();
				
				bfw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
