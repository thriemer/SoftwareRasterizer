package graphicPipeline;

import java.util.ArrayList;
import java.util.List;

public class Mesh {

	public int indiciesIndex;


	List<Integer> vboIds = new ArrayList<>();

	public void storeDataInAttribute(int atribute, int vectorsize, float[] data) {
		if (atribute<vboIds.size()&&vboIds.get(atribute) != null) {
			vboIds.remove(atribute);
		}
		int vboId = Memory.storeFloatData(vectorsize, data);
		vboIds.add(atribute, vboId);
	}
	
	public void storeIndiecies(int attribute, float[] data) {
		indiciesIndex = attribute;
		storeDataInAttribute(attribute, 1, data);
	}

	public int getAttributeSizeOfId(int id) {
		return Memory.getDatasizeOfVbo(vboIds.get(id));
	}

	public float[] getData(int id) {
		return Memory.getFloatData(vboIds.get(id));
	}
	
	public int getAttributeCountWithoutIndicies() {
		return vboIds.size()-1;
	}
	
}
