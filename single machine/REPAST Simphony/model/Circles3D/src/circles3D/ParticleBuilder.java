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
 * This class creates particles initial states and adds them to the continuous space and grid data-structures
 */
public class ParticleBuilder implements ContextBuilder {

	public Context build(Context context) {
		context.setId ("circles3D");
		ContinuousSpaceFactory spaceFactory =
		ContinuousSpaceFactoryFinder . createContinuousSpaceFactory(null);
		ContinuousSpace < Particle > space;
		if(Particle.LOAD_AGENTS)
		{
			//Load particles from 'initAgents.txt' CSV
			space = spaceFactory.createContinuousSpace("space", context, new TextFileAdder<Particle>(Particle.AGENTS_PATH), new repast.simphony.space.continuous.StickyBorders(), Particle.WIDTH, Particle.WIDTH, Particle.WIDTH);
		}
		else
		{
			//Randomly scatter particles
			space = spaceFactory.createContinuousSpace("space", context, new RandomCartesianAdder<Particle>(), new repast.simphony.space.continuous.StickyBorders(), Particle.WIDTH, Particle.WIDTH, Particle.WIDTH);
			
		}
		//Calculate grid dimensions
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Particle> grid = gridFactory.createGrid("grid", context, new GridBuilderParameters<Particle>(new repast.simphony.space.grid.StickyBorders(), new SimpleGridAdder<Particle>(), true , (int)Particle.GRID_DIM , (int)Particle.GRID_DIM, (int)Particle.GRID_DIM));
		int zombieCount = (int)Math.round((float)(Math.pow(Particle.WIDTH, Particle.DIMENSIONS) * Particle.DENSITY));
		//Create particles
		List<Particle> particleList = new ArrayList<Particle>();
		for ( int i = 0; i < zombieCount ; i ++) {
			particleList.add(new Particle(space, grid));
		}
		context.addAll(particleList);
		//Place them into grid
		for (Particle obj : particleList) {
			NdPoint pt = space.getLocation(obj);
			grid.moveTo(obj, (int)(pt.getX()/Particle.INTERACTION_RADIUS2),(int)(pt.getY()/Particle.INTERACTION_RADIUS2),(int)(pt.getZ()/Particle.INTERACTION_RADIUS2));
			obj.init();
		}
		context.add(new ParticleUpdater(particleList));
		return context ;

	}

}
