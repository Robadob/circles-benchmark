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
 * This class represents a particle agents and holds the two runtime actions step and update
 */
public class Particle {
	
	private ContinuousSpace <Particle> space ;
	private Grid <Particle> grid ;
	private Vector3d displacement;
	private Vector3d myOldLoc;
	private GridPoint gridLoc;
	private GridPoint newGridLoc;
	public static double WIDTH = 100.0;
	public static double DENSITY = 0.01;
	public static double ATTRACTION_FORCE = 0.00001;
	public static double REPELLING_FORCE = 0.00001;
	public static double INTERACTION_RADIUS = 5.0;
	public static double INTERACTION_RADIUS2 = 2 * INTERACTION_RADIUS;
	public static int GRID_DIM = (int)Math.ceil(WIDTH/INTERACTION_RADIUS2);
	public static int DIMENSIONS = 3;
	public static boolean LOG_AGENTS = false;
	public static boolean LOAD_AGENTS = false;
	public static String AGENTS_PATH = "";
	public Vector3d getLoc()
	{
		return myOldLoc;
	}
	public Particle(ContinuousSpace <Particle> space , Grid <Particle> grid)
	{
		this.space = space;
		this.grid = grid;
	}
	public void init()
	{
		myOldLoc = new Vector3d(space.getLocation(this).toDoubleArray(null));
		gridLoc = grid.getLocation(this);
		newGridLoc = grid.getLocation(this);
	}
	//Don't schedule here, Schedule inside Particle Updater so we can do it multi-threaded
	//@ScheduledMethod (start = 1 , interval = 1, priority = 1)
	public void step()
	{
		// get the grid location of this Particle
		//GridPoint myGridLoc = grid.getLocation(this);
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood .
		GridCellNgh <Particle> nghCreator = new GridCellNgh <Particle>(grid, gridLoc, Particle.class, 1, 1, 1);
		//Get Moore neighbourhood of cells
		List <GridCell<Particle>> gridCells = nghCreator.getNeighborhood(true);

		displacement = new Vector3d(0,0,0);
		Vector3d toLoc;
	    double separation;
	    double k;
	    //Iterate all particles within Moore neighbourhood
		for(GridCell<Particle> cell : gridCells) {
			for(Particle p : cell.items())
			{
				toLoc = new Vector3d(space.getLocation(p).toDoubleArray(null));
				if(!myOldLoc.equals(toLoc))
				{
					toLoc.sub(myOldLoc);
					separation = toLoc.length();
					if(separation < INTERACTION_RADIUS2)
					{
			              k = (separation < INTERACTION_RADIUS) ? REPELLING_FORCE : ATTRACTION_FORCE;
			              if(separation < INTERACTION_RADIUS) toLoc.negate();
			              toLoc.scale(1.0/separation);//Normalize (without recalculating separation)
			              separation = (separation < INTERACTION_RADIUS) ? separation : (INTERACTION_RADIUS2 - separation);
			              toLoc.scale(k * separation);
			              displacement.add(toLoc);
					}
				}
			}
		}
		myOldLoc.add(displacement);
		myOldLoc.clampMin(0.0);
		myOldLoc.clampMax(WIDTH-1);
		//Calculate new grid location
		newGridLoc = new GridPoint((int)(myOldLoc.x/Particle.INTERACTION_RADIUS2),(int)(myOldLoc.y/Particle.INTERACTION_RADIUS2),(int)(myOldLoc.z/Particle.INTERACTION_RADIUS2));
	}
	//Update in serial, otherwise we break the grid
	@ScheduledMethod (start = 1 , interval = 1, priority = 0)
	public void updateLocation()
	{

		//space.moveTo(this, myOldLoc.getX(), myOldLoc.getY(), myOldLoc.getZ());//This throws an exception on out of bounds, despite use of StickyBorders (aka it's dumb)
		space.moveByDisplacement(this, displacement.x, displacement.y, displacement.z);//Sticky borders clamps this


        long startTime = System.nanoTime();
		//Update grid location if it has changed (myOldLoc is already updated in parallel)
		if(!newGridLoc.equals(gridLoc))
		{
			gridLoc = newGridLoc;
			grid.moveTo(this, gridLoc.getX(), gridLoc.getY(), gridLoc.getZ());
		}
	}
}
