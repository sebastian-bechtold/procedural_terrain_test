package main.java.com.sebastianbechtold.proceduralTerrainTest;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Droplet {

	public Vector3D mPos = new Vector3D(0,0,0);
	
	public Vector3D mDir = new Vector3D(0,0,0);
	
	public double vel = 1;
	public double mCapacity = 0;
	
	public double mWater = 1;
	public double mSediment = 0;
}
