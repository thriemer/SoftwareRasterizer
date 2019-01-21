/*
 * Decompiled with CFR 0_122.
 */
package maths;

public class Vec2 {
    public float x;
    public float y;

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Vec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void multiply(float s) {
        this.x *= s;
        this.y *= s;
    }

    public void setLerp(Vec2 a, Vec2 b, float p) {
        this.x = a.x + p * (b.x - a.x);
        this.y = a.y + p * (b.y - a.y);
    }

    @Override
	public String toString() {
        return "Vec2{x=" + this.x + ", y=" + this.y + '}';
    }
}

