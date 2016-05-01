package repast.simphony.demo.simple;

import java.io.File;

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
import repast.simphony.parameter.SweeperProducer;
import simphony.util.messages.MessageCenter;

public class CirclesBatchRunner extends AbstractRunner {

	public static void main(String[] args){

		File file = new File("Circles3D"); // the scenario dir

		CirclesBatchRunner runner = new CirclesBatchRunner();

		try {
			runner.load(file);     // load the repast scenario
		} catch (Exception e) {
			e.printStackTrace();
		}
        int i ITERATIONS = 1000;
        //Parse model configurations
        for(int i=0;i<args.length;i++)
        {
          String arg = args[i].toLowerCase();
          if (arg.equals("-width"))
          {
            i++;
            Circles3D.WIDTH = Float.parseFloat(args[i]);
            j+=2;
          }
          else if (arg.equals("-density"))
          {
            i++;
            Circles3D.DENSITY = Float.parseFloat(args[i]);
            j+=2;
          }
          else if (arg.equals("-radius"))
          {
            i++;
            Circles3D.INTERACTION_RADIUS = Float.parseFloat(args[i]);
            j+=2;
          }
          else if (arg.equals("-attract"))
          {
            i++;
            Circles3D.ATTRACTION_FORCE = Float.parseFloat(args[i]);
            j+=2;
          }
          else if (arg.equals("-repel"))
          {
            i++;
            Circles3D.REPULSION_FORCE = Float.parseFloat(args[i]);
            j+=2;
          }
          else if (arg.equals("-for"))
          {
            i++;
            ITERATIONS = Integer.parseInt(args[i]);
          }
          else
          {
            break;
          }
            
        }
        
//		double endTime = 1000.0;  // some arbitrary end time

		// Run the sim a few times to check for cleanup and init issues.
		for(int i=0; i<2; i++){

			runner.runInitialize();  // initialize the run

//			RunEnvironment.getInstance().endAt(endTime);

			while (runner.getActionCount() > 0){  // loop until last action is left
				if (runner.getModelActionCount() == 0) {
					runner.setFinishing(true);
				}
				runner.step();  // execute all scheduled actions at next tick

			}

			runner.stop();          // execute any actions scheduled at run end
			runner.cleanUpRun();
		}
		runner.cleanUpBatch();    // run after all runs complete
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
    controller.runInitialize(null);
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