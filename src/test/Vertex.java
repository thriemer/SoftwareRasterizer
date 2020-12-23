package test;

import maths.Vec3;

import java.util.ArrayList;
import java.util.List;

public class Vertex {

	private static final int NO_INDEX = -1;

	private Vec3 position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private Vertex duplicateVertex = null;
	private int index;

	public Vertex(int index, Vec3 position) {
		this.index = index;
		this.position = position;
	}

	public int getIndex() {
		return index;
	}

	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public Vec3 getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
