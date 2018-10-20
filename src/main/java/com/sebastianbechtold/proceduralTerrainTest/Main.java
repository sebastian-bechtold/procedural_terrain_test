package main.java.com.sebastianbechtold.proceduralTerrainTest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) {

		System.out.print("Generating heightmap...");
		
		int size = 2048;
		
		Heightmap hm = PerlinGenerator.makePerlinNoise(size,size, 10);

		//hm = new Heightmap(512,512, 0.5);
		
		
		
		
		FluvialErosionTerrainChangerProcess etcp = new FluvialErosionTerrainChangerProcess(hm);
		ImpactTerrainChangerProcess itcp = new ImpactTerrainChangerProcess(hm);
		
		for(int step = 0; step < size*size*50; step++) {
			etcp.step();
			itcp.step();
		}
		
		
		System.out.println("finished.");
		
		BufferedImage img1 = hm.getAsImage();
		
		
		
		
		try {
			File outputfile1 = new File("heightmap.png");
			ImageIO.write(img1, "png", outputfile1);
			
			
		} catch (IOException e) {

		}

	}

}
