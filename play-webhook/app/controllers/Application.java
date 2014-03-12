package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {

    public static Result index() {
        return ok("Running Web Tools");
    }
    
    @BodyParser.Of(Json.class)
    public static Result git() throws IOException {
      //Get JSON
      JsonNode json = request().body().asJson();
      //Find Value
      String ref = json.findPath("ref").textValue();
      //Verify is not null
      if(ref == null) {
        return badRequest("Missing parameter [ref]");
      } 
      String status = "";
      //Check Ref is Master
      if(ref.equals("refs/heads/master")){
    	  status = "Script Run";
    	  CommandHelper.runCommand(Path.scriptPath, true, Path.logPath,Path.logName);
      }
      else
    	  status = "Script Not Run";
      return ok("Status: "+status);
    }
    
    
    public static class CommandHelper{
    	
    	public static void runCommand(String cmd, Boolean log, String logPath, String logName){
    		try {  
    			//Run Process
    			Process p = Runtime.getRuntime().exec(cmd);  
    			if(log){
    				//Read Input Stream
    				BufferedReader in = new BufferedReader(  
    				                                new InputStreamReader(p.getInputStream()));
    				
    				//Open File
    				File file = new File(logPath+logName);
    				 
    				// if file doesnt exists, then create it
    				if (!file.exists()) {
    					file.createNewFile();
    				}
    	 
    				FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
    				BufferedWriter bw = new BufferedWriter(fw);
    				
    				
    				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    				Date date = new Date();
    				
    				//Write TimeStamps
    				bw.write("\n=====Log Date: "+dateFormat.format(date)+"=====\n");
    				
    				//Empty String
    				String line = null;
    				
    				
    				while ((line = in.readLine()) != null) {  
    					//Write Lines
    					bw.write(line+"\n");
    				}  
    				
    				//Write End Delimiter
    				bw.write("\n========================================\n");
    				
    				//Close File
    				bw.close();
    			}
    			
    		} catch (IOException e) {  
    			e.printStackTrace(); 
    		}  
    		
    		
    	}
    	
    	
    	
    }
    
    
}
