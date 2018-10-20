package main.java.com.sebastianbechtold.proceduralTerrainTest;

import java.util.Random;

public class ImpactTerrainChangerProcess extends AbstractTerrainChangerProcess {

	static Random randomGenerator = new Random();
	
	int mStepCount = 0;
	
	public ImpactTerrainChangerProcess(Heightmap hm) {
		super(hm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void step() {
		// TODO Auto-generated method stub
		
		// TODO: Make crater count depend on map size
		if (mStepCount < 50000) {
			mStepCount++;
			return;
		}
		
		mStepCount = 0;
		
		//###################### BEGIN Crater code ###########################
		double crater = randomGenerator.nextDouble();

		int crater_radius = 0;

		if (crater > 0.5) {
			crater_radius = 5;
		}

		if (crater > 0.99) {
			crater_radius = 30;
		}

		if (crater > 0.998) {
			crater_radius = 80;
		}

		// crater_radius = -1;

		if (crater_radius > 0) {
			// System.out.println("boom!");

			int centerX = randomGenerator.nextInt(hm.mSizeX);
			int centerY = randomGenerator.nextInt(hm.mSizeY);

			int radius = randomGenerator.nextInt(crater_radius);

			double depth = 0.002;
			
			for (int x = -radius; x < radius; x++) {
				for (int y = -radius; y < radius; y++) {

					double dist = Math.sqrt(x * x + y * y);

					if (dist < radius) {

						double relDepth = (radius - dist) * depth + Math.max(depth, (depth / 10) * radius);

						try {
							double newDepth = hm.get(centerX + x, centerY + y) - relDepth;
							hm.set(centerX + x, centerY + y, newDepth);
						} catch (Exception e) {

						}
					}
				}
			}

		}
		//###################### BEGIN Crater code ###########################
	}

}
