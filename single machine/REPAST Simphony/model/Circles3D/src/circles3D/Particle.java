/**
 * 
 */
package circles3D;
import java.util.List;

import javax.vecmath.Vector3d;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
/**
 * @author rob
 *
 */
public class Particle {
	
	private ContinuousSpace <Particle> space ;
	private Grid <Particle> grid ;
	private Vector3d myNewLoc;
	private Vector3d myOldLoc;
	public static double WIDTH = 100.0;
	public static double DENSITY = 0.01;
	public static double ATTRACTION_FORCE = 0.00001;
	public static double REPELLING_FORCE = 0.00001;
	public static double INTERACTION_RADIUS = 5.0;
	public static int GRID_DIM = (int)Math.ceil(WIDTH/INTERACTION_RADIUS);
	public static int DIMENSIONS = 3;
	public Particle(ContinuousSpace <Particle> space , Grid <Particle> grid)
	{
		this.space = space;
		this.grid = grid;
	}
	public void init()
	{
		myOldLoc = new Vector3d(space.getLocation(this).toDoubleArray(null));
		}
	//@ScheduledMethod (start = 1 , interval = 1, priority = 1)
	public void step()
	{
		// get the grid location of this Particle
		GridPoint myGridLoc = grid.getLocation(this);
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood .
		GridCellNgh <Particle> nghCreator = new GridCellNgh <Particle>(grid, myGridLoc, Particle.class, 1, 1, 1);
		//Get Moore neighbourhood of cells
		List <GridCell<Particle>> gridCells = nghCreator.getNeighborhood(true);

		myNewLoc = new Vector3d(0,0,0);
		Vector3d neighbourLoc, locDiff;
	    double locDist, separation;
	    double k;
	    int i = 0;
	    //Iterate all particles within Moore neighbourhood
		for(GridCell<Particle> cell : gridCells) {
			for(Particle p : cell.items())
			{
				i++;
				neighbourLoc = new Vector3d(space.getLocation(p).toDoubleArray(null));
				locDiff = new Vector3d(myOldLoc);//Copy by val
				locDiff.sub(neighbourLoc);
				locDist = locDiff.length();
				separation = locDist - INTERACTION_RADIUS;
				if(separation < INTERACTION_RADIUS)
				{
					if ( separation > 0)
						k = ATTRACTION_FORCE;
					else
						k = -REPELLING_FORCE;
					locDiff.scale(k*separation/INTERACTION_RADIUS);
					myNewLoc.add(locDiff);
				}
			}
		}
		myOldLoc.add(myNewLoc);
	}
	//Update in serial, otherwise we break the grid
	@ScheduledMethod (start = 1 , interval = 1, priority = 0)
	public void updateLocation()
	{
		//Update location
		//space.moveTo(this, myNewLoc.getPoint());//This throws an exception on out of bounds, despite use of StickyBorders (aka it's dumb)
		space.moveByDisplacement(this, myNewLoc.x, myNewLoc.y, myNewLoc.z);
		//Update grid location
		grid.moveTo(this,
				(int)Math.max(0, Math.min(GRID_DIM-1, Math.floor(myOldLoc.x*GRID_DIM/WIDTH))), 
				(int)Math.max(0, Math.min(GRID_DIM-1, Math.floor(myOldLoc.y*GRID_DIM/WIDTH))),
				(int)Math.max(0, Math.min(GRID_DIM-1, Math.floor(myOldLoc.z*GRID_DIM/WIDTH)))
				);
	}
}
