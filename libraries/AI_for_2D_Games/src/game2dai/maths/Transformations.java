package game2dai.maths;

import java.util.List;


public class Transformations {


	//--------------------------- WorldTransform -----------------------------
	//
	//given a List of 2D vectors, a position and  orientation
	// forward and side should be normalised before calling this method
	//this function transforms the 2D vectors into the object's world space
	//------------------------------------------------------------------------

	public static List<Vector2D> worldTransform(final List<Vector2D> points,
			Vector2D   pos,
			Vector2D   forward,
			Vector2D   side,
			Vector2D   scale){

		//create a transformation matrix
		Matrix2D matTransform = new Matrix2D();

//		forward.normalize();
//		side.normalize();

		//scale
		if ( (scale.x != 1.0) || (scale.y != 1.0) )	
			matTransform.scale(scale.x, scale.y);
		//rotate
		matTransform.rotate(forward, side);
		//and translate
		matTransform.translate(pos.x, pos.y);

		//now transform the object's vertices
		return matTransform.transformVector2D(points);
	}

	public static List<Vector2D> worldTransform(final List<Vector2D> points,
			Vector2D   pos,
			Vector2D   forward,
			Vector2D   side){
		return worldTransform(points, pos, forward, side, Vector2D.ONE);
	}


	//--------------------- PointToWorldSpace --------------------------------
	// agentHeading and agentSide should be normalised first
	//Transforms a point from the agent's local space into world space
	//------------------------------------------------------------------------
	public static Vector2D pointToWorldSpace(Vector2D point,
			Vector2D AgentHeading,
			Vector2D AgentSide,
			Vector2D AgentPosition)
	{
		
//		AgentHeading.normalize();
//		AgentSide.normalize();
		
		//create a transformation matrix
		Matrix2D matTransform = new Matrix2D();

		//rotate
		matTransform.rotate(AgentHeading, AgentSide);
		//matTransform.rotate(Vector2D.normalize(AgentHeading), Vector2D.normalize(AgentSide));

		//and translate
		matTransform.translate(AgentPosition.x, AgentPosition.y);

		//now transform the vertices
		return matTransform.transformVector2D(point);
	}


	//--------------------- VectorToWorldSpace --------------------------------
	//
	//Transforms a vector from the agent's local space into world space
	//------------------------------------------------------------------------
	public static Vector2D vectorToWorldSpace(Vector2D vec,	Vector2D AgentHeading,
			Vector2D AgentSide) {
//		AgentHeading.normalize();
//		AgentSide.normalize();
		
		//create a transformation matrix
		Matrix2D matTransform = new Matrix2D();

		//rotate
		matTransform.rotate(AgentHeading, AgentSide);

		//now transform and return the vertices
		return matTransform.transformVector2D(vec);
	}


	//--------------------- PointToLocalSpace --------------------------------
	// agentHeading and agentSide should be normalised
	//------------------------------------------------------------------------
	public static Vector2D pointToLocalSpace( Vector2D point,
			Vector2D agentHeading,
			Vector2D agentSide,
			Vector2D agentPosition)
	{
		//create a transformation matrix
		Matrix2D matTransform = new Matrix2D();

//		agentHeading.normalize();
//		agentSide.normalize();

		double tx = -agentPosition.dot(agentHeading);
		double ty = -agentPosition.dot(agentSide);

		//create the transformation matrix
		matTransform._11(agentHeading.x); 	matTransform._12(agentSide.x);
		matTransform._21(agentHeading.y); 	matTransform._22(agentSide.y);
		matTransform._31(tx);           	matTransform._32(ty);

		//now transform the vertices
		return matTransform.transformVector2D(point);
	}

	//--------------------- VectorToLocalSpace --------------------------------
	//
	//------------------------------------------------------------------------
	public static Vector2D vectorToLocalSpace(Vector2D vec,
			Vector2D AgentHeading,
			Vector2D AgentSide)
	{ 
//		AgentHeading.normalize();
//		AgentSide.normalize();

		//create a transformation matrix
		Matrix2D matTransform = new Matrix2D();

		//create the transformation matrix
		matTransform._11(AgentHeading.x); matTransform._12(AgentSide.x);
		matTransform._21(AgentHeading.y); matTransform._22(AgentSide.y);

		//now transform the vertices
		return matTransform.transformVector2D(vec);
	}

	//-------------------------- Vec2DRotateAroundOrigin --------------------------
	// v is unchanged
	//rotates a vector ang rads around the origin
	//-----------------------------------------------------------------------------
	public static Vector2D vec2DRotateAroundOrigin(Vector2D v, double ang)
	{
		//create a transformation matrix
		Matrix2D mat = new Matrix2D();;

		//rotate
		mat.rotate(ang);

		//now transform the object's vertices
		return mat.transformVector2D(v);
	}

}
