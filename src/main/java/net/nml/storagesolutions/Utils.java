package net.nml.storagesolutions;

public class Utils {
	static public int floorSlots(int slots) {
		int rows = calculateRows(slots);
		int columns = slots / rows;
		if (columns < 3)
			columns = 3;
		return rows * columns;
	}

	static public int calculateRows(int slots) {
		int rows;
		int columns;

		for (rows = 3; rows <= 6; rows++) {
			columns = slots / rows;
			if (columns <= rows * 3 + 3) {
				return rows;
			}
		}
		return slots / 18;
	}
}
