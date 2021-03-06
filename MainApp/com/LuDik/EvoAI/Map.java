package com.LuDik.EvoAI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Map {

	private int mapSize;
	private int tileSize;
	private Tile[][] tiles;
	private double smoothness;
	private ArrayList<LandTile> landTiles;

	public Map(int tSize, int mapSizeInTiles) {

		tileSize = tSize;
		Configuration.tileSize = tileSize;

		mapSize = mapSizeInTiles;
		Configuration.mapSizeInTiles = mapSizeInTiles;
		tiles = new Tile[mapSize][mapSize];
		setLandTiles(new ArrayList<LandTile>());

		double randomValue;

		for (int i = 0; i < mapSize; i++) {

			for (int k = 0; k < mapSize; k++) {

				randomValue = (Math.random() * 1.333) - 0.333;

				if (randomValue >= 0) {
					tiles[i][k] = new LandTile(i * tileSize, k * tileSize, (float) randomValue);
					landTiles.add((LandTile) tiles[i][k]);
				} else {
					tiles[i][k] = new WaterTile(i * tileSize, k * tileSize);
				}
			}

		}

	}

	public Map(int tSize, int mapSizeInTiles, double seed) {
		if (seed <= 0) {
			seed = Math.random() * 255d;
		}
		
		tileSize = tSize;
		Configuration.tileSize = tileSize;

		mapSize = mapSizeInTiles;
		Configuration.mapSizeInTiles = mapSizeInTiles;
		tiles = new Tile[mapSize][mapSize];
		
		smoothness = Configuration.DEFAULT_SMOOTHNESS;
		
		
		
		double perlinSeededValue;
		double perlin;
				
		setLandTiles(new ArrayList<LandTile>());
		
		for (int i = 0; i < mapSize; i++) {

			for (int k = 0; k < mapSize; k++) {
				
				perlin = (float) 5f * ((float) ImprovedNoise.noise((float) i * smoothness, (float) k * smoothness, seed));
				perlin = (float) (1 / (2 + Math.expm1(-perlin)));
				perlinSeededValue = (perlin * (4d/3d)) - (1d/3d);
				
				if (perlinSeededValue >= 0) {
					tiles[i][k] = new LandTile(i * tileSize, k * tileSize, (float) perlinSeededValue);
					landTiles.add((LandTile) tiles[i][k]);
					
				} else {
					tiles[i][k] = new WaterTile(i * tileSize, k * tileSize);
				}
			}
		}

	}
		
		public Map(int tSize, int mapSizeInTiles, double seed, double smthnss) {
			if (seed <= 0) {
				seed = Math.random() * 255d;
			}

			tileSize = tSize;
			Configuration.tileSize = tileSize;
			
			mapSize = mapSizeInTiles;
			Configuration.mapSizeInTiles = mapSizeInTiles;
			tiles = new Tile[mapSize][mapSize];
			
			smoothness = smthnss;
			
			
			
			double perlinSeededValue;
			double perlin;
			
			setLandTiles(new ArrayList<LandTile>());
			
			for (int i = 0; i < mapSize; i++) {
				
				for (int k = 0; k < mapSize; k++) {
					
					perlin = (float) 5f * ((float) ImprovedNoise.noise((float) i * smoothness, (float) k * smoothness, seed));
					perlin = (float) (1 / (2 + Math.expm1(-perlin)));
					perlinSeededValue = (perlin * (4d/3d)) - (1d/3d);
					
					if (perlinSeededValue >= 0) {
						tiles[i][k] = new LandTile(i * tileSize, k * tileSize, (float) perlinSeededValue);
						landTiles.add((LandTile) tiles[i][k]);
					} else {
						tiles[i][k] = new WaterTile(i * tileSize, k * tileSize);
					}
				}
				
			}			
	}
		
		public void refillLandTiles() {
			for (LandTile lndTile : landTiles) {
				lndTile.refill();
			}
		}

	public Tile[][] getTiles() {
			return tiles;
		}

		public void setTiles(Tile[][] tiles) {
			this.tiles = tiles;
		}

	public void drawMap(Graphics2D g2d) {
		for (Tile[] tileArray : tiles) {
			for (Tile tile : tileArray) {
				tile.draw(g2d);
			}
		}
	}

	public ArrayList<LandTile> getLandTiles() {
		return landTiles;
	}

	public void setLandTiles(ArrayList<LandTile> landTiles) {
		this.landTiles = landTiles;
	}


}
