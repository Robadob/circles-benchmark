package circles3D;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
//Logging
import java.nio.file.*;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.DefaultParameters;
import repast.simphony.parameter.ParameterSetter;
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

public class CirclesBatchRunner extends AbstractRunner {

	public static void main(String[] args){
		repast.simphony.random.RandomHelper.setSeed(0);
		File file = new File("Circles3D.rs/"); // the scenario dir
		if(!file.exists())
		{
			System.out.println("Scenario dir was not found");
		}
		CirclesBatchRunner runner = new CirclesBatchRunner();

		try {
			runner.load(file.getAbsoluteFile());     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        int ITERATIONS = 1000;
        String logFile = "results.txt";
        //Parse model configurations
        for(int i=0;i<args.length;i++)
        {
            String arg = args[i].toLowerCase();
            if (arg.equals("-width"))
            {
              i++;
              Particle.WIDTH=Float.parseFloat(args[i]);
              Particle.GRID_DIM = (int)Math.ceil(Particle.WIDTH/Particle.INTERACTION_RADIUS2);
            }
            else if (arg.equals("-density"))
            {
            	i++;
            	Particle.DENSITY=Float.parseFloat(args[i]);
            }
            else if (arg.equals("-radius"))
            {
            	i++;
            	Particle.INTERACTION_RADIUS=Float.parseFloat(args[i]);
            	Particle.INTERACTION_RADIUS2=2*Particle.INTERACTION_RADIUS;
            	Particle.GRID_DIM = (int)Math.ceil(Particle.WIDTH/Particle.INTERACTION_RADIUS2);
            }
            else if (arg.equals("-attract"))
            {
              i++;
              Particle.ATTRACTION_FORCE=Float.parseFloat(args[i]);
            }
            else if (arg.equals("-repel"))
            {
            	i++;
              	Particle.REPELLING_FORCE=Float.parseFloat(args[i]);
            }  
            else if (arg.equals("-file")||arg.equals("-f")||arg.equals("-out"))
            {
            	i++;
              	logFile = args[i];
            }
            else if(arg.equals("-for"))
            {
                i++;
                ITERATIONS = Integer.parseInt(args[i]);
            }
            else if (arg.equals("-load"))
            {
            	i++;
            	Particle.LOAD_AGENTS = true;
            	Particle.AGENTS_PATH = args[i];
            }
            else if (arg.equals("-agents"))
            {
            	Particle.LOG_AGENTS = true;
            }
        }
        
		runner.runInitialize();  // initialize the run

        //Log the start time
        long startTime = System.currentTimeMillis();
        
		// Run the sim for the specified number of iterations
		for(int i=0; i<ITERATIONS; i++){
			runner.step();  // execute all scheduled actions at next tick
		}
        //Log the end time
        long endTime = System.currentTimeMillis();
        runner.setFinishing(true);
        runner.schedule.executeEndActions();
        logTime(endTime-startTime, logFile);
		runner.stop();          // execute any actions scheduled at run end
		runner.cleanUpRun();
		runner.cleanUpBatch();    // run after all runs complete
		System.exit(0);		
	}
	public static void logTime(long millisOccured, String file)
	{
	    try {
	        Files.write(Paths.get(file), (""
	        +String.format(java.util.Locale.US,"%.6f", Particle.WIDTH)+","
	        +String.format(java.util.Locale.US,"%.6f", Particle.DENSITY)+","
	        +String.format(java.util.Locale.US,"%.6f", Particle.INTERACTION_RADIUS)+","
	        +String.format(java.util.Locale.US,"%.6f", Particle.ATTRACTION_FORCE)+","
	        +String.format(java.util.Locale.US,"%.6f", Particle.REPELLING_FORCE)+","
	        +(int)Math.round(Math.pow(Particle.WIDTH, Particle.DIMENSIONS) * Particle.DENSITY)+","
	        +String.format(java.util.Locale.US,"%.6f",millisOccured/1000.0f)+",s\r\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
	    }catch (java.io.IOException e) {
	      System.err.println(e.toString());
	      System.err.println("Err writing timing info to file.");
	    }
	    System.out.println("Results written to: "+file);
	}
	private static MessageCenter msgCenter = MessageCenter.getMessageCenter(CirclesBatchRunner.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected boolean pause = false;
	protected Object monitor = new Object();
	protected SweeperProducer producer;
	private ISchedule schedule;

	public CirclesBatchRunner() {
		runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this, true);
		controller = new DefaultController(runEnvironmentBuilder);
		controller.setScheduleRunner(this);
	}

	public void load(File scenarioDir) throws Exception{
		if (scenarioDir.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(scenarioDir);
			ControllerRegistry registry = loader.load(runEnvironmentBuilder);
			controller.setControllerRegistry(registry);
		} else {
			msgCenter.error("Scenario not found", new IllegalArgumentException(
					"Invalid scenario " + scenarioDir.getAbsolutePath()));
			return;
		}

		controller.batchInitialize();
		controller.runParameterSetters(null);
	}

	public void runInitialize(){
        DefaultParameters defaultParameters = new DefaultParameters(); 
        defaultParameters.addParameter("randomSeed", "Default Random Seed", Number.class, (int)System.currentTimeMillis(), false); 
        defaultParameters.addParameter("WIDTH", "WIDTH", Double.class, Particle.WIDTH, false); 
        defaultParameters.addParameter("DENSITY", "DENSITY", Double.class, Particle.DENSITY, false); 
        defaultParameters.addParameter("INTERACTION_RADIUS", "INTERACTION_RADIUS", Double.class, Particle.INTERACTION_RADIUS, false); 
        defaultParameters.addParameter("INTERACTION_RADIUS2", "INTERACTION_RADIUS2", Double.class, Particle.INTERACTION_RADIUS2, false); 
        defaultParameters.addParameter("ATTRACTION_FORCE", "ATTRACTION_FORCE", Double.class, Particle.ATTRACTION_FORCE, false); 
        defaultParameters.addParameter("REPELLING_FORCE", "REPELLING_FORCE", Double.class, Particle.REPELLING_FORCE, false); 
        controller.runParameterSetters(defaultParameters); 
    controller.runInitialize(defaultParameters);
		schedule = RunState.getInstance().getScheduleRegistry().getModelSchedule();
	}

	public void cleanUpRun(){
		controller.runCleanup();
	}
	public void cleanUpBatch(){
		controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime(){
		return ((Schedule)RunEnvironment.getInstance().getCurrentSchedule()).peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount(){
		return schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount(){
		return schedule.getActionCount();
	}

	// Step the schedule
	public void step(){
    schedule.execute();
	}

	// stop the schedule
	public void stop(){
		if ( schedule != null )
			schedule.executeEndActions();
	}

	public void setFinishing(boolean fin){
		schedule.setFinishing(fin);
	}

	public void execute(RunState toExecuteOn) {
		// required AbstractRunner stub.  We will control the
		//  schedule directly.
	}
}