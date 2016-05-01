package sim;

import sim.engine.*;
import ec.util.MersenneTwisterFast;
import sim.util.*;
import sim.field.continuous.*;
//Logging
import java.nio.file.*;

public class Circles3D extends SimState
{
  private static  final long serialVersionUID = 1L;
  //Model args (static so they can be configured independently of execution)
  static float WIDTH = 50.0f;
  static float DENSITY = 0.01f;
  static float INTERACTION_RADIUS = 5.0f;
  static float ATTRACTION_FORCE = 0.00001f;
  static float REPULSION_FORCE = 0.00001f;
  
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
    int particleCount = (int)Math.floor(Math.pow(WIDTH,3)*DENSITY);
    Steppable[] sCalc = new Steppable[particleCount];
    Steppable[] sUpdate = new Steppable[particleCount];
    for(int i = 0;i<particleCount;i++)
    {
      Circle3D circle = new Circle3D(random, WIDTH);
      environment.setObjectLocation(circle, circle.getLocation());
      schedule.scheduleRepeating(circle);           
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
    schedule.scheduleRepeating(Schedule.EPOCH,0,new ParallelSequence(sCalc),1);    
    //Add the location update sequence AFTER the calculation sequence
    schedule.scheduleRepeating(Schedule.EPOCH,1,new ParallelSequence(sUpdate),1);
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
        Circles3D.WIDTH = Float.parseFloat(args[i]);
        j+=2;
      }
      else if (arg.equals("-density"))
      {
        i++;
        Circles3D.DENSITY = Float.parseFloat(args[i]);
        j+=2;
      }
      else if (arg.equals("-radius"))
      {
        i++;
        Circles3D.INTERACTION_RADIUS = Float.parseFloat(args[i]);
        j+=2;
      }
      else if (arg.equals("-attract"))
      {
        i++;
        Circles3D.ATTRACTION_FORCE = Float.parseFloat(args[i]);
        j+=2;
      }
      else if (arg.equals("-repel"))
      {
        i++;
        Circles3D.REPULSION_FORCE = Float.parseFloat(args[i]);
        j+=2;
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
        +String.format(java.util.Locale.US,"%.6f", WIDTH)+","
        +String.format(java.util.Locale.US,"%.6f", DENSITY)+","
        +String.format(java.util.Locale.US,"%.6f", INTERACTION_RADIUS)+","
        +String.format(java.util.Locale.US,"%.6f", ATTRACTION_FORCE)+","
        +String.format(java.util.Locale.US,"%.6f", REPULSION_FORCE)+","
        +(int)Math.floor(Math.pow(WIDTH,3)*DENSITY)+","
        +String.format(java.util.Locale.US,"%.6f",millisOccured/1000.0f)+",s\r\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }catch (java.io.IOException e) {
      System.err.println(e.toString());
      System.err.println("Err writing timing info to file.");
    }
    
  }
}