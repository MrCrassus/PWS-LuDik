package com.LuDik.EvoAI;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Deze class houdt de staat van de map en de creatures bij.
 * 
 * @author Luuk
 *
 */

public class Board {

	private Map map;
	private ArrayList<LandTile> landTiles;
	private int mapLength;

	EvoAI mainFrame;

	private ArrayList<Creature> creatures;
	private ArrayList<Creature> tempList;
	private ArrayList<Creature> allCreaturesOfGeneration;
	private int generation;
	private ArrayList<Number> averageFitness;

	private int BEGIN_AMOUNT_CREATURES = Configuration.BEGIN_AMOUNT_CREATURES;
	private double CREATURE_SIZE = Configuration.DEFAULT_CREATURE_SIZE;
	private double EVOLUTION_FACTOR = Configuration.DEFAULT_EVOLUTION_FACTOR;
	private int RATIO_CHILDS_PER_PARENT = Configuration.RATIO_CHILDS_PER_PARENT;
	private int AMOUNT_OF_RANDOM_CREATURES_PER_GENERATION = Configuration.AMOUNT_OF_RANDOM_CREATURES_PER_GENERATION;
	private Integer tileSize;

	private Area landArea;
	private Area spawnArea;
	private TimeKeeper timeKeeper;
	private InfoPanel infoPanel;

	public Board(Integer tileSize, Integer mapSize, EvoAI evoAI) {
		evoAI.setBoard(this);

		map = new Map(tileSize, mapSize);

	}

	public Board(Integer tileSize, Integer mapSize, double seed, EvoAI evoAI) {
		evoAI.setBoard(this);

		this.tileSize = tileSize;

		map = new Map(tileSize, mapSize, seed);

	}

	public Board(Integer tileSize, Integer mapSize, double seed, double smoothness, EvoAI eAI) {
		eAI.setBoard(this);

		this.tileSize = tileSize;
		mainFrame = eAI;
		infoPanel = mainFrame.getInfoPanel();
		map = new Map(tileSize, mapSize, seed, smoothness);

		landTiles = map.getLandTiles();

		landArea = new Area();

		// System.out.println(map.getLandTiles());

		for (LandTile landTile : landTiles) {

			if (landTile.getTileRect() != null) {
				landArea.add(new Area(landTile.getTileRect()));

			}
		}

		timeKeeper = new TimeKeeper(this);
		timeKeeper.setInfoPanel(infoPanel);
		infoPanel.setTimeKeeper(timeKeeper);

	}

	public void spawnFirstCreatures() {

		creatures = new ArrayList<Creature>();
		allCreaturesOfGeneration = new ArrayList<Creature>();
		tempList = new ArrayList<Creature>();
		averageFitness = new ArrayList<Number>();
		// spawnArea = landArea;

		ArrayList<Point2D> spawnPoints = this.generateSpawnPoints();
		ArrayList<Point2D> availableSpawnPoints = this.generateSpawnPoints();
		int creaturesToSpawn = Math.min(BEGIN_AMOUNT_CREATURES, spawnPoints.size());

		for (int i = 0; i < creaturesToSpawn; i++) {
			Point2D point = availableSpawnPoints.get((int) ((availableSpawnPoints.size() - 1) * Math.random() + 0.5));
			Creature nextCreature = new Creature(point.getX(), point.getY(), this, i);
			creatures.add(nextCreature);
			allCreaturesOfGeneration.add(nextCreature);
			availableSpawnPoints.remove(point);
		}
		generation = 0;
		System.out.println("Generation: " + generation + " spawned!");
	}

	public void spawnCreatures() {

		ArrayList<Point2D> spawnPoints = this.generateSpawnPoints();
		ArrayList<Point2D> availableSpawnPoints = this.generateSpawnPoints();
		ArrayList<Creature> parentCreatures = new ArrayList<Creature>();
		ArrayList<Creature> newAllCreaturesOfGeneration = new ArrayList<Creature>();
		ArrayList<Creature> sortedCreaturesOfGeneration = new ArrayList<Creature>(infoPanel.getCreatures());

		int creaturesToSpawn = Math.min(BEGIN_AMOUNT_CREATURES, spawnPoints.size());

		for (int i = 0; i < creaturesToSpawn / RATIO_CHILDS_PER_PARENT; i++) {
			Creature parentCreature = sortedCreaturesOfGeneration.get(i);
			parentCreatures.add(parentCreature);
			sortedCreaturesOfGeneration.remove(parentCreature);
		}
		

		for (int i = 0; i < creaturesToSpawn / RATIO_CHILDS_PER_PARENT
				- AMOUNT_OF_RANDOM_CREATURES_PER_GENERATION; i++) {
			parentCreatures.get(i);
			for (int j = 0; j < 2; j++) {
				Point2D point = availableSpawnPoints
						.get((int) ((availableSpawnPoints.size() - 1) * Math.random() + 0.5));
				Creature nextCreature = new Creature(parentCreatures.get(i), point.getX(), point.getY(), generation,
						i, EVOLUTION_FACTOR);
				creatures.add(nextCreature);
				newAllCreaturesOfGeneration.add(nextCreature);
			}
		}
		
		
		allCreaturesOfGeneration.clear();
		for (int i = 0; i < newAllCreaturesOfGeneration.size(); i++) {
			allCreaturesOfGeneration.add(newAllCreaturesOfGeneration.get(i));
		}
		for (int i = newAllCreaturesOfGeneration.size(); i < creaturesToSpawn; i++) {
			allCreaturesOfGeneration.add(spawnRandomCreature(i));
		}

		generation++;
		System.out.println("Generation: " + generation + " spawned!");
	}

