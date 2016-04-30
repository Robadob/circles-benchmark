/**
 * 
 */
package circles3D;
import java.util.List;

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
	public class Vec3 {

		public final double x, y, z;
		Vec3(double x) {
	        this(x,x,x);
	    }
	    Vec3(double x, double y, double z) {
	        this.x = x;
	        this.y = y;
	        this.z = z;
	    }
	    Vec3(Vec3 a) {
	        this.x = a.x;
	        this.y = a.y;
	        this.z = a.z;
	    }
	    Vec3(NdPoint a) {
	        this.x = a.getX();
	        this.y = a.getY();
	        this.z = a.getZ();
	    }
	    public double[] getPoint() {
	    	return new double[]{this.x, this.y, this.z};
	    }
	    public Vec3 add(Vec3 vector) {
	        return new Vec3(x+vector.x, y+vector.y, z+vector.z);
	    }
	    public Vec3 subtract(Vec3 vector) {
	        return new Vec3(x-vector.x, y-vector.y, z-vector.z);
	    }
	    public Vec3 divide(double divisor) {
	        return new Vec3(x/divisor, y/divisor, z/divisor);
	    }
	    public Vec3 multiply(double divisor) {
	        return new Vec3(x*divisor, y*divisor, z*divisor);
	    }
	    public double length() {
	    	return Math.sqrt((this.x*this.x)+(this.y*this.y)+(this.z*this.z));
	    }

	}
	
	private ContinuousSpace <Particle> space ;
	private Grid <Particle> grid ;
	Vec3 myNewLoc;
	public static final double WIDTH = (double)RunEnvironment.getInstance().getParameters().getValue("WIDTH");
	public static final double DENSITY = (double)RunEnvironment.getInstance().getParameters().getValue("DENSITY");
	public static final double ATTRACTION_FORCE = (double)RunEnvironment.getInstance().getParameters().getValue("ATTRACTION_FORCE");
	public static final double REPELLING_FORCE = (double)RunEnvironment.getInstance().getParameters().getValue("REPELLING_FORCE");
	public static final double INTERACTION_RADIUS = (double)RunEnvironment.getInstance().getParameters().getValue("INTERACTION_RADIUS");
	public static final int GRID_DIM = (int)Math.ceil(WIDTH/INTERACTION_RADIUS);
	public static final int DIMENSIONS = 3;
	public Particle(ContinuousSpace <Particle> space , Grid <Particle> grid)
	{
		this.space = space;
		this.grid = grid;
	}
	
	@ScheduledMethod (start = 1 , interval = 1, priority = 1)
	public void step()
	{
		// get the grid location of this Particle
		GridPoint myGridLoc = grid.getLocation(this);
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood .
		GridCellNgh <Particle> nghCreator = new GridCellNgh <Particle>(grid, myGridLoc, Particle.class, 1, 1, 1);
		//Get Moore neighbourhood of cells
		List <GridCell<Particle>> gridCells = nghCreator.getNeighborhood(true);

		Vec3 myOldLoc = new Vec3(space.getLocation(this));
		myNewLoc = new Vec3(0);
		Vec3 neighbourLoc, locDiff;
	    double locDist, separation;
	    double k;
	    
	    //Iterate all particles within Moore neighbourhood
		for(GridCell<Particle> cell : gridCells) {
			for(Particle p : cell.items())
			{
				neighbourLoc = new Vec3(space.getLocation(p));
				locDiff = myOldLoc.subtract(neighbourLoc);
				locDist = locDiff.length();
				separation = locDist - INTERACTION_RADIUS;
				if(separation < INTERACTION_RADIUS)
				{
					if ( separation > 0)
						k = ATTRACTION_FORCE;
					else
						k = -REPELLING_FORCE;
					myNewLoc = myNewLoc.add(neighbourLoc.multiply(k*separation/INTERACTION_RADIUS));
				}
			}
		}
	}
	
	@ScheduledMethod (start = 1 , interval = 1, priority = 0)
	public void updateLocation()
	{
		//Update location
		//space.moveTo(this, myNewLoc.getPoint());//This throws an exception on out of bounds, despite use of StickyBorders (aka it's dumb)
		space.moveByDisplacement(this, myNewLoc.getPoint());
		//Update grid location
		grid.moveTo(this,
				(int)Math.max(0, Math.min(GRID_DIM-1, Math.floor(myNewLoc.x*GRID_DIM/WIDTH))), 
				(int)Math.max(0, Math.min(GRID_DIM-1, Math.floor(myNewLoc.y*GRID_DIM/WIDTH))),
				(int)Math.max(0, Math.min(GRID_DIM-1, Math.floor(myNewLoc.z*GRID_DIM/WIDTH)))
				);
	}
}
