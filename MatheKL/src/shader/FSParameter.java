package shader;

public class FSParameter {

	public float[] data;
	public String name;
	
	public FSParameter(String name, int dataLength) {
		this.name=name;
		data = new float[dataLength];
	}

	public float[] getDataInfluencedByCoords(float[] newData, double coord) {
		if (newData == null) {
			newData = new float[data.length];
		}
		for (int i = 0; i < newData.length; i++) {
			newData[i] = (float) (data[i] * coord);
		}
		return newData;
	}

}
