package circles3D;

public class CircleParameters {

	public static final float ATTRACTION_FORCE = 0.1f;
	public static final float REPULSION_FORCE = 0.0f;
	public static final float RADIUS = 2.0f;
	public static final float WIDTH = 100;
	public static final float DENSITY = 0.004f;
	public static final int DIMENSIONS = 3;
	
	public static final int GRID_DIM = (int)Math.ceil(CircleParameters.WIDTH/CircleParameters.RADIUS);
}
