package com.LuDik.EvoAI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class CameraPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static final int CPWIDTH = 1000;
	private static final int CPHEIGHT = 1000;

	private EvoAI mainFrame;

	private double cameraX = (CPWIDTH/2);
	private double cameraY = (CPWIDTH/2);
	private double scale = 1;
	
	private AffineTransform saveXform;
	private AffineTransform scaleT;
	private AffineTransform translateT;
		
	private static final double SCROLL_SPEED = 20;
	private static final double ZOOM_SPEED_IN = 1.1;
	private static final double ZOOM_SPEED_OUT = 1d/1.1;
	
	public CameraPanel(EvoAI parent) {
		mainFrame = parent;
		setBackground(Color.white);

		setPreferredSize(new Dimension(CPWIDTH, CPHEIGHT));
		setFocusable(true);
		addKeyListener(new KeyInputHandler());
		addMouseListener(new MouseInputHandler());
		
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);

	}

	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform saveXform = g2d.getTransform();
		
		g2d.setStroke(new BasicStroke(0.0001f));
		
		scaleT = new AffineTransform();
		translateT = new AffineTransform();
		
		scaleT.scale(scale, scale);
		translateT.translate(
				0.5 * CPWIDTH / scale - (cameraX),
				0.5 * CPWIDTH / scale - (cameraY));
				
		scaleT.concatenate(translateT);		
		g2d.setTransform(scaleT);	
		
		if (mainFrame.getBoard() != null) {
			mainFrame.getBoard().getMap().drawMap(g2d);
			
		}
		
		
		
		g2d.setTransform(saveXform);
		
//		debug lijnen, uncomment als je twee lijnen die door het midden van het scherm gaan wil hebben.
//		g2d.setColor(Color.green);
//		g2d.drawLine(0, CPWIDTH/2, CPWIDTH, CPWIDTH/2);
//		g2d.drawLine(CPWIDTH/2, 0, CPWIDTH/2, CPWIDTH);
		
		
		
		g2d.dispose();
		
	}
	
	private void moveCamera(int amplitudeX, int amplitudeY) {
		cameraX += amplitudeX * SCROLL_SPEED;
		cameraY += amplitudeY * SCROLL_SPEED;
	}
	
	private void zoomCamera(boolean zoomIn) {
		double currentScale;
		
		if (zoomIn) {
			currentScale = ZOOM_SPEED_IN;
		} else {
			currentScale = ZOOM_SPEED_OUT;
		}
		
		scale *= currentScale;		
	}
	
	class MouseInputHandler extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			requestFocusInWindow();
			System.out.println("mousepressed");
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			requestFocusInWindow();
			System.out.println("mouseEntered");
		}
		
		
		public void mouseDown(MouseWheelEvent e) {
			System.out.println("mouseScrolled");			
			repaint();
		}
	}

	class KeyInputHandler extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();

			switch (keyCode) {

			case KeyEvent.VK_LEFT:
				moveCamera(-1, 0);
				repaint();
				break;

			case KeyEvent.VK_RIGHT:
				moveCamera(1, 0);
				
				repaint();
				break;

			case KeyEvent.VK_DOWN:
				moveCamera(0, 1);
				
				repaint();
				break;

			case KeyEvent.VK_UP:
				moveCamera(0, -1);
				
				repaint();
				break;
				
			case KeyEvent.VK_ADD:
				zoomCamera(true);
				
				repaint();
				break;
				
			case KeyEvent.VK_SUBTRACT:
				zoomCamera(false);
				
				repaint();
				break;
			}
		}
	}

	public void update() {
		repaint();
	}

	public static int getCPWIDTH() {
		return CPWIDTH;
	}

	public static int getCPHEIGHT() {
		return CPHEIGHT;
	}

}
