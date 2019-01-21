package graphicPipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Memory {

	private static List<float[]> floatDataList = new ArrayList<>();
	private static List<int[]> intDataList = new ArrayList<>();
	private static Map<Integer, Integer> vectorSize = new HashMap<>();

	public static int storeIntData(int[] data) {
		intDataList.add(data);
		return intDataList.size() - 1;
	}

	public static int[] getIntData(int index) {
		return intDataList.get(index);
	}

	public static int storeFloatData(int vectorsize, float[] data) {
		floatDataList.add(data);
		int index = floatDataList.size() - 1;
		vectorSize.put(index, vectorsize);
		return index;
	}

	public static float[] getFloatData(int index) {
		return floatDataList.get(index);
	}

	public static int getDatasizeOfVbo(int index) {
		return vectorSize.get(index);
	}
}
