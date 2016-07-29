package circles3D;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.vecmath.Vector3d;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.Dimensions;
import repast.simphony.space.continuous.ContinuousAdder;
import repast.simphony.space.continuous.ContinuousSpace;

public class TextFileAdder<T> implements ContinuousAdder<T> {

	Vector3d locations[];
	int index = 0;
	int count = 0;
	boolean finished = false;
	TextFileAdder(String file)
	{
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line = br.readLine();
			count = Integer.parseInt(line);
			locations = new Vector3d[count];
			for(int i = 0;i<count;i++)
			{
		        line = br.readLine();
		        String parts[] = line.split(",");
		        locations[i]=new Vector3d(
		        		Double.parseDouble(parts[0]),
		        		Double.parseDouble(parts[1]),
		        		Double.parseDouble(parts[2])
		        		);
			}
		}
		catch(Exception e)
		{
			System.out.println(file);
			e.printStackTrace();
			System.out.println("Unable to load agent data from file. Exiting.");
			System.exit(1);
		}
	}
	/**
	 * Adds the specified object to the space at a random location.
	 * 
	 * @param space
	 *            the space to add the object to.
	 * @param obj
	 *            the object to add.
	 */
	public void add(ContinuousSpace<T> space, T obj) {
		Dimensions dims = space.getDimensions();
		double[] location = new double[dims.size()];
		if(obj.getClass()!=ParticleUpdater.class)
			findLocation(location);
		while (!space.moveTo(obj, location)) {
			System.out.println("Retrying setLocation");
			//findLocation(location);
		}
	}

	private void findLocation(double[] location) {
		if(finished)
		{
			//finished=false;
			System.out.println("More agents than file provides.");
		}
		location[0]=locations[index].x;
		location[1]=locations[index].y;
		location[2]=locations[index].z;
		index++;
		if(index>=count)
		{
			index=0;
			finished=true;
		}
	}
}
