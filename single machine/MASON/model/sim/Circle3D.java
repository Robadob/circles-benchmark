package sim;

import ec.util.MersenneTwisterFast;
import sim.util.*;
import sim.engine.*;
import java.util.Iterator;

public class Circle3D implements Steppable
{
  //Member vars
  private Double3D location;
  private MutableDouble3D displacement;
  //Random init
  public Circle3D(MersenneTwisterFast rng, float WIDTH)
  {
    location = new Double3D(
            rng.nextDouble()*WIDTH,
            rng.nextDouble()*WIDTH,
            rng.nextDouble()*WIDTH
          );
    displacement = new MutableDouble3D(0.0,0.0,0.0);
  }
  //Init with clamping
  public Circle3D(Double3D loc, float WIDTH)
  {
    location = new Double3D(
        Math.max(0, Math.min(WIDTH, loc.getX())),
        Math.max(0, Math.min(WIDTH, loc.getY())),
        Math.max(0, Math.min(WIDTH, loc.getZ()))
      );
    displacement = new MutableDouble3D(0.0,0.0,0.0);
  }
  public Double3D getLocation() { 
    return location;
  }
  public void step(SimState state)
  {
    Circles3D sim = (Circles3D)state;
    
    MutableDouble3D toLoc;
    Double3D neighbourLoc;
    double locDist, separation;
    double k;
	  displacement = new MutableDouble3D(0.0,0.0,0.0);
	  //Iterate all particles within the interaction radius x2
    Bag n = sim.environment.getNeighborsWithinDistance(location, sim.INTERACTION_RADIUS2);
    int bagSize = n.size();
    for(int i = 0;i<bagSize;i++)
    {
      Circle3D neighbr = ((Circle3D)n.objs[i]);
      neighbourLoc = neighbr.getLocation();
      if(!neighbourLoc.equals(location))
      {
        toLoc = new MutableDouble3D(neighbourLoc.subtract(location));
        separation = toLoc.length();
        
        if(separation < sim.INTERACTION_RADIUS2)
        {
			    k = (separation < sim.INTERACTION_RADIUS) ? sim.REPULSION_FORCE : sim.ATTRACTION_FORCE;
			    if(separation < sim.INTERACTION_RADIUS) toLoc.negate();
          toLoc.multiplyIn(1.0/separation);//Normalize (without recalculating separation)
          separation = (separation < sim.INTERACTION_RADIUS) ? separation : (sim.INTERACTION_RADIUS2 - separation);
          toLoc.multiplyIn(k * separation);
          displacement.addIn(toLoc);
        }
      }
    } 
  }
  public void updateLocation(SimState state)
  {
    long startTime = System.nanoTime();
    Circles3D sim = (Circles3D)state;
    //Clamp location to environment bounds
    location = new Double3D(
      Math.max(0, Math.min(sim.WIDTH-1, location.getX()+displacement.getX())),
      Math.max(0, Math.min(sim.WIDTH-1, location.getY()+displacement.getY())),
      Math.max(0, Math.min(sim.WIDTH-1, location.getZ()+displacement.getZ()))
    );
    //Update circles location
    sim.environment.setObjectLocation(this, location);
    
    long endTime = System.nanoTime();
        System.out.printf("Time: %dns\n", endTime-startTime);
  }

}