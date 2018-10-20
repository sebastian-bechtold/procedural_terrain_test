package main.java.com.sebastianbechtold.proceduralTerrainTest;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class FluvialErosionTerrainChangerProcess extends AbstractTerrainChangerProcess {

	double[][] stamp = null;

	double erode_sum = 0;

	float sizeX = 0;
	float sizeY = 0;

	
	// Moon:
	/*
	 * float p_minSlope = 0.0001f; float p_capacity = 100; float p_deposition = 0.015f; // [0,1] float p_erosion = 0.1f; // [0,1] float p_inertia = 0.2f; //
	 * [0,1] float p_gravity = 1f; float p_evaporation = 0.2f; // [0,1] int p_radius = 7; int p_maxPath = 100; int p_numDroplets = 200000;
	 */

	Droplet d = new Droplet();

	// Earth:

	float p_minSlope = 0.0001f;
	float p_capacity = 100;
	float p_deposition = 0.01f; // [0,1]
	float p_erosion = 0.2f; // [0,1]
	float p_inertia = 0f; // [0,1]
	float p_gravity = 1f;
	float p_evaporation = 0.1f; // [0,1]
	int p_radius = 6;

	Vector3D zero = new Vector3D(0, 0, 0);

	public FluvialErosionTerrainChangerProcess(Heightmap hm) {
		super(hm);

		// ################ BEGIN Create erosion "stamp" ###################

		stamp = new double[p_radius * 2 + 1][p_radius * 2 + 1];

		for (int ex = 0; ex <= p_radius * 2; ex++) {
			for (int ey = 0; ey <= p_radius * 2; ey++) {

				double dist = Math.sqrt((ex - p_radius) * (ex - p_radius) + (ey - p_radius) * (ey - p_radius));

				erode_sum += Math.max(0, p_radius - dist);
			}
		}

		for (int ex = 0; ex <= p_radius * 2; ex++) {
			for (int ey = 0; ey <= p_radius * 2; ey++) {

				double dist = Math.sqrt((ex - p_radius) * (ex - p_radius) + (ey - p_radius) * (ey - p_radius));

				stamp[ex][ey] = Math.max(0, p_radius - dist) / erode_sum;

			}
		}
		// ################ END Create erosion "stamp" ###################

		sizeX = hm.sizeX() * hm.getRes();
		sizeY = hm.sizeY() * hm.getRes();

		// long timeStart = System.nanoTime();

		//
	}

	void startDroplet() {
		// System.out.println("New droplet!");

		double x = randomGenerator.nextDouble() * hm.sizeX();
		double y = randomGenerator.nextDouble() * hm.sizeY();

		d.mCapacity = 0;
		d.vel = 1;
		d.mWater = 1;
		d.mSediment = 0;
		d.mPos = new Vector3D(x, y, 0);
		d.mDir = new Vector3D(0, 0, 0);

	}

	@Override
	public void step() {

		int rx_old = (int) Math.round(d.mPos.getX());
		int ry_old = (int) Math.round(d.mPos.getY());

		Vector3D grad = hm.getGradient(rx_old, ry_old);

		// TODO 2: Better solution for this
		if (grad.getX() == 0 && grad.getY() == 0) {
			// System.out.println("Edge of map!");
			// grad = new Vector3D(randomGenerator.nextDouble(), randomGenerator.nextDouble(), 0).normalize();
			startDroplet();
			return;
		}

		// ######### BEGIN Geht #########
		Vector3D pos_old = d.mPos.add(zero);

		Vector3D gradient = new Vector3D(grad.getX(), grad.getY(), 0).normalize();

		Vector3D dir_new = d.mDir.scalarMultiply(p_inertia).add(gradient.scalarMultiply(1 - p_inertia));

		Vector3D pos_new = pos_old.add(dir_new);

		// ######### END Geht #########

		d.mDir = dir_new;
		d.mPos = pos_new;

		// TODO 2: Better solution for this
		if (pos_new.getX() < 0 || pos_new.getX() >= hm.sizeX() - 1 || pos_new.getY() < 0 || pos_new.getY() >= hm.sizeY() - 1) {

			startDroplet();
			return;

		}

		double h_old = hm.get(pos_old.getX(), pos_old.getY());
		double h_new = hm.get(pos_new.getX(), pos_new.getY());

		double h_diff = h_new - h_old;

		// ########### BEGIN Sedimentation ##############
		if (h_diff > 0) {

			double sediment = Math.min(h_diff, d.mSediment);

			d.mSediment -= sediment;
			hm.set(pos_old, hm.get(pos_old) + sediment);
		}
		// ########### END Sedimentation ##############

		// ############ BEGIN Erosion #############

		else {
			// System.out.println("update!");

			// Update droplet sediment carry capacity:
			d.mCapacity = Math.max(-h_diff, p_minSlope) * d.vel * d.mWater * p_capacity;

			// Deposit due to over-saturation:
			if (d.mSediment > d.mCapacity) {
				double sediment = (d.mSediment - d.mCapacity) * p_deposition;

				d.mSediment -= sediment;
				hm.set(pos_old, hm.get(pos_old) + sediment);
			}

			// Erosion due to under-saturation:
			else {
				// System.out.println("erosion!");
				double erosion = Math.min((d.mCapacity - d.mSediment) * p_erosion, -h_diff);

				d.mSediment += erosion;

				int rx = (int) Math.round(pos_old.getX());
				int ry = (int) Math.round(pos_old.getY());

				// ############ BEGIN Erosion radius loop #############
				for (int ex = -p_radius; ex <= p_radius; ex++) {
					for (int ey = -p_radius; ey <= p_radius; ey++) {

						// TODO 3: Cleaner solution for this?
						if (rx + ex < 0 || rx + ex >= hm.sizeX() || ry + ey < 0 || ry + ey >= hm.sizeY()) {
							continue;
						}

						double wi = stamp[ex + p_radius][ey + p_radius];

						hm.set(rx + ex, ry + ey, hm.get(rx + ex, ry + ey) - erosion * wi);
					}
				}
				// ############ END Erosion radius loop #############
			}
		}

		// Update droplet speed:
		d.vel = Math.sqrt(d.vel * d.vel + h_diff * p_gravity);

		// Update droplet water amount (evaporation):
		d.mWater = d.mWater * (1 - p_evaporation);

		if (d.mWater < 0.0001) {
			// System.out.println("Evaporate!");
			startDroplet();
			return;
		}
	}

}
