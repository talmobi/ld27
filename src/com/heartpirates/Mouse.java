package com.heartpirates;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

public class Mouse implements MouseInputListener {

	private final Factory factory;
	boolean[] mbuttons = new boolean[5];

	private int mouseX = 0;
	private int mouseY = 0;
	
	public final int MLEFT = 1;
	public final int MMID = 2;
	public final int MRIGHT = 3;

	public Mouse(Factory factory) {
		this.factory = factory;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		updateMousePosition(e);
		int b = e.getButton();
		System.out.println("PRESSED: " + b);
		if (b > 0 && b <= 3)
			mbuttons[b] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		updateMousePosition(e);
		int b = e.getButton();
		if (b > 0 && b <= 3)
			mbuttons[b] = false;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		updateMousePosition(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateMousePosition(e);
	}

	private void updateMousePosition(MouseEvent e) {
		mouseX = e.getX() / Main.SCALE - factory.getCamera().getX();
		mouseY = e.getY() /  Main.SCALE - factory.getCamera().getY();
	}

	public int getX() {
		return mouseX;
	}
	
	public int getY() {
		return mouseY;
	}

}