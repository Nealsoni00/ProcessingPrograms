package ch.dieseite.colladaloader.wrappers;

import java.io.Serializable;

/**
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.0
 */
public class Point2D implements Serializable
{
	/**
	 * according to Processing specifications
	 */
    public float x;
	/**
	 * according to Processing specifications
	 */
    public float y;

    public Point2D(float xx,float yy)
    {
        x=xx;
        y=yy;
    }


}
