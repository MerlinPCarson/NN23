import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Matrix2D {

	private double _11, _12, _13;
	private double _21, _22, _23;
	private double _31, _32, _33;
	
	
	Matrix2D(){
		identity(this);
	}
	
	public void identity(Matrix2D matrix){
		
		matrix._11 = 1; matrix._12 = 0; matrix._13 = 0;
		matrix._21 = 0; matrix._22 = 1; matrix._23 = 0;
		matrix._31 = 0; matrix._32 = 0; matrix._33 = 1;
		
	}
	
	public void translate(double x, double y){
		
		Matrix2D translateMatrix = new Matrix2D();
		
		translateMatrix._11 = 1; translateMatrix._12 = 0; translateMatrix._13 = 0;
		translateMatrix._21 = 0; translateMatrix._22 = 1; translateMatrix._23 = 0;
		translateMatrix._31 = x; translateMatrix._32 = y; translateMatrix._33 = 1;
		
		matrix_multiply(translateMatrix);
		
	}
	
	public void scale(double xScale, double yScale){
		
		Matrix2D scaleMatrix = new Matrix2D();
		
		scaleMatrix._11 = xScale; scaleMatrix._12 = 0; 		scaleMatrix._13 = 0;
		scaleMatrix._21 = 0;	  scaleMatrix._22 = yScale; scaleMatrix._23 = 0;
		scaleMatrix._31 = 0;      scaleMatrix._32 = 0; 		scaleMatrix._33 = 1;
		
		scaleMatrix = matrix_multiply(scaleMatrix);
		
	}
	
	public void rotate(double rotation){
		
		Matrix2D rotateMatrix = new Matrix2D();
		double sin = Math.sin(rotation);
		double cos = Math.cos(rotation);
		
		rotateMatrix._11 = cos; 	rotateMatrix._12 = sin; 	rotateMatrix._13 = 0;
		rotateMatrix._21 = -sin;	rotateMatrix._22 = cos; 	rotateMatrix._23 = 0;
		rotateMatrix._31 = 0;       rotateMatrix._32 = 0; 		rotateMatrix._33 = 1;
		
		matrix_multiply(rotateMatrix);
		
	}
	
	// points is an array list of ordered pairs, [0] = x, [1] = y
	public void transform(ArrayList<Point2D.Double> points){
		
		double newX, newY;
		
		for(Point2D.Double point: points){
			newX = (_11*point.x) + (_21*point.y) + _31;
			newY = (_12*point.x) + (_22*point.y) + _32;
			
			point.setLocation(newX, newY);
		
		}
		
	}
	
	public Matrix2D matrix_multiply(Matrix2D matrix){
		
		Matrix2D rtnMatrix = new Matrix2D();
		
		rtnMatrix._11 = (_11*matrix._11) + (_12*matrix._21) + (_13*matrix._31);
		rtnMatrix._12 = (_11*matrix._12) + (_12*matrix._22) + (_13*matrix._32); 
		rtnMatrix._13 = (_11*matrix._13) + (_12*matrix._23) + (_13*matrix._33);
		
		rtnMatrix._21 = (_21*matrix._11) + (_22*matrix._21) + (_23*matrix._31);
		rtnMatrix._22 = (_21*matrix._12) + (_22*matrix._22) + (_23*matrix._32); 
		rtnMatrix._23 = (_21*matrix._13) + (_22*matrix._23) + (_23*matrix._33);
		
		rtnMatrix._31 = (_31*matrix._11) + (_32*matrix._21) + (_33*matrix._31);
		rtnMatrix._32 = (_31*matrix._12) + (_32*matrix._22) + (_33*matrix._32); 
		rtnMatrix._33 = (_31*matrix._13) + (_32*matrix._23) + (_33*matrix._33);
		
		_11 = rtnMatrix._11; _12 = rtnMatrix._12; _13 = rtnMatrix._13;
		_21 = rtnMatrix._21; _22 = rtnMatrix._22; _23 = rtnMatrix._23;
		_31 = rtnMatrix._31; _32 = rtnMatrix._32; _33 = rtnMatrix._33;
	
		return rtnMatrix;
	}
	
	
}
