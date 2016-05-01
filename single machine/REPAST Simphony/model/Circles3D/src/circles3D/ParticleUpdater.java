package circles3D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
//Multi-threading based on the flocking example
//https://sourceforge.net/p/repast/repast-simphony-models/ci/GeoZombies/tree/Flock/src/flock/FlockUpdater.java
public class ParticleUpdater {

	ExecutorService executor;
	ExecutorCompletionService ecs;
	List<Callable> processRunner;
	//List<Callable> updateRunner;
	
	public ParticleUpdater(List<Particle> particleList){
		int cores = Runtime.getRuntime().availableProcessors();
		executor = Executors.newFixedThreadPool(cores+1);  // +1 is optimal
		ecs = new ExecutorCompletionService<>(executor);
		processRunner = new ArrayList<Callable>();
		//updateRunner = new ArrayList<Callable>();

		// Divide the list containing all Prey into sublists that will be
		//  assigned to individual runners.
		
		int stride =  (int)Math.max(1, Math.round((double)particleList.size() / (double)cores));

		while(!particleList.isEmpty()){
			List subList = new ArrayList();
			for (int i=0; i<stride; i++){
				if (particleList.size() > 0)
					subList.add(particleList.remove(0));
			}
			processRunner.add(new ParticleProcessRunner(subList));
			//updateRunner.add(new ParticleUpdateRunner(subList));
		}
		
		// Schedule the update method (cant do this multi threaded)
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters sp = ScheduleParameters.createRepeating(1,1,0); 
		//schedule.schedule(sp, this, "update");
		
		// Schedule the shutdown method
		sp = ScheduleParameters.createRepeating(1,1,1); 
		schedule.schedule(sp, this, "process");
	}
	public void process(){
		for (Callable runner : processRunner){
			ecs.submit(runner);
		}
		
		for (Callable runner : processRunner){
			try {
				ecs.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}/*
	public void update(){
		for (Callable runner : updateRunner){
			ecs.submit(runner);
		}
		
		for (Callable runner : updateRunner){
			try {
				ecs.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private class ParticleUpdateRunner implements Callable{

        private List<Particle> particleList;
        
        public ParticleUpdateRunner(List<Particle> particleList){
            this.particleList = particleList;
        }
        
        @Override
        public Object call() {
        	try
        	{
           for (Particle p : particleList){
        	   p.updateLocation();
           }
        	}catch(Exception e)
        	{
           		synchronized (System.out) {
        			System.out.println(e.toString());
        			e.printStackTrace();
        		}
        	}
           
           return null;  // no result
        }
    }	*/
	private class ParticleProcessRunner implements Callable{

        private List<Particle> particleList;
        
        public ParticleProcessRunner(List<Particle> particleList){
            this.particleList = particleList;
        }
        
        @Override
        public Object call() {
        	try
        	{
           for (Particle p : particleList){
        	   p.step();
           }
        	}catch(Exception e)
        	{
           		synchronized (System.out) {
        			System.out.println(e.toString());
        			e.printStackTrace();
        		}
        	}           
           return null;  // no result
        }
    }
	
}
