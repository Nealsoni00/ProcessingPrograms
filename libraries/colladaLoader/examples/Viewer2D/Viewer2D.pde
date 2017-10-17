import java.util.Properties;

import ch.dieseite.colladaloader.ColladaLoader;
import ch.dieseite.colladaloader.wrappers.ColladaModel;
import ch.dieseite.colladaloader.wrappers.Triangle;


/**
 * <p>This class is designed for tests only. It's not a required part
 * of that package. Same code can be found at example pde</p>
 * 
 * <p>The Idea of this code is a presentation of 3D 
 * in an non GL Environment because some platforms don't support P2D or P3D
 * of Processing. Note that 2D has no Textures</p>
 *
 * <p>This source is free; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License and by nameing of the originally author</p>
 *
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 3.1
 */


    //****************************
    //choose here which example you want (one ore more)

    boolean modelsEnabled[] = new boolean[]{false,false,false,false,false,false,true};

    //****************************

    ColladaModel[] models = new ColladaModel[modelsEnabled.length];
      
    void settings() 
    {
          size(500,500); //switch off GL 
    }
      
    void setup()
    {
          frameRate(10);
          
          Properties optionals = new Properties();
          optionals.setProperty("option_no_texture", "true"); //spares image loadings
          
          //SketchUp formated files:
          //pure complex dae file without textures
          if (modelsEnabled[0]) models[0] = ColladaLoader.load("sketchup/truck.dae",this,optionals);
          // a beer can with textures, basic shapes and lines
          if (modelsEnabled[1]) models[1] = ColladaLoader.load("sketchup/duff.kmz",this,optionals);
          //default sketchupmodel with transparent textures and colors
          if (modelsEnabled[2]) models[2] = ColladaLoader.load("sketchup/ghostly_susan_at_glasswall.kmz",this,optionals);
          //a kmz just with one triangle, one texture and three lines
          if (modelsEnabled[3]) models[3] = ColladaLoader.load("sketchup/simpleshapes.kmz",this,optionals);
         
          optionals.setProperty("LinkingSchema", "Blender"); //blender parsing profile
//          optionals.setProperty("debuglevel", "4"); //4 = extreme details 3 = details, 2 = medium, 1 = abstract , 0 = none (default)

         //Blender formated file (to see it: scale bigger)
         //a transparent skull (triangles only)
          if (modelsEnabled[4]) models[4] = ColladaLoader.load("blender/halloween_crystal.dae",this,optionals);
          //same skull but textured (triangles only)
          if (modelsEnabled[5]) models[5] = ColladaLoader.load("blender/halloween_textured.dae",this,optionals);
          //same skull as grid (lines only)
          if (modelsEnabled[6]) models[6] = ColladaLoader.load("blender/halloween_grid_only.dae",this,optionals);
         
      
    }

    void draw(){
          
          background(200);
          textSize(20);
          text("you may press keys: a,d,s,y,x,c,1,2,3,p,o\nthen drag mouse over sketch", 10, 30); 
          //shift coords skethup like -> point zero should appear in the center
          translate(250, 250);
          scale(2.5f); 
          
          //draw the same x,y,z lines that you see in google sketchup      
          strokeWeight(2);
          stroke(125, 0, 0);
          line(0, 0, width, 0); 
          stroke(0, 0, 125);
          line(0, 0, 0, -height);

       
          //now draw the model
          for (int i = 0; i< modelsEnabled.length; i++)
          {
               if(modelsEnabled[i])
               {
                    models[i].draw2D();
               }
          }

    }
    
      
    void mouseDragged()
    {
        
         //these functions demonstrate built in tools for model positioning
          
         if (key == 'a' )
         {
               for (int i = 0; i< modelsEnabled.length; i++)
               {
                     if(modelsEnabled[i])
                     {
                          models[i].rotate((mouseX - pmouseX) * 0.004f, 'y');
                          models[i].rotate((mouseY - pmouseY) * 0.004f, 'x');
                     }
               }

         }
         if (key == 'd')
         {
               for (int i = 0; i< modelsEnabled.length; i++)
               {
                     if(modelsEnabled[i])
                     {
                          models[i].scale(1+(pmouseY - mouseY) * 0.01f);
                     }
               }
         }
         if (key == 's' )
         {
               for (int i = 0; i< modelsEnabled.length; i++)
               {
                     if(modelsEnabled[i])
                     {
                          models[i].shift((mouseX - pmouseX)*5, 'x');
                          models[i].shift((mouseY - pmouseY)*5,'y');
                     }
               }
   
         }
      
         if (key == 'y'||key == 'x'||key == 'c' )
         {
             char axis = (key == 'y')?'x':(key == 'x')?'y':(key == 'c')?'z':'0';

               for (int i = 0; i< modelsEnabled.length; i++)
               {
                     if(modelsEnabled[i])
                     {
                           models[i].shift((mouseY - pmouseY)*5, axis);
                     }
               }
         }
         if (key == '1'||key == '2'||key == '3' )
         {
             char axis = (key == '1')?'x':(key == '2')?'y':(key == '3')?'z':'0';
             
               for (int i = 0; i< modelsEnabled.length; i++)
               {
                     if(modelsEnabled[i])
                     {
                           models[i].rotate((mouseY - pmouseY) * 0.004f, axis);
                     }
               }
         }     


    }

    void keyPressed() 
    {
      
         //these shortcuts demonstrate what else you could do: 
         if (key=='p')
               colorManipulations(); 
         if (key=='o')
               shapeManipulations(); 

    }

    //demo how to change colors at runtime (origin dae/kmz wont be changed)
    void colorManipulations()
    {
          for (int i = 0; i< modelsEnabled.length; i++)
         {
               if(modelsEnabled[i])
               {
                   for (Triangle t:models[i].getTriangles()) 
                   {
                      if (!t.containsTexture)
                      {
                         float r=t.colour.red;
                         float g=t.colour.green;
                         float b=t.colour.blue;
                         t.colour.red = g;
                         t.colour.green = b;
                         t.colour.blue = r;
                      }
                   }
               }
         }

    }
    //demo how to change shapes at runtime (origin dae/kmz wont be changed)
    void shapeManipulations()
    {
          for (int i = 0; i< modelsEnabled.length; i++)
         {
               if(modelsEnabled[i])
               {
                   Triangle tri = models[i].getTriangles().get(round(random(0,models[i].getTriangles().size()-1)));
                   tri.A.x +=10.0f;
               }
         }

    }
   


