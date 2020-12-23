package graphicPipeline;

import java.util.ArrayList;
import java.util.List;

public class Mesh {

	float[][] storedData;
	int[] vboVectorDimension;
	int[] indices;
	int vectorCount;
	public Mesh(int vboCount){
		vboVectorDimension = new int[vboCount];
		storedData = new float[vboCount][];
		vectorCount=-1;
	}

	public void storeDataInAttribute(int attribute, int vectorDimension, float[] data) {
		storedData[attribute]=data;
		vboVectorDimension[attribute]=vectorDimension;
		if(vectorCount!=data.length/vectorDimension&&vectorCount!=-1){
			System.out.println("You have diffrently sized vector arrays in your model");
		}
		vectorCount=data.length/vectorDimension;
	}
	
	public void storeIndiecies(int[] data) {
		indices =data;
	}

	public int getAttributeSizeOfId(int id) {
		return vboVectorDimension[id];
	}
	public int getAttributeCount() {
		return vboVectorDimension.length;
	}
	public float[] getVector(int vboId, int position, float[] target) {
		int vecDim = vboVectorDimension[vboId];
		if(target == null){
			target=new float[vecDim];
		}
		int vboStartIndex = position*vecDim;
		System.arraycopy(storedData[vboId],vboStartIndex,target,0,vecDim);
		return target;
	}

	public int getIndicesCount(){
		return indices.length;
	}

	public int getVectorCount(){
		return vectorCount;
	}
}
