package maths;

public class Vec3 {

	public float x, y, z;

	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3(float[] coords) {
		this.x = coords[0];
		this.y = coords[1];
		this.z = coords[2];
	}

	public static Vec3 sub(Vec3 left, Vec3 right, Vec3 in) {
		if (in == null) {
			in = new Vec3(0, 0, 0);
		}
		in.x = left.x - right.x;
		in.y = left.y - right.y;
		in.z = left.z - right.z;
		return in;
	}

	public static Vec3 sub(Vec3 p1, Vec3 p2) {
		return new Vec3(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
	}

	public static float dot(Vec3 a, Vec3 b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}

	public void normalize() {
		float length = (float) Math.sqrt(x * x + y * y + z * z);
		x /= length;
		y /= length;
		z /= length;
	}

	public static Vec3 mul(Vec3 toScale, float scale, Vec3 in) {
		if (in == null) {
			in = new Vec3(0, 0, 0);
		}
		in.x = toScale.x * scale;
		in.y = toScale.y * scale;
		in.z = toScale.z * scale;
		return in;
	}

	public void scale(float s) {
		x = x * s;
		y = y * s;
		z = z * s;
	}
}
