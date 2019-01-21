package maths;

public class Matrix {

	private static float[] buffer = new float[16];
	private static float[] buffer2 = new float[16];
	private static float[] buffer3 = new float[16];

	public static float[] createTransformationMatrix(float tx, float ty, float tz, float rx, float ry, float rz,
			float scale, float[] transformationMatrix) {
		setIdentity(transformationMatrix);
		
		createTranslationMatrix(tx, ty, tz, buffer);
		multiply4x4Matrix(transformationMatrix, buffer, transformationMatrix);
		
		createRotationMatrix(rx, ry, rz, buffer);
		multiply4x4Matrix(transformationMatrix, buffer, transformationMatrix);
		
		createScaleMatrix(scale, buffer);
		multiply4x4Matrix(transformationMatrix, buffer, transformationMatrix);
		
		return transformationMatrix;
	}

	public static float[] to4x4(float[] matrix, int rows) {
		float[] to4x4 = new float[16];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < rows; j++) {
				to4x4[i * 4 + j] = matrix[j];
			}
		}
		return to4x4;
	}

	public static float[] createProjectionMatrix(float FOV, float NEAR_PLANE, float FAR_PLANE, float aspectRatio) {
		float[] projectionMatrix = new float[16];
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix[0] = x_scale;
		projectionMatrix[5] = y_scale;
		projectionMatrix[10] = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix[14] = -1;
		projectionMatrix[11] = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix[15] = 0;
		return projectionMatrix;
	}

	public static float[] createTranslationMatrix(float x, float y, float z, float[] target) {
		setIdentity(target);
		target[3] = x;
		target[7] = y;
		target[11] = z;
		return target;
	}

	public static float[] createScaleMatrix(float scale, float[] target) {
		setIdentity(target);
		target[0] = scale;
		target[5] = scale;
		target[10] = scale;
		return target;
	}
	
	public static float[] createRotationMatrix(float x,float y, float z, float[] target) {
		setIdentity(target);
		createRotationMatrixY(y, buffer3);
		multiply4x4Matrix(target, buffer3, target);
		createRotationMatrixZ(z, buffer3);
		multiply4x4Matrix(target, buffer3, target);
		createRotationMatrixX(x, buffer3);
		multiply4x4Matrix(target, buffer3, target);
		return target;
	}

	public static float[] createRotationMatrixX(float angleInDegrees, float[] target) {
		setIdentity(target);
		double a = Math.toRadians(angleInDegrees);
		float s = (float) Math.sin(a);
		float c = (float) Math.cos(a);
		target[5] = c;
		target[6] = -s;
		target[9] = s;
		target[10] = c;
//		System.out.println(a+" "+c+" s"+" "+(-s));

		return target;
	}

	public static float[] createRotationMatrixY(float angleInDegrees, float[] target) {
		setIdentity(target);
		double a = Math.toRadians(angleInDegrees);
		float s = (float) Math.sin(a);
		float c = (float) Math.cos(a);
		target[0] = c;
		target[2] = s;
		target[8] = -s;
		target[10] = c;

		return target;
	}

	public static float[] createRotationMatrixZ(float angleInDegrees, float[] target) {
		setIdentity(target);
		double a = Math.toRadians(angleInDegrees);
		float s = (float) Math.sin(a);
		float c = (float) Math.cos(a);
		target[0] = c;
		target[1] = -s;
		target[4] = s;
		target[5] = c;

		return target;
	}

	public static float[] transformVektorWithMatrix(float matrix[], float vertex[]) {
		if (matrix.length % vertex.length != 0) {
			System.err.println("Matrix passt nicht zum Vektor");
			System.exit(1);
		}
		float[] newVec = new float[matrix.length / vertex.length];
		for (int offset = 0; offset < matrix.length / vertex.length; offset++) {
			newVec[offset] = 0;
			for (int i = 0; i < vertex.length; i++) {
				newVec[offset] += matrix[offset * vertex.length + i] * vertex[i];
			}
		}
		return newVec;
	}

	public static float[] multiplySquaredMatrix(float[] left, float[] right, int rows) {
		if (left.length % right.length != 0) {
			System.err.println("Matrix passt nicht zur Matrix");
			System.exit(1);
		}
		float[] newMatrix = new float[rows * rows];
		for (int offsetright = 0; offsetright < rows; offsetright++) {
			for (int offsetleft = 0; offsetleft < rows; offsetleft++) {
				newMatrix[offsetleft * rows + offsetright] = 0;
				for (int i = 0; i < rows; i++) {
					newMatrix[offsetleft * rows + offsetright] += left[offsetleft * rows + i]
							* right[i * rows + offsetright];
				}
			}
		}
		return newMatrix;
	}

	public static float[] multiply4x4Matrix(float[] left, float[] right, float[] target) {
		if (left.length % right.length != 0) {
			System.err.println("Matrix passt nicht zur Matrix");
			System.exit(1);
		}
		int rows = 4;

		for (int offsetright = 0; offsetright < rows; offsetright++) {
			for (int offsetleft = 0; offsetleft < rows; offsetleft++) {
				buffer2[offsetleft * rows + offsetright] = 0;
				for (int i = 0; i < rows; i++) {
					buffer2[offsetleft * rows + offsetright] += left[offsetleft * rows + i]
							* right[i * rows + offsetright];
				}
			}
		}
		for (int i = 0; i < target.length; i++) {
			target[i] = buffer2[i];
		}
		return target;
	}

	public static float[] addSquaredMatrix(float[] left, float[] right) {
		if (left.length != right.length) {
			System.err.println("Matritzen können nicht addiert werden, weil sie nicht gleich groß sind");
			System.exit(1);
		}
		float[] matrix = new float[left.length];
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = left[i] + right[i];
		}
		return matrix;
	}

	public static void setIdentity(float[] matrix) {
		if (matrix == null) {
			matrix = new float[16];
		}
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = 0;
		}
		matrix[0] = 1;
		matrix[5] = 1;
		matrix[10] = 1;
		matrix[15] = 1;
	}

}