	public Creature spawnRandomCreature(int id) {
		ArrayList<Point2D> availableSpawnPoints = this.generateSpawnPoints();
		Point2D point = availableSpawnPoints.get((int) ((availableSpawnPoints.size() - 1) * Math.random() + 0.5));
		Creature randomCreature = new Creature(point.getX(), point.getY(), this, id);	
		return randomCreature;
	}

	private ArrayList<Point2D> generateSpawnPoints() {

		ArrayList<Point2D> spawnPoints = new ArrayList<Point2D>();

		spawnPoints.ensureCapacity(
				(int) (landTiles.size() * (tileSize / (CREATURE_SIZE + 2)) * (tileSize / (CREATURE_SIZE + 2))));
		for (LandTile landTile : landTiles) {
			for (int i = 0; i < tileSize / (CREATURE_SIZE + 2) - 1; i++) {
				for (int k = 0; k < tileSize / (CREATURE_SIZE + 2) - 1; k++) {
					spawnPoints
							.add(new Point2D.Double(landTile.getTileRect().getX() + (i + 0.5d) * ((CREATURE_SIZE + 2d)),
									landTile.getTileRect().getY() + (k + 0.5d) * ((CREATURE_SIZE + 2d))));
				}
			}
		}

		return spawnPoints;
	}

	public void updateStep() {

		if (creatures.size() == 0) {
			double averageFitness = 0;
			for (int i = 0; i < allCreaturesOfGeneration.size(); i++) {
				averageFitness += allCreaturesOfGeneration.get(i).getFitness();
			}
			averageFitness /= BEGIN_AMOUNT_CREATURES;

			infoPanel.setAverageFitnessOfPreviousGeneration(averageFitness);
			System.out.println("averageFitness: " + (int) averageFitness);

			this.averageFitness.add(generation, averageFitness);
			if (generation > 2) {
				double improvement = (double) this.averageFitness.get(generation)
						- (double) this.averageFitness.get(generation - 1);
				System.out.println("improvement: " + (int) improvement);
				improvement = (double) this.averageFitness.get(generation) - (double) this.averageFitness.get(0);
				System.out.println("Total improvement: " + (int) improvement);

			}

			map.refillLandTiles();

			this.spawnCreatures();
		}

		for (Creature crtr : creatures) {

			if (crtr.isControlled()) {
				crtr.doStep(mainFrame.getCameraPanel().getRcDeltaSpeed(),
						mainFrame.getCameraPanel().getRcDeltaDirection(), mainFrame.getCameraPanel().getRcFoodAmount());

			} else {
				crtr.doStep();

			}
			if (crtr.isDead()) {
				tempList.add(crtr);
			}
		}

		for (Creature crtr : tempList) {
			creatures.remove(crtr);
		}

		tempList.clear();

		for (Tile[] tileArray : map.getTiles()) {
			for (Tile tile : tileArray) {
				tile.calculateNextFood();
			}
		}

		mainFrame.getCameraPanel().update();
		// System.out.println("done");

	}

	public void drawBoard(Graphics2D g2d) {
		map.drawMap(g2d);

		if (creatures != null) {
			for (Creature crtr : new ArrayList<Creature>(creatures)) {
				if (crtr != null) {
					crtr.draw(g2d);
				} else {
					System.out.println(crtr);

				}
			}
		}
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public ArrayList<Creature> getCreatures() {
		return creatures;
	}

	public TimeKeeper getTimeKeeper() {
		return timeKeeper;
	}

	public void setTimeKeeper(TimeKeeper timeKeeper) {
		this.timeKeeper = timeKeeper;
	}

	public EvoAI getEvoAI() {
		return mainFrame;
	}

	public ArrayList<Creature> getAllCreaturesOfGeneration() {
		return allCreaturesOfGeneration;
	}

	public void setAllCreaturesOfGeneration(ArrayList<Creature> allCreaturesOfGeneration) {
		this.allCreaturesOfGeneration = allCreaturesOfGeneration;
	}

}
