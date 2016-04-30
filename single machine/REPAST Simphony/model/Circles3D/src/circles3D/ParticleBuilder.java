/**
 * 
 */
package circles3D;

import repast.simphony.context.Context;
import repast.simphony.context.space.continuous.ContinuousSpaceFactory;
import repast.simphony.context.space.continuous.ContinuousSpaceFactoryFinder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

/**
 * @author rob
 *
 */
public class ParticleBuilder implements ContextBuilder<Particle> {
	
	@Override
	public Context build(Context<Particle> context) {
		context.setId ("circles3D");
try
{
	throw new RuntimeException("blah");
	
}
catch(Exception e)
{
	e.printStackTrace();
}
		ContinuousSpaceFactory spaceFactory =
		ContinuousSpaceFactoryFinder . createContinuousSpaceFactory(null);
		//Randomly scatter particles in space
		ContinuousSpace < Particle > space =
		spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Particle>(), new repast.simphony.space.continuous.StickyBorders(), Particle.WIDTH, Particle.WIDTH, Particle.WIDTH);
		
		//Calculate grid dimensions
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Particle> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Particle>(new repast.simphony.space.grid.StickyBorders(), new SimpleGridAdder<Particle>(), true , (int)Particle.GRID_DIM , (int)Particle.GRID_DIM, (int)Particle.GRID_DIM));
		int zombieCount = (int)(Math.pow(Particle.WIDTH, Particle.DIMENSIONS) * Particle.DENSITY);
		//Create particles
		for ( int i = 0; i < zombieCount ; i ++) {
			context.add(new Particle(space, grid));
		}
		//Place them into grid
		for (Particle obj : context) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, 
					(int)Math.max(0, Math.min(Particle.GRID_DIM-1, Math.floor(pt.getX()*Particle.GRID_DIM/Particle.WIDTH))), 
					(int)Math.max(0, Math.min(Particle.GRID_DIM-1, Math.floor(pt.getY()*Particle.GRID_DIM/Particle.WIDTH))),
					(int)Math.max(0, Math.min(Particle.GRID_DIM-1, Math.floor(pt.getZ()*Particle.GRID_DIM/Particle.WIDTH)))
					);
		}

		return context ;

	}

}
