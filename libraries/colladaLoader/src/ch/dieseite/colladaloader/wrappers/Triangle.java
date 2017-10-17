package ch.dieseite.colladaloader.wrappers;

import java.io.Serializable;

import processing.core.PImage;

/**
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.0
 */
public class Triangle implements Serializable
{
     /**
     * the 3 Vertices of the Triangle
     */
    public Point3D A,B,C;
    /**
     * Texture-Points
     */
    public Point2D texA,texB,texC;
    /**
     * the Filename of the texture-Image
     */
    public String imageFileName;
    /**
     * says if the Triangle has Texture
     */
    public boolean containsTexture = false;
    /**
     * an object reference of a loaded image by Processing. Default is null.
     */
    public transient PImage imageReference;
    /**
     * the color of the Triangle if the Triangle has no texture
     */
     public Color colour;
 
}
