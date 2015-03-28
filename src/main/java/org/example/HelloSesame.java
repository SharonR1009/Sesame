package org.example;

import info.aduna.iteration.Iterations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UnsupportedQueryLanguageException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;
import org.openrdf.sail.SailException;
import org.openrdf.sail.inferencer.fc.ForwardChainingRDFSInferencer;
import org.openrdf.sail.memory.MemoryStore;

public class HelloSesame {
	
	public static void getUniv() throws IOException, RepositoryException, RDFHandlerException, MalformedQueryException, UnsupportedQueryLanguageException, SailException{
		String baseURL = "http://dbpedia.org/sparql";
		String para1 = "default-graph-uri=http://dbpedia.org&";
		String para2 = "query=select+distinct+?university+where{"
				+ "?university<http://dbpedia.org/ontology/type><http://dbpedia.org/resource/Private_university>."
				+ "?university<http://dbpedia.org/ontology/state><http://dbpedia.org/resource/California>}";
		String url = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query=select+distinct+%3Funiversity+where%7B%0D%0A%3Funiversity%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Ftype%3E%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FPrivate_university%3E.%0D%0A%3Funiversity%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fstate%3E%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FCalifornia%3E%0D%0A%7D&format=text%2Fhtml&timeout=30000&debug=on";
		URL obj = new URL(baseURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(para1 + para2);
		//wr.writeBytes(url);
		wr.flush();
		wr.close();
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String line, name;

		while ((line = in.readLine()) != null) {
			if(line.contains("href")){
				line = line.split("href=")[1];
				line = line.split(">")[0];
				name = line.split("http://dbpedia.org/resource/")[1];
				/*if(name.contains("%E2%80%93"))
					name = name.replace("%E2%80%93", "-");*/
				name = name.replaceAll("_", " ");
				//System.out.println(line.replaceAll("\"", "") + "," + "\""+name);
				System.out.println(name);
			}
		}
		in.close();
	}
	//
	public static void constructAndStore() throws Exception{
		String baseURL = "http://dbpedia.org/sparql";
		String query = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&"
				+ "query=CONSTRUCT%7B%0D%0A%3Funiversity+a%3Chttp%3A%2F%2Fschema.org%2FCollegeOrUniversity%3E."
				+ "%0D%0A%3Funiversity%3Chttp%3A%2F%2Fschema.org%2Fname%3E+%3Fname."
				+ "%0D%0A%3Funiversity%3Chttp%3A%2F%2Fschema.org%2FadditionalType%3E%3Funiversity."
				+ "%0D%0A%7D+WHERE+%7B%0D%0A%7B%3Funiversity%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Ftype%3E%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FPrivate_university%3E%7D%0D%0AUNION%0D%0A%7B%3Funiversity%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Ftype%3E%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FPrivate_university%3E%7D."
				+ "%0D%0A%7B%3Funiversity%3Chttp%3A%2F%2Fdbpedia.org%2Fontology%2Fstate%3E%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FCalifornia%3E%7D%0D%0AUNION%0D%0A%7B%3Funiversity%3Chttp%3A%2F%2Fdbpedia.org%2Fproperty%2Fstate%3E%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FCalifornia%3E%7D."
				+ "%0D%0A%3Funiversity%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23label%3E%3Fname.%0D%0A%0D%0AFILTER+%28lang%28%3Fname%29+%3D+%22en%22%29.%0D%0A%7D%0D%0A&"
				+ "format=rdf";
		URL obj = new URL(baseURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(query);
		wr.flush();
		wr.close();
		
		File file = new File("data.rdf");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String line;
		StringBuffer re = new StringBuffer();
		while ((line = in.readLine()) != null) {
			re.append(line);
			//System.out.println(line);
		}
		bw.write(re.toString());
		in.close();
		bw.close();
		
		Repository rep = new SailRepository(new MemoryStore());
		rep.initialize();
		String base = "http://example.org/example/local";
		RepositoryConnection conn = rep.getConnection();
		   try {
		      conn.add(file, base, RDFFormat.RDFXML);
		      /*RepositoryResult<Statement> statements =  conn.getStatements(null, null, null, true);
		      Model model = Iterations.addAll(statements, new LinkedHashModel());
		      Rio.write(model, System.out, RDFFormat.TURTLE);*/

		   }
		   finally {
		      conn.close();
		   }
		   findOrg(rep);
		   insert(rep);
		   findAll(rep);
	}
	//
	public static void schema() throws Exception{
		Repository rep = new SailRepository(new MemoryStore());
		rep.initialize();
		File file = new File("all.rdf");
		String base = "http://example.org/example/local";
		RepositoryConnection conn = rep.getConnection();
		   try {
		      conn.add(file, base, RDFFormat.RDFXML);
		      /*RepositoryResult<Statement> statements =  conn.getStatements(null, null, null, true);
		      Model model = Iterations.addAll(statements, new LinkedHashModel());
		      Rio.write(model, System.out, RDFFormat.TURTLE);*/
		   }
		   finally {
		      conn.close();
		   }
		   findOrg(rep);
	}
	//
	public static void forwardChaining() throws Exception{
		Repository rep = new SailRepository(
                new ForwardChainingRDFSInferencer(
                new MemoryStore()));
		rep.initialize();
		File all = new File("all.rdf");
		File file = new File("data.rdf");
		String base = "http://example.org/example/local";
		RepositoryConnection conn = rep.getConnection();
		   try {
		      conn.add(file, base, RDFFormat.RDFXML);
		      conn.add(all, base, RDFFormat.RDFXML);
		      //RepositoryResult<Statement> statements =  conn.getStatements(null, null, null, true);
		      //Model model = Iterations.addAll(statements, new LinkedHashModel());
		      //Rio.write(model, System.out, RDFFormat.TURTLE);
		   }
		   finally {
		      conn.close();
		   }
		   //findOrgFC(rep);
		   insert(rep);
		   findAll(rep);
	}
	//
	public static void findOrg(Repository rep) throws Exception{
		RepositoryConnection con = rep.getConnection();
		   try {
			  String queryString = "SELECT ?organization WHERE { ?organization a <http://schema.org/Organization>} ";
			  TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			  TupleQueryResult result = tupleQuery.evaluate();
			  try {
		              while (result.hasNext()) {  // iterate over the result
		            	  BindingSet bindingSet = result.next();
		            	  Value org = bindingSet.getValue("organization");
		            	  
		            	  // do something interesting with the values here...
		            	  System.out.println(org);
		              }
			  }
			  finally {
			      result.close();
			  }
		   }
		   finally {
		      con.close();
		   }
	}
	//
	public static void findOrgFC(Repository rep) throws Exception{
		RepositoryConnection con = rep.getConnection();
		   try {
			  String queryString = "SELECT ?organization WHERE { ?organization a <http://schema.org/Organization>} ";
			  TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			  TupleQueryResult result = tupleQuery.evaluate();
			  try {
		              while (result.hasNext()) {  // iterate over the result
		            	  BindingSet bindingSet = result.next();
		            	  Value org = bindingSet.getValue("organization");
		            	  
		            	  // do something interesting with the values here...
		            	  System.out.println(org);
		              }
			  }
			  finally {
			      result.close();
			  }
		   }
		   finally {
		      con.close();
		   }
	}
	//
	public static void insert(Repository rep) throws Exception{
		RepositoryConnection conn = rep.getConnection();
		ValueFactory f = rep.getValueFactory();
		URI usc = f.createURI("http://dbpedia.org/resource/University_of_Southern_California");
		URI cl = f.createURI("http://dbpedia.org/resource/C._L._Max_Nikias");
		URI alumni = f.createURI("http://schema.org/alumni");
		conn.add(usc, alumni,cl);
		   try {
		      RepositoryResult<Statement> statements =  conn.getStatements(null, null, null, true);
		      Model model = Iterations.addAll(statements, new LinkedHashModel());
		      //model.add(usc,alumni,cl);
		      //Rio.write(model, System.out, RDFFormat.TURTLE);
		   }
		   finally {
		      conn.close();
		   }
	}
	//
	public static void findAll(Repository rep) throws Exception{
		RepositoryConnection con = rep.getConnection();
		   try {
			  String queryString = "SELECT ?person WHERE { ?person a <http://schema.org/Person>} ";
			  TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

			  TupleQueryResult result = tupleQuery.evaluate();
			  try {
		              while (result.hasNext()) {  // iterate over the result
		            	  BindingSet bindingSet = result.next();
		            	  Value person = bindingSet.getValue("person");
		            	  
		            	  // do something interesting with the values here...
		            	  System.out.println(person);
		              }
			  }
			  finally {
			      result.close();
			  }
		   }
		   finally {
		      con.close();
		   }
	}
	//
	public static void main(String[] args) throws Exception {
		//getUniv();
		//constructAndStore();
		//schema();
		forwardChaining();
	}
}
