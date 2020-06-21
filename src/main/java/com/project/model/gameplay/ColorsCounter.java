package com.project.model.gameplay;

import java.util.HashMap;
import java.util.Map;

import com.project.model.bubble.BubbleColor;

public class ColorsCounter {

	private Map<BubbleColor, Integer> quantities;

	public ColorsCounter() {
		quantities = new HashMap<>();
		for (BubbleColor value : BubbleColor.values())
			quantities.put(value, 0);
	}

	public void increment(BubbleColor color) {
		int value = quantities.get(color);
		quantities.put(color, ++value);
	}

	public void decrement(BubbleColor color) {
		int value = quantities.get(color);
		quantities.put(color, --value);
	}
	
	public int getQuantity(BubbleColor color) {
		return quantities.get(color);
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		quantities.forEach((k, v) -> stringBuilder.append(k + " " + v + '\n'));
		stringBuilder.append('\n');
		return stringBuilder.toString();
	}

}