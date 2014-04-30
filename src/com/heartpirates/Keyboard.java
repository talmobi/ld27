package com.heartpirates;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.List;

public class Keyboard implements KeyListener {

	protected boolean[] keys = new boolean[1024];

	public final Key UP = new Key(KeyEvent.VK_UP, KeyEvent.VK_W);
	public final Key DOWN = new Key(KeyEvent.VK_DOWN, KeyEvent.VK_S);
	public final Key LEFT = new Key(KeyEvent.VK_LEFT, KeyEvent.VK_A);
	public final Key RIGHT = new Key(KeyEvent.VK_RIGHT, KeyEvent.VK_D);

	public final Key SPACE = new Key(KeyEvent.VK_SPACE);

	public final Key VK_1 = new Key(KeyEvent.VK_1, KeyEvent.VK_NUMPAD1);
	public final Key VK_2 = new Key(KeyEvent.VK_2, KeyEvent.VK_NUMPAD2);
	public final Key VK_3 = new Key(KeyEvent.VK_3, KeyEvent.VK_NUMPAD3);

	public final Key VK_Q = new Key(KeyEvent.VK_Q);
	public final Key VK_E = new Key(KeyEvent.VK_E);

	class Key {
		private List<Integer> keysList = new LinkedList<Integer>();

		Key(int... keys) {
			for (int k : keys) {
				this.keysList.add(k);
			}
		}

		public boolean isPressed() {
			for (int key : this.keysList) {
				if (keys[key])
					return true;
			}
			return false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

}