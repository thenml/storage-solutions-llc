package net.nml.storagesolutions;

public class Utils {
	// there are 1253 items in vanilla minecraft
	// why tf would you need to store 4x that ammount in a single container
	public static final int MAX_SLOTS = 4095;

	static public int floorSlots(int slots) {
		int rows = calculateRows(slots);
		int columns = slots / rows;
		if (columns < 3)
			columns = 3;
		return Math.min(rows * columns, MAX_SLOTS);
	}

	static public int calculateRows(int slots) {
		int rows;
		int columns;

		for (rows = 3; rows <= 9; rows++) {
			columns = slots / rows;
			if (columns <= rows * 3 + 3 && columns <= 27) {
				return rows;
			}
		}
		return slots / 27;
	}
}
