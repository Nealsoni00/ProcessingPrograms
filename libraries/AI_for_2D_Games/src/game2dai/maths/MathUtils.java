package game2dai.maths;


public class MathUtils {


	public static Integer getInteger(Object obj){
		if(obj == null) return null;
		try {
			Integer i = Integer.parseInt(obj.toString());
			return i;
		}
		catch(Exception e){
			return null;
		}
	}

	public static Float getFloat(Object obj){
		if(obj == null) return null;
		try {
			Float f = Float.parseFloat(obj.toString());
			return f;
		}
		catch(Exception e){
			return null;
		}
	}

	public static Double getDouble(Object obj){
		if(obj == null) return null;
		try {
			Double i = Double.parseDouble(obj.toString());
			return i;
		}
		catch(Exception e){
			return null;
		}
	}

	public static boolean isEqual(double a, double b){
		return (FastMath.abs(a-b) < 1.0E-12f) ? true : false;
	}
	
	public static boolean isEqual(double a, double b, double err){
		return (FastMath.abs(a-b) < err) ? true : false;
	}
	
	public static double randomClamped(){
		return (Math.random() - Math.random());
	}
	
	public static double randomInRange(double r0, double r1){
		return (r1 - r0) * Math.random() + r0;
	}
}
