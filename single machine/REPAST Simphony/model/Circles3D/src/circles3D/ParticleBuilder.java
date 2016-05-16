/**
 * 
 */
package circles3D;

import java.util.ArrayList;
import java.util.List;

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
public class ParticleBuilder implements ContextBuilder {
	
	@Override
	public Context build(Context context) {
		context.setId ("circles3D");
		ContinuousSpaceFactory spaceFactory =
		ContinuousSpaceFactoryFinder . createContinuousSpaceFactory(null);
		//Randomly scatter particles in space
		ContinuousSpace < Particle > space =
		spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Particle>(), new repast.simphony.space.continuous.StickyBorders(), Particle.WIDTH, Particle.WIDTH, Particle.WIDTH);
		
		//Calculate grid dimensions
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Particle> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Particle>(new repast.simphony.space.grid.StickyBorders(), new SimpleGridAdder<Particle>(), true , (int)Particle.GRID_DIM , (int)Particle.GRID_DIM, (int)Particle.GRID_DIM));
		int zombieCount = (int)(Math.pow(Particle.WIDTH, Particle.DIMENSIONS) * (double)Particle.DENSITY);
		//Create particles
		List<Particle> particleList = new ArrayList<Particle>();
		for ( int i = 0; i < zombieCount ; i ++) {
			particleList.add(new Particle(space, grid));
		}
		context.addAll(particleList);
		//Place them into grid
		for (Particle obj : particleList) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, 
					(int)Math.max(0, Math.min(Particle.GRID_DIM-1, Math.floor(pt.getX()*Particle.GRID_DIM/Particle.WIDTH))), 
					(int)Math.max(0, Math.min(Particle.GRID_DIM-1, Math.floor(pt.getY()*Particle.GRID_DIM/Particle.WIDTH))),
					(int)Math.max(0, Math.min(Particle.GRID_DIM-1, Math.floor(pt.getZ()*Particle.GRID_DIM/Particle.WIDTH)))
					);
			obj.init();
		}
		context.add(new ParticleUpdater(particleList));

		return context ;

	}

}
