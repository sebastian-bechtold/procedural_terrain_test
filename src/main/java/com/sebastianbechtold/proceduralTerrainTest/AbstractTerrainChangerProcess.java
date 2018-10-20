package main.java.com.sebastianbechtold.proceduralTerrainTest;

import java.util.Random;

public abstract class AbstractTerrainChangerProcess {

	Heightmap hm = null;
	Random randomGenerator = new Random();

	public AbstractTerrainChangerProcess(Heightmap hm) {

		this.hm = hm;
	}

	abstract public void step();
}
