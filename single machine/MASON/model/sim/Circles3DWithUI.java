package sim;

import sim.portrayal3d.network.*;
import sim.portrayal3d.continuous.*;
import sim.display3d.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import sim.portrayal3d.simple.*;

public class Circles3DWithUI extends GUIState
    {
    public Display3D display;
    public JFrame displayFrame;

    ContinuousPortrayal3D nodePortrayal = new ContinuousPortrayal3D();

    public static void main(String[] args)
    {
      new Circles3DWithUI().createController();
    }

    public Circles3DWithUI() { 
      super(new Circles3D(System.currentTimeMillis())); 
    }

    public Circles3DWithUI(SimState state) 
    { 
      super(state); 
    }

    public static String getName() { return "Circles 3D Benchmark"; }
    
    public Object getSimulationInspectedObject() { return state; }

    public void start()
    {
      super.start();
      setupPortrayals();
    }

    public void load(SimState state)
    {
      super.load(state);
      setupPortrayals();
    }

    public void setupPortrayals()
    {      
      Circles3D sim = (Circles3D) state;
      
      nodePortrayal.setField( sim.environment );
      try
          {
            nodePortrayal.setPortrayalForAll(new CircledPortrayal3D(
                    new Shape3DPortrayal3D(new com.sun.j3d.utils.geometry.ColorCube()),
                    20f, true));

          }
      catch (Exception e) { throw new RuntimeException("Error", e); }

      display.createSceneGraph(); 
      display.reset();
    }

    public void init(Controller c)
        {
        super.init(c);

        Circles3D sim = (Circles3D) state;

        // make the displayer
        display = new Display3D(600,600,this);                        
                        
        display.attach( nodePortrayal, "Balls" );

        display.translate(-sim.WIDTH/2,
            -sim.WIDTH/2,
            -sim.WIDTH/2);
        
        display.scale(1.0/sim.WIDTH);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Balls and Bands");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);

        display.getSelectionBehavior().setTolerance(10.0f);
        }

    public void quit()
        {
        super.quit();

        if (displayFrame!=null) displayFrame.dispose();
        displayFrame = null;
        display = null;
        }

    }