package main.java.com.sebastianbechtold.proceduralTerrainTest;

import java.util.Random;

public class PerlinGenerator {
	
	// TODO: Make this configurable
			static float mPersistance = 0.4f;//25f;


	static Random randomGenerator = new Random();

	static Heightmap makeNoise(int sizex, int sizey) {

		Heightmap result = new Heightmap(sizex, sizey);

		for (int x = 0; x < sizex; x++) {
			for (int y = 0; y < sizey; y++) {
				
				// NOTE: Heightmap max height will be 1
				double v = randomGenerator.nextDouble();

				result.set(x, y, v);
			}
		}

		return result;
	}

	static double linear(double start, double end, double coef) {
		return coef * (end - start) + start;
	}

	static double poly(double coef) {
		return 3 * coef * coef - 2 * coef * coef * coef;
	}

	static double Interpolate(double start, double end, double coef) {
		return linear(start, end, poly(coef));
	}

	static Heightmap makeSmoothNoise(Heightmap baseNoise, int octave) {


		Heightmap smoothNoise = new Heightmap(baseNoise.sizeX(), baseNoise.sizeY());

		int samplePeriod = 1 << octave; // calculates 2 ^ k
		float sampleFrequency = 1.0f / samplePeriod;

		for (int i = 0; i < baseNoise.sizeX(); i++) {
			// calculate the horizontal sampling indices
			int sample_i0 = (i / samplePeriod) * samplePeriod;
			int sample_i1 = (sample_i0 + samplePeriod) % baseNoise.sizeX(); // wrap around
			float horizontal_blend = (i - sample_i0) * sampleFrequency;

			for (int j = 0; j < baseNoise.sizeY(); j++) {
				// calculate the vertical sampling indices
				int sample_j0 = (j / samplePeriod) * samplePeriod;
				int sample_j1 = (sample_j0 + samplePeriod) % baseNoise.sizeY(); // wrap around
				float vertical_blend = (j - sample_j0) * sampleFrequency;

				// blend the top two corners
				double top = Interpolate(baseNoise.get(sample_i0, sample_j0), baseNoise.get(sample_i1, sample_j0), horizontal_blend);

				// blend the bottom two corners
				double bottom = Interpolate(baseNoise.get(sample_i0, sample_j1), baseNoise.get(sample_i1, sample_j1), horizontal_blend);

				// final blend
				smoothNoise.set(i, j, Interpolate(top, bottom, vertical_blend));
			}
		}

		return smoothNoise;
	}

	static public Heightmap makePerlinNoise(int sizex, int sizey, int octaveCount) {

		
		
		Heightmap baseNoise = makeNoise(sizex, sizey);
		
		Heightmap[] smoothNoise = new Heightmap[octaveCount];


		// generate smooth noise
		for (int i = 0; i < octaveCount; i++) {
			smoothNoise[i] = makeSmoothNoise(baseNoise, i);
		}

		Heightmap perlinNoise = new Heightmap(sizex, sizey);
		
		float amplitude = 1.0f;
		float totalAmplitude = 0.0f;

		// blend noise together
		for (int octave = octaveCount - 1; octave >= 0; octave--) {
			amplitude *= mPersistance;
			totalAmplitude += amplitude;

			for (int i = 0; i < perlinNoise.sizeX(); i++) {
				for (int j = 0; j < perlinNoise.sizeY(); j++) {
					perlinNoise.set(i,j, perlinNoise.get(i, j) + smoothNoise[octave].get(i,j) * amplitude);
				}
			}
		}

		// normalisation
		for (int i = 0; i < perlinNoise.sizeX(); i++) {
			for (int j = 0; j < perlinNoise.sizeY(); j++) {
				perlinNoise.set(i,j, perlinNoise.get(i, j) / totalAmplitude);
			}
		}

		return perlinNoise;
	}
}
