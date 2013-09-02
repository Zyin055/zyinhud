package zyin.zyinhud.util;

public class Vec2
{
    double x;
    double y;

    public Vec2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /*public double signedAngle(Vec2 v2)
    {
          double perpDot = perpDot(this, v2);
          return Math.atan2(perpDot, this.dot(v2));
    }
    public double perpDot(Vec2 v1, Vec2 v2)
    {
    	return v1.x * v2.y - v1.y * v2.x;
    }*/
    public double dot(Vec2 v)
    {
        return this.x * v.x + this.y * v.y;
    }
    public double length()
    {
        return Math.sqrt(x * x + y * y);
    }
    /*public double cross(Vec2 v)
    {
    	return this.x*v.y - this.y*v.x;
    }*/

}
