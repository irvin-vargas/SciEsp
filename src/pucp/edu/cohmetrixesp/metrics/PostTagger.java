package pucp.edu.cohmetrixesp.metrics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import pucp.edu.cohmetrixesp.structs.CohParagraph;
import pucp.edu.cohmetrixesp.structs.CohStats;
import pucp.edu.cohmetrixesp.structs.CohText;
import pucp.edu.cohmetrixesp.structs.FreelingWordIterable;
import pucp.edu.cohmetrixesp.utils.IFreelingAnalyzer;
import pucp.edu.cohmetrixesp.utils.ImplFreelingAnalyzer;
import edu.upc.freeling.*;

public class PostTagger {
  // Modify this line to be your FreeLing installation directory
  private static final String FREELINGDIR = "/usr/local";
  private static final String DATA = FREELINGDIR + "/share/freeling/";
  private static final String LANG = "es";
  private IFreelingAnalyzer freeling = ImplFreelingAnalyzer.getInstance();
  private DescriptiveAnalyzer desc = DescriptiveAnalyzer.getInstance();
  public BufferedWriter bfw;
  public ArrayList<String> listaB;
  public ArrayList<String> listaG;
  public ArrayList<String> listaP;
  public ArrayList<String> listaM;
  public ArrayList<String> listaR;
  public ArrayList<String> listaC;
  public ArrayList<String> listaS;
  public ArrayList<String> listaOraciones;
  
  public ArrayList<String> getListaOraciones()
  {
	  return this.listaOraciones;
  }
  
  public PostTagger(String text,BufferedWriter bfw) 
  {
	  this.bfw = bfw;
	  
    LangIdent lgid = new LangIdent(DATA + "/common/lang_ident/ident-few.dat");

    Tokenizer tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
    Splitter sp = new Splitter( DATA + LANG + "/splitter.dat" );
    Maco mf = freeling.getMorfological();//new Maco( op );

    HmmTagger tg = new HmmTagger( DATA + LANG + "/tagger.dat", true, 2 );
    ChartParser parser = new ChartParser(
      DATA + LANG + "/chunker/grammar-chunk.dat" );
    DepTxala dep = new DepTxala( DATA + LANG + "/dep/dependences.dat",
      parser.getStartSymbol() );
    Nec neclass = new Nec( DATA + LANG + "/nerc/nec/nec-ab-poor1.dat" );

    Senses sen = new Senses(DATA + LANG + "/senses.dat" ); // sense dictionary
    Ukb dis = new Ukb( DATA + LANG + "/ukb.dat" ); // sense disambiguator
    
    listaB = new ArrayList<String>();
    listaG = new ArrayList<String>();
    listaP = new ArrayList<String>();
    listaM = new ArrayList<String>();
    listaR = new ArrayList<String>();
    listaC = new ArrayList<String>();
    listaS = new ArrayList<String>();
    listaOraciones = new ArrayList<String>();
    
    leerArchivo();
    
    String line = text;

    ListWord l = tk.tokenize( line );

    ListSentence ls = sp.split( l, false );

    mf.analyze( ls );

    tg.analyze( ls );

    neclass.analyze( ls );

    sen.analyze( ls );
    dis.analyze( ls );
    
    printResults( ls, "tagged" );
  }

  private static void printSenses( Word w ) {
    String ss = w.getSensesString();

    // The senses for a FreeLing word are a list of
    // pair<string,double> (sense and page rank). From java, we
    // have to get them as a string with format
    // sense:rank/sense:rank/sense:rank
    // which will have to be splitted to obtain the info.
    //
    // Here, we just output it:
    System.out.print( " " + ss );
  }

