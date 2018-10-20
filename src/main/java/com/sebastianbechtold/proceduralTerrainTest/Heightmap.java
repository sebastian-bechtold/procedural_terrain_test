package main.java.com.sebastianbechtold.proceduralTerrainTest;

import java.awt.image.BufferedImage;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Heightmap {

	static final Vector3D up = new Vector3D(0, 0, 1);

	public float mRes = 1;

	double[][] mData = null;

	int mSizeX = 0;
	int mSizeY = 0;

	public Heightmap(int sizex, int sizey) {

		mSizeX = sizex;
		mSizeY = sizey;

		mData = new double[sizex][sizey];

	}

	public Heightmap(int sizex, int sizey, double initVal) {

		mSizeX = sizex;
		mSizeY = sizey;

		mData = new double[sizex][sizey];

		for (int x = 0; x < mSizeX; x++) {

			for (int y = 0; y < mSizeY; y++) {
				mData[x][y] = initVal;
			}
		}
	}

	public void add(int x, int y, float val) {
		mData[x][y] += val;
	}

	public double get(int x, int y) {
		return mData[x][y];
	}

	public double get(Vector3D v) {
		int ix = (int) Math.round(v.getX());
		int iy = (int) Math.round(v.getY());

		return mData[ix][iy];
	}

	public double get(double x, double y) {
		int ix = (int) Math.round(x);
		int iy = (int) Math.round(y);

		return mData[ix][iy];
	}

	public Vector3D getGradient(int x, int y) {

		// TODO 3: Better solution for this
		if (x < 1 || x >= mSizeX - 1) {
			return new Vector3D(0, 0, 0);
		}

		if (y < 1 || y >= mSizeY - 1) {
			return new Vector3D(0, 0, 0);
		}

		return new Vector3D(mData[x - 1][y] - mData[x + 1][y], mData[x][y - 1] - mData[x][y + 1], 2);
	}

	public BufferedImage getAsImage() {

		BufferedImage img = new BufferedImage(mSizeX, mSizeY, BufferedImage.TYPE_USHORT_GRAY);

		// ##################### BEGIN Render loop ######################
		for (int x = 0; x < mSizeX; x++) {

			for (int y = 0; y < mSizeY; y++) {

				float val = (float) get(x, y);

				// ########### BEGIN Clip to range ############
				if (val < 0) {
					val = 0;
				}

				if (val > 1) {
					val = 1;
				}
				// ########### END Clip to range ############

				// For 8 Bit RGB image:
				// Color col = new Color(val, val, val);
				// img.setRGB(x, y, col.getRGB());

				// For 16-Bit grayscale:
				int[] values = { (int) (val * 65535) };
				img.getRaster().setPixel(x, y, values);
			}
		}

		return img;
	}

	public float getRes() {
		return mRes;
	}

	double getSteepness(int x, int y) {

		return Math.acos(getGradient(x, y).dotProduct(up));
	}

	public void set(int x, int y, double val) {
		mData[x][y] = val;
	}

	public void set(Vector3D v, double val) {
		int ix = (int) Math.round(v.getX());
		int iy = (int) Math.round(v.getY());

		mData[ix][iy] = val;
	}

	public int sizeX() {
		return mSizeX;
	}

	public int sizeY() {
		return mSizeY;
	}

}
