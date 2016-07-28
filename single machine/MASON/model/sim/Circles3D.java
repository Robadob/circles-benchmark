package sim;

import sim.engine.*;
import ec.util.MersenneTwisterFast;
import sim.util.*;
import sim.field.continuous.*;
//Logging
import java.nio.file.*;
import java.io.*;

public class Circles3D extends SimState
{
  private static  final long serialVersionUID = 1L;
  //Model args (static so they can be configured independently of execution)
  static double WIDTH = 50.0f;
  static double DENSITY = 0.01f;
  static double INTERACTION_RADIUS = 5.0f;
  static double INTERACTION_RADIUS2 = 10.0f;
  static double ATTRACTION_FORCE = 0.00001f;
  static double REPULSION_FORCE = 0.00001f;
  
  static boolean LOG_AGENTS = false;
  static boolean LOAD_AGENTS = false;
  static String AGENTS_PATH = "";
  static Circle3D[] agents = null;
  
  public Continuous3D environment;
  
  public Circles3D(long seed)
  {
    super(seed);
    environment = new Continuous3D(
                    INTERACTION_RADIUS*2,
                    WIDTH,
                    WIDTH,
                    WIDTH
                  );
  }
  public void start()
  {
    super.start();
    
    environment.clear();
    
    //Init circles
    int particleCount = (int)Math.round((float)(Math.pow(WIDTH,3)*DENSITY));
    Steppable[] sCalc = new Steppable[particleCount];
    Steppable[] sUpdate = new Steppable[particleCount];
    agents = new Circle3D[particleCount];
    //Create agents
    if(LOAD_AGENTS)
    {
      
      try(BufferedReader br = new BufferedReader(new FileReader(AGENTS_PATH))) 
      {
		    String line = br.readLine();
        int count = Integer.parseInt(line);
        if(count!=particleCount)
        {
          System.out.println("Cannot load agents counts do not match!: loaded("+count+") vs density("+particleCount+")");
          System.exit(1);
        }
        for(int i = 0;i<count;i++)
        {
              line = br.readLine();
              String parts[] = line.split(",");
              agents[i] = new Circle3D(new Double3D(
                  Double.parseDouble(parts[0]),
                  Double.parseDouble(parts[1]),
                  Double.parseDouble(parts[2])
                ), (float)WIDTH);
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
        System.out.println("Unable to load agent data from file. Exiting."+e.toString());
        System.exit(1);
      }
    }
    else
    {
      for(int i = 0;i<particleCount;i++)
      {
        agents[i] = new Circle3D(random, (float)WIDTH);
      }
    }
    //Schedule agents
    for(int i = 0;i<particleCount;i++)
    {
      Circle3D circle = agents[i];
      environment.setObjectLocation(circle, circle.getLocation());
      sCalc[i] = new Steppable()
        {
        public void step(SimState state) { circle.step(state); }
        
        static final long serialVersionUID = -4269174171145445917L;
        };
      sUpdate[i] = new Steppable()
        {
        public void step(SimState state) { circle.updateLocation(state); }
        
        static final long serialVersionUID = -4269174171145445918L;
        };
    }
    //Add the location calculation sequence
    schedule.scheduleRepeating(Schedule.EPOCH,0,new ParallelSequence(sCalc, ParallelSequence.CPUS),1);    
    //Add the location update sequence AFTER the calculation sequence, parallel causes the grid to produce errs
    schedule.scheduleRepeating(Schedule.EPOCH,1,new Sequence(sUpdate),1);
    
  }
  public static void main(String[] args)
  {
    //Parse input args
    int j = 0;
    for(int i=0;i<args.length;i++)
    {
      String arg = args[i].toLowerCase();
      if (arg.equals("-width"))
      {
        i++;
        Circles3D.WIDTH = Double.parseDouble(args[i]);
        j+=2;
      }
      else if (arg.equals("-density"))
      {
        i++;
        Circles3D.DENSITY = Double.parseDouble(args[i]);
        j+=2;
      }
      else if (arg.equals("-radius"))
      {
        i++;
        Circles3D.INTERACTION_RADIUS = Double.parseDouble(args[i]);
        Circles3D.INTERACTION_RADIUS2 = Circles3D.INTERACTION_RADIUS*2;
        j+=2;
      }
      else if (arg.equals("-attract"))
      {
        i++;
        Circles3D.ATTRACTION_FORCE = Double.parseDouble(args[i]);
        j+=2;
      }
      else if (arg.equals("-repel"))
      {
        i++;
        Circles3D.REPULSION_FORCE = Double.parseDouble(args[i]);
        j+=2;
      }
      else if (arg.equals("-load"))
      {
        i++;
        Circles3D.LOAD_AGENTS = true;
        Circles3D.AGENTS_PATH = args[i];
        j+=2;
      }
      else if (arg.equals("-agents"))
      {
        Circles3D.LOG_AGENTS = true;
        j+=1;
      }
      else
      {
        break;
      }
        
    }
    
    //Strip parsed args
    String[] splitArgs = new String[args.length-j];
    System.arraycopy(args, j, splitArgs, 0, splitArgs.length);
    doLoop(Circles3D.class, splitArgs);
    
    System.exit(0);
  }
  public static void logTime(long millisOccured)
  {
    try {
        Files.write(Paths.get("results.txt"), (""
        +String.format(java.util.Locale.US,"%.6f", (float)WIDTH)+","
        +String.format(java.util.Locale.US,"%.6f", (float)DENSITY)+","
        +String.format(java.util.Locale.US,"%.6f", (float)INTERACTION_RADIUS)+","
        +String.format(java.util.Locale.US,"%.6f", (float)ATTRACTION_FORCE)+","
        +String.format(java.util.Locale.US,"%.6f", (float)REPULSION_FORCE)+","
        +(int)Math.round(Math.pow(WIDTH,3)*DENSITY)+","
        +String.format(java.util.Locale.US,"%.6f",millisOccured/1000.0f)+",s\r\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }catch (java.io.IOException e) {
      System.err.println(e.toString());
      System.err.println("Err writing timing info to file.");
    }
    
    //Log agents
    if(agents!=null&&LOG_AGENTS)
    {
      System.out.println("Logging agents to masonAgents.txt");
      //Dump them to file
      PrintWriter writer;
      try {
        writer = new PrintWriter("masonAgents.txt", "UTF-8");
        writer.println(agents.length);
        for(Circle3D p:agents)
        {
          writer.println(p.getLocation().x+","+p.getLocation().y+","+p.getLocation().z);
        }
        writer.close();
      } catch (Exception e) {
        System.out.println("Err printing agents to file.");
        e.printStackTrace();
      }
    }
  }
}