  public void printResults( ListSentence ls, String format ) 
  {
	//System.out.println("------------------");
	String cad = "";
    if (format == "tagged") 
    {
    	//System.out.println(ls.size());
      ListSentenceIterator sIt = new ListSentenceIterator(ls);
      int j=0;
       while (sIt.hasNext()) 
      {
    	  j++;
        Sentence s = sIt.next();
        
        FreelingWordIterable words = new FreelingWordIterable(s);
        
        Word ori = null;
        
        int n = 0;
		for (Word word : words) {
			if (isWord(word)) {
				n += 1;
			}
		}
		
		int orig = n;
		
		cad = evaluar(n);
		int ultimo = (int)ls.size();
		int penultimo = ultimo-1;
		if (j==1)
				cad += ",Primera";
		else if(j==2)
				cad += ",Segunda";
		else if (j==penultimo)
				cad += ",Penúltima";
		else if(j==ultimo)
				cad += ",Última";
		else
				cad += ",Mediana";
		
		if(j==11)
		{
			//System.out.println();
		}
        
        ListWordIterator wIt2 = new ListWordIterator(s);
        int i=0;
        
        String oracion = obtenerCadena(wIt2);
        
        //System.out.println(texto);
        
        ListWordIterator wIt = new ListWordIterator(s);
        
        while (wIt.hasNext()) 
        {
          Word w = wIt.next();
          Word w2 = null;
          Word w3 = null;
          if (w.getTag().charAt(0)=='V' && i==0)
          {
        	  n--;
        	  //System.out.print(w.getTag()+" ");
        	  switch(w.getTag().charAt(1))
        	  {
        	  	case 'S'://este se paso de verga
        	  		w2 = wIt.next();
        	  		if(w2.getTag().charAt(0)!='V')//la valio
        	  		{
        	  			cad += analizaTiempo(w.getTag().charAt(3));
            	  		i++;
        	  		}
        	  		else
        	  		{
        	  			switch(w2.getTag().charAt(2))
            	  		{
            	  			case 'P'://Participio y pasivo
            	  				cad += analizaCPO(w.getTag().charAt(3),"PASIVO");
                    	  		i++;
            	  				break;
            	  		}
        	  		}
        	  		break;
        	  	case 'M'://solo se verifica tiempo :D
        	  		if (w.getTag().charAt(2)=='P')
        	  			i=0;
        	  		else 
        	  		{	if ((w.getTag().charAt(2)=='N')||(w.getTag().charAt(2)=='G'))
        	  				cad += ",PRES,ACTIVO";
        	  			else
        	  				cad += analizaTiempo(w.getTag().charAt(3));
        	  			i++;
        	  		}
        	  		break;
        	  	case 'A'://caso pendejo :(
        	  		w2 = wIt.next();
        	  		if(w2.getTag().charAt(0)=='S')//si es sustantivo analizo el tiempo
        	  		{
        	  			cad += analizaTiempo(w.getTag().charAt(3));
        	  			i++;
        	  		}
        	  		else
        	  		{
	        	  		switch(w2.getTag().charAt(1))
	        	  		{
	        	  			case 'M'://otra vez caso facil :D
	        	  				switch(w2.getTag().charAt(2))
	                	  		{
	                	  			case 'P'://Participio y activo
	                	  				cad += analizaCPO(w.getTag().charAt(3),"ACTIVO");
	                        	  		i++;
	                	  				break;
	                	  			case 'G'://Gerundio
	                	  				cad += analizaCT(w.getTag().charAt(3));
	                        	  		i++;
	                	  				break;
	                	  		}
	        	  				break;
	        	  			case 'A'://otra vez caso pendejo :(
	        	  				w3 = wIt.next();
	        	  				switch(w3.getTag().charAt(2))
	                	  		{
	                	  			case 'P'://Participio
	                	  				cad += analizaCPO(w.getTag().charAt(3),"PASIVO");
	                	  				i++;
	                	  				break;
	                	  			case 'G'://Gerundio
	                	  				cad += analizaCPOCT(w.getTag().charAt(3));
	                	  				i++;
	                	  				break;
	                        	}
	                	  		break;
	                	  }
        	  		}
        	  		break;
        	  }
	        	  if(i==1)
		  	        {
		  	        	if((w.getLemma().equals("deber"))||(w.getLemma().equals("poder"))||(w.getLemma().equals("querer"))||(w.getLemma().equals("desear"))||(w.getLemma().equals("soler")))
		  	          		cad += ",si";
		  	        	else if(w.getLemma().equals("tener"))
		  	        	{
		  	        		w2 = wIt.next();
		  	        		if (w2.getLemma().equals("que"))
		  	        			cad += ",si";
		  	        		else
			  	    	      	cad += ",no";
		  	        	}
		  	    	    else
		  	    	      	cad += ",no";
		  	        	//System.out.print(cad);
		  	        	try
		  				{
		  	        		cad += analizaExpresion(oracion);
		  	        		listaOraciones.add(oracion);
		  	        		bfw.write(cad);
		  	        		System.out.print(n+" "+w.getForm()+" "+w.getLemma()+" "+cad);
		  				}
		  				catch (Exception e){}
		        	 }
          	}
	      }
        }
      }
   }
  
  public String analizaExpresion(String texto)
  {
	  boolean es = false;
	  for(int i = 0;i < listaB.size();i++)
	  {
		  es = texto.contains(listaB.get(i));
		  if(es)
			  return ",B,?\n";
	  }
	  for(int i = 0;i < listaG.size();i++)
	  {
		  es = texto.contains(listaG.get(i));
		  if(es)
			  return ",G,?\n";
	  }
	  for(int i = 0;i < listaP.size();i++)
	  {
		  es = texto.contains(listaP.get(i));
		  if(es)
			  return ",P,?\n";
	  }
	  for(int i = 0;i < listaM.size();i++)
	  {
		  es = texto.contains(listaM.get(i));
		  if(es)
			  return ",M,?\n";
	  }
	  for(int i = 0;i < listaR.size();i++)
	  {
		  es = texto.contains(listaR.get(i));
		  if(es)
			  return ",R,?\n";
	  }
	  for(int i = 0;i < listaC.size();i++)
	  {
		  es = texto.contains(listaC.get(i));
		  if(es)
			  return ",C,?\n";
	  }
	  for(int i = 0;i < listaS.size();i++)
	  {
		  es = texto.contains(listaS.get(i));
		  if(es)
			  return ",S,?\n";
	  }
	  return ",noexpr,?\n";
  }
  
