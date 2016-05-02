import java.io.*;
import java.nio.file.*;
import java.lang.String;
import java.nio.charset.Charset;

public class FLAMEGPUBuilder
{
  public static void main(String[] args) throws Exception
  {
    float WIDTH = 100.0f;
    float DENSITY = 0.01f;
    float INTERACTION_RADIUS = 5.0f;
    float ATTRACTION_FORCE = 0.00001f;
    float REPULSION_FORCE = 0.00001f;
    final float DIMENSIONS = 3.0f;
    File FLAME_DIR = new File("C:\\Users\\rob\\FLAMEGPU");
    File IN_DIR = new File("C:\\Users\\rob\\Desktop\\circles\\single machine\\FLAMEGPU\\model");
    String OUT_LOC = "";
    //Parse input args
    for(int i=0;i<args.length;i++)
    {
      String arg = args[i].toLowerCase();
      if (arg.equals("-width")||arg.equals("-w"))
      {
        i++;
        WIDTH = Float.parseFloat(args[i]);
      }
      else if (arg.equals("-density")||arg.equals("-d"))
      {
        i++;
        DENSITY = Float.parseFloat(args[i]);
      }
      else if (arg.equals("-radius")||arg.equals("-rad"))
      {
        i++;
        INTERACTION_RADIUS = Float.parseFloat(args[i]);
      }
      else if (arg.equals("-attract"))
      {
        i++;
        ATTRACTION_FORCE = Float.parseFloat(args[i]);
      }
      else if (arg.equals("-repel"))
      {
        i++;
        REPULSION_FORCE = Float.parseFloat(args[i]);
      }
      else if(arg.equals("-out"))
      {
        i++;
        OUT_LOC = args[i];
      }
      else if(arg.equals("-in"))
      {
        i++;
        IN_DIR = new File(args[i]);
        if(!IN_DIR.exists()||!IN_DIR.isDirectory())
        {
          System.out.println("The -in arg must pass the directory containing model templates.");
          printUsage();
          System.exit(1);
        }
      }
      else if(arg.equals("-flame"))
      {
        i++;
        FLAME_DIR = new File(args[i]);
        if(!FLAME_DIR.exists()||!FLAME_DIR.isDirectory())
        {
          System.out.println("The -flame arg must pass the root directory of FLAMEGPU.");
          printUsage();
          System.exit(1);
        }
      }
      else
      {
          System.out.println("The "+args[i]+" arg was not recognised.");
          printUsage();
          System.exit(1);
      }
    }
    if(OUT_LOC.isEmpty())
    {
      System.out.println("-out must be specified.");
      printUsage();
      System.exit(1);
    }
    //Read in input files
    String XMLModelFile_template;
    String functions_template;
    byte[] encoded = Files.readAllBytes(Paths.get(IN_DIR.getAbsolutePath(),"XMLModelFile.xml"));
    XMLModelFile_template = new String(encoded, Charset.defaultCharset());
    encoded = Files.readAllBytes(Paths.get(IN_DIR.getAbsolutePath(),"functions.c"));
    functions_template = new String(encoded, Charset.defaultCharset());
    
    //Perform replacement
    //width
    XMLModelFile_template = XMLModelFile_template.replaceAll("__WIDTH__",String.format(java.util.Locale.US,"%.6f",WIDTH));
    functions_template = functions_template.replaceAll("__WIDTH__",String.format(java.util.Locale.US,"%.6f",WIDTH));
    //density
    XMLModelFile_template = XMLModelFile_template.replaceAll("__DENSITY__",String.format(java.util.Locale.US,"%.6f",DENSITY));
    functions_template = functions_template.replaceAll("__DENSITY__",String.format(java.util.Locale.US,"%.6f",DENSITY));
    //radius
    XMLModelFile_template = XMLModelFile_template.replaceAll("__RADIUS__",String.format(java.util.Locale.US,"%.6f",INTERACTION_RADIUS));
    functions_template = functions_template.replaceAll("__RADIUS__",String.format(java.util.Locale.US,"%.6f",INTERACTION_RADIUS));
    //radiusX2
    XMLModelFile_template = XMLModelFile_template.replaceAll("__RADIUS2__",String.format(java.util.Locale.US,"%.6f",INTERACTION_RADIUS*2));
    functions_template = functions_template.replaceAll("__RADIUS2__",String.format(java.util.Locale.US,"%.6f",INTERACTION_RADIUS*2));
    //attract force
    XMLModelFile_template = XMLModelFile_template.replaceAll("__ATTRACT__",String.format(java.util.Locale.US,"%.6f",ATTRACTION_FORCE));
    functions_template = functions_template.replaceAll("__ATTRACT__",String.format(java.util.Locale.US,"%.6f",ATTRACTION_FORCE));
    //repel force
    XMLModelFile_template = XMLModelFile_template.replaceAll("__REPEL__",String.format(java.util.Locale.US,"%.6f",REPULSION_FORCE));
    functions_template = functions_template.replaceAll("__REPEL__",String.format(java.util.Locale.US,"%.6f",REPULSION_FORCE));
    //AGENT POP
    final int AGENT_COUNT = (int)Math.floor(Math.pow(WIDTH,DIMENSIONS)*DENSITY);
    XMLModelFile_template = XMLModelFile_template.replaceAll("__AGENT_COUNT__",""+AGENT_COUNT);
    functions_template = functions_template.replaceAll("__AGENT_COUNT__",""+AGENT_COUNT);
    
    //Overwrite model files
    Files.write(
      Paths.get(FLAME_DIR.getAbsolutePath(),"examples", "CirclesPartitioning_float","src", "model", "XMLModelFile.xml"),
      XMLModelFile_template.getBytes(),
      StandardOpenOption.CREATE, 
      StandardOpenOption.TRUNCATE_EXISTING, 
      StandardOpenOption.WRITE
    );
    Files.write(
      Paths.get(FLAME_DIR.getAbsolutePath(),"examples", "CirclesPartitioning_float","src", "model", "functions.c"),
      functions_template.getBytes(),
      StandardOpenOption.CREATE, 
      StandardOpenOption.TRUNCATE_EXISTING, 
      StandardOpenOption.WRITE
    );
    //Call compilation
    String batchCommand = "call \"C:\\Program Files (x86)\\Microsoft Visual Studio 11.0\\Common7\\Tools\\VsDevCmd.bat\"\r\nMsBuild.exe \""+Paths.get(FLAME_DIR.getAbsolutePath(),"examples","CirclesPartitioning_float", "CirclesPartitioning_float.sln").toString()+"\" /t:Rebuild /p:Configuration=Release_Console /property:Platform=x64";
    //Write to batchfile
    Files.write(
      Paths.get("BuildFlame.bat"),
      batchCommand.getBytes(),
      StandardOpenOption.CREATE, 
      StandardOpenOption.TRUNCATE_EXISTING, 
      StandardOpenOption.WRITE
    );
    ProcessBuilder pb =new ProcessBuilder("BuildFlame.bat");
    Process p = pb.start();
    
BufferedReader stdInput = new BufferedReader(new 
     InputStreamReader(p.getInputStream()));
BufferedReader stdError = new BufferedReader(new 
     InputStreamReader(p.getErrorStream()));

// read the output from the command
String s = null;
while ((s = stdInput.readLine()) != null) {
  //  System.out.println(s);
}
while ((s = stdError.readLine()) != null) {
  //  System.out.println(s);
}
    p.waitFor();
   
    //Copy executable
    Files.copy(
      Paths.get(FLAME_DIR.getAbsolutePath(),"bin","x64", "Release_Console", "CirclesPartitioning_float.exe"),
      Paths.get(OUT_LOC),
      StandardCopyOption.REPLACE_EXISTING
    );
    System.out.println("Width: "+WIDTH+" Radius: "+INTERACTION_RADIUS+" Density: "+DENSITY+" built successfully.");
  }
  public static void printUsage()
  {
    System.out.println("<Executable> <params>");
    System.out.println("-width -w <Environment width>");
    System.out.println("-density -d <Environment density>");
    System.out.println("-radius -a <Attraction force>");
    System.out.println("-repel -r <repulsion force>");
    System.out.println("-flame <FLAMEGPU root dir>");
    System.out.println("-in <Directory containing XMLModelFile.xml and functions.c templates>");
    System.out.println("-out <Output executable file path/name>");
  }
}