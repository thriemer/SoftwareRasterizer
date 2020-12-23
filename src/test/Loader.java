package test;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import graphicPipeline.Mesh;
import graphicPipeline.Texture;
import maths.Vec2;
import maths.Vec3;

public class Loader {

	private static final String RES_LOC = "/res/";

	public static Texture loadTexture(String name) {
		try {
			BufferedImage image = ImageIO.read(Loader.class.getResourceAsStream(RES_LOC + name + ".png"));
			return new Texture(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Mesh loadOBJ(String objFileName) {
		InputStreamReader isr = new InputStreamReader(Loader.class.getResourceAsStream(RES_LOC + objFileName + ".obj"));
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<>();
		List<Vec2> textures = new ArrayList<>();
		List<Vec3> normals = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vec3 vertex = new Vec3(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vec2 texture = new Vec2(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vec3 normal = new Vec3(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					break;
				}
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				Vertex v0 = processVertex(vertex1, vertices, indices);
				Vertex v1 = processVertex(vertex2, vertices, indices);
				Vertex v2 = processVertex(vertex3, vertices, indices);
				calculateTangents(v0, v1, v2, textures);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}
		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray
				);
		Mesh mesh = new Mesh(3);
		mesh.storeDataInAttribute(0, 3, verticesArray);
		mesh.storeDataInAttribute(1, 2, texturesArray);
		mesh.storeDataInAttribute(2, 3, normalsArray);
		mesh.storeIndiecies(indices.stream().mapToInt(i->i).toArray());
		return mesh;
	}

	private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2, List<Vec2> textures) {
		Vec3 delatPos1 = Vec3.sub(v1.getPosition(), v0.getPosition());
		Vec3 delatPos2 = Vec3.sub(v2.getPosition(), v0.getPosition());
		Vec2 uv0 = textures.get(v0.getTextureIndex());
		Vec2 uv1 = textures.get(v1.getTextureIndex());
		Vec2 uv2 = textures.get(v2.getTextureIndex());
		Vec2 deltaUv1 = Vec2.sub(uv1, uv0);
		Vec2 deltaUv2 = Vec2.sub(uv2, uv0);

		float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		delatPos1.scale(deltaUv2.y);
		delatPos2.scale(deltaUv1.y);
		Vec3 tangent = Vec3.sub(delatPos1, delatPos2);
		tangent.scale(r);
	}

	private static Vertex processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private static float[] convertIndicesListToArray(List<Integer> indices) {
		float[] indicesArray = new float[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vec2> textures, List<Vec3> normals,
			float[] verticesArray, float[] texturesArray, float[] normalsArray) {
		float furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			Vec3 position = currentVertex.getPosition();
			Vec2 textureCoord = textures.get(currentVertex.getTextureIndex());
			Vec3 normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		return furthestPoint;
	}

	private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex,
			List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices,
						vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}

		}
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

}
