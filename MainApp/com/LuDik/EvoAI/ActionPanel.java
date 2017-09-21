package com.LuDik.EvoAI;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ActionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// UI containers:
	private EvoAI mainFrame;
	private CameraPanel cameraPanel;
	private InfoPanel infoPanel;

	private Board board;
	private TimeKeeper timeKeeper;

	private static int APHeight;
	private static final int APWidth = 400;

	private Button startBoardBtn;
	private Button pauseBtn;
	
	private JSlider delaySlider;
	private JLabel delaySliderLbl;
	
	private TextField boardTileSizeTF;
	private TextField boardMapSizeInTilesTF;
	private TextField smoothnessTF;

	private boolean paused;

	public ActionPanel(EvoAI parent) {
		initActionPanel(parent);
	}

	private void initActionPanel(EvoAI parent) {
		APHeight = parent.getCameraPanel().getCPHEIGHT();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(APWidth, APHeight));

		mainFrame = parent;
		paused = false;

		boardTileSizeTF = new TextField("" + Configuration.DEFAULT_TILE_SIZE);

		boardMapSizeInTilesTF = new TextField("" + Configuration.DEFAULT_MAP_SIZE_IN_TILES);
		
		smoothnessTF = new TextField("" + Configuration.DEFAULT_SMOOTHNESS);
		
		delaySlider = new JSlider(0, 200, 25);
		delaySlider.setPaintTicks(true);
		delaySlider.setPaintLabels(true);
		delaySlider.setMajorTickSpacing(20);		
		
		delaySliderLbl = new JLabel("Delay: " + delaySlider.getValue() + " ms");

		startBoardBtn = new Button("Start board");

		pauseBtn = new Button("Paused: " + paused);
		pauseBtn.setEnabled(false);
		
		add(boardTileSizeTF);
		add(boardMapSizeInTilesTF);
		add(smoothnessTF);
		
		add(delaySliderLbl);
		add(delaySlider);
		
		add(startBoardBtn);
		add(pauseBtn);
		
		

		startBoardBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				infoPanel = mainFrame.getInfoPanel();
				
				if (timeKeeper != null) {
					timeKeeper = null;
				}
				
				try {
					board = new Board(
							Integer.valueOf(boardTileSizeTF.getText()),
							Integer.valueOf(boardMapSizeInTilesTF.getText()),
							Configuration.DEFAULT_SEED,
							Double.valueOf(smoothnessTF.getText()),
							mainFrame
							);
					
				} catch (NumberFormatException e) {
					board = new Board(
							Configuration.DEFAULT_TILE_SIZE,
							Configuration.DEFAULT_MAP_SIZE_IN_TILES,
							Configuration.DEFAULT_SEED,
							mainFrame
							);
					startBoardBtn.setLabel("Taking default values...");

				} 
				boardTileSizeTF.setText("" + Configuration.tileSize);
				boardMapSizeInTilesTF.setText("" + Configuration.mapSizeInTiles);
//				boardTileSizeTF.setEditable(false);
//				boardMapSizeInTilesTF.setEditable(false);
//				startBoardBtn.setEnabled(false);
				
				infoPanel.setBoard(board);
				
				mainFrame.getCameraPanel().update();
				
				board.spawnCreatures();
				mainFrame.getCameraPanel().update();
				
				timeKeeper = board.getTimeKeeper();				
				timeKeeper.start();
				
				pauseBtn.setEnabled(true);


			};
		});
		
		pauseBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				paused = !paused;
				
				synchronized(timeKeeper) {
					timeKeeper.setPaused(paused);
				}
				
				if(!paused) {
					synchronized(timeKeeper) {
						timeKeeper.notify();
					}
				}
				
				pauseBtn.setLabel("Paused: " + paused);
				
				
			};
		});
		
		delaySlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
			    if (source.getValueIsAdjusting()) {
			    	timeKeeper.setDelay((int) source.getValue());
			    	delaySliderLbl.setText("Delay: " + delaySlider.getValue() + " ms");
			    }
			}
			
		});
	}

	public TimeKeeper getTimeKeeper() {
		
		return timeKeeper;
	}

	public Board getBoard() {
		return board;
	}
}