  public void leerArchivo()
  {
	  File archivo = new File ("./files/regularExpr.txt");
	  try
	  {
		  FileReader fr = new FileReader (archivo);
		  BufferedReader br = new BufferedReader(fr);
		  String linea;
		  int i=0;
		  while((linea = br.readLine())!=null)
		  {
			  if(linea.charAt(0) == '-')
				  i++;
			  switch (i)
			  {
			  	case 0:
			  		listaB.add(linea);
			  		break;
			  	case 1:
			  		listaG.add(linea);
			  		break;
			  	case 2:
			  		listaP.add(linea);
			  		break;
			  	case 3:
			  		listaM.add(linea);
			  		break;
			  	case 4:
			  		listaR.add(linea);
			  		break;
			  	case 5:
			  		listaC.add(linea);
			  		break;
			  	case 6:
			  		listaS.add(linea);
			  		break;
			  }
		  }
		  br.close();
		  fr.close();
	  }
	  catch(Exception e){}
  }
  
  public String obtenerCadena(ListWordIterator w)
  {
	  String texto = "";
	  while(w.hasNext())
	  {
		  Word word = w.next();
		  if(w.hasNext())
			  texto += word.getForm() + " ";
		  else
			  texto += word.getForm();
	  }
	  return texto;
  }
  
  public String analizaTiempo(char c)
  {
	  String cad = "";
	  switch(c)
		{
			case 'P'://Presente
				cad = ",PRES,ACTIVO";
				break;
			case 'S'://Pasado
				cad = ",PAST,ACTIVO";
				break;
			case 'F'://Futuro
				cad = ",FUT,ACTIVO";
				break;
			case 'I'://Imperfecto
				cad = ",IMP,ACTIVO";
				break;
			case 'C'://Condicional
				cad = ",COND,ACTIVO";
				break;
		}
	  return cad;
  }
  
  public String analizaCPOCT(char c)
  {
	  String cad = "";
	  switch(c)
		{
			case 'P'://Presente
				cad = ",PRES_CPO_CT,ACTIVO";
				break;
			case 'S'://Pasado
				cad = ",PAST_CPO_CT,ACTIVO";
				break;
			case 'F'://Futuro
				cad = ",FUT_CPO_CT,ACTIVO";
				break;
			case 'I'://Imperfecto
				cad = ",IMP_CPO_CT,ACTIVO";
				break;
			case 'C'://Condicional
				cad = ",COND_CPO_CT,ACTIVO";
				break;
		}
	  return cad;
  }
  
  public String analizaCT(char c)
  {
	  String cad = "";
	  switch(c)
		{
			case 'P'://Presente
				cad = ",PRES_CT,ACTIVO";
				break;
			case 'S'://Pasado
				cad = ",PAST_CT,ACTIVO";
				break;
			case 'F'://Futuro
				cad = ",FUT_CT,ACTIVO";
				break;
			case 'I'://Imperfecto
				cad = ",IMP_CT,ACTIVO";
				break;
			case 'C'://Condicional
				cad = ",COND_CT,ACTIVO";
				break;
		}
	  return cad;
  }
  
  public String analizaCPO(char c,String estado)
  {
	  String cad = "";
	  switch(c)
		{
			case 'P'://Presente
				cad = ",PRES_CPO,"+estado;
				break;
			case 'S'://Pasado
				cad = ",PAST_CPO,"+estado;
				break;
			case 'F'://Futuro
				cad = ",FUT_CPO,"+estado;
				break;
			case 'I'://Imperfecto
				cad = ",IMP_CPO,"+estado;
				break;
			case 'C'://Condicional
				cad = ",COND_CPO,"+estado;
				break;
		}
	  return cad;
  }
  
  public boolean isWord(Word w) {
		return !w.getTag().startsWith("F") && 
			   !w.getTag().startsWith("Z") &&
			   !w.getTag().startsWith("W");
	}
  
			//try
			//{bfw.write(cad);}
			//catch (Exception e){}
	
	public String evaluar(int valor)
	{
		String cad = "";
		try
		{
			if(valor<20)
				cad = "Corta";
			if(valor>=20 && valor<40)
				cad = "Mediana";
			if(valor>=40)
				cad = "Larga";
		}
		catch(Exception e){return null;}
		return cad;
	}
}