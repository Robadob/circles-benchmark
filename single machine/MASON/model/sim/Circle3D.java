package sim;

import ec.util.MersenneTwisterFast;
import sim.util.*;
import sim.engine.*;
import java.util.Iterator;

public class Circle3D implements Steppable
{
  //Member vars
  private Double3D location;
  private MutableDouble3D myNewLoc;
  public Circle3D(MersenneTwisterFast rng, float WIDTH)
  {
    location = new Double3D(
            rng.nextDouble()*WIDTH,
            rng.nextDouble()*WIDTH,
            rng.nextDouble()*WIDTH
          );
    myNewLoc = new MutableDouble3D(location);
  }
  public Double3D getLocation() { 
    return location;
  }
  public void step(SimState state)
  {
    Circles3D sim = (Circles3D)state;
    
    Double3D neighbourLoc, locDiff;
    double locDist, separation;
    float k;
	    
	  //Iterate all particles within the interaction radius x2
    Bag n = sim.environment.getNeighborsWithinDistance(location, sim.INTERACTION_RADIUS*2);
    int bagSize = n.size();
    int j=0;
    for(int i = 0;i<bagSize;i++)
    {
      Circle3D neighbr = ((Circle3D)n.objs[i]);
      if(neighbr==null)
      {
        //At high neighbourhood volumes bags often contain a single null value
        continue;        
      }
      neighbourLoc = neighbr.getLocation();
      locDiff = location.subtract(neighbourLoc);
      locDist = locDiff.length();
      separation = locDist - sim.INTERACTION_RADIUS;
      if(separation < sim.INTERACTION_RADIUS)
      {
        if ( separation > 0)
          k = sim.ATTRACTION_FORCE;
        else
          k = -sim.REPULSION_FORCE;
        myNewLoc.addIn(locDiff.multiply(k*separation/sim.INTERACTION_RADIUS));
      }
    }
  }
  public void updateLocation(SimState state)
  {
    Circles3D sim = (Circles3D)state;
    //Clamp location to environment bounds
    location = new Double3D(
      Math.max(0, Math.min(sim.WIDTH, myNewLoc.getX())),
      Math.max(0, Math.min(sim.WIDTH, myNewLoc.getY())),
      Math.max(0, Math.min(sim.WIDTH, myNewLoc.getZ()))
    );
    //Pass back clamping
    myNewLoc = new MutableDouble3D(location);
    //Update circles location
    sim.environment.setObjectLocation(this, location);
  }

}