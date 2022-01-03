package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import static com.jme3.math.FastMath.computeNormal;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.util.BufferUtils;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    int globalnum = 0;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
        
    }

    @Override
    public void simpleInitApp() {
        
        AmbientLight am = new AmbientLight();
        rootNode.addLight(am);
       Node top = (Node)assetManager.loadModel("Models/stage1.j3o");
       
        File dir = new File(System.getProperty("user.home") + "\\poop");
        for (File file : dir.listFiles())
            if (!file.isDirectory())
                file.delete();
       
        rootNode.attachChild(top);
        genMesh(top);      
   }
    public float sinDeg(float a ) {
        return FastMath.sin(a*FastMath.DEG_TO_RAD);
        
    }
    public float cosDeg(float a) {
        return FastMath.cos(a*FastMath.DEG_TO_RAD);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    public void genMesh(Node top) {
        for (int in = 0; in < top.getQuantity(); in++) {

            try {
                String fileName = "object " +globalnum + ".obj";

                File dir = new File(System.getProperty("user.home") + "\\poop");
                File file = new File(dir, fileName);
                file.createNewFile();
                FileWriter writer = new FileWriter(file);

                if (top.getChild(in) instanceof Geometry) {
                    Geometry cube = (Geometry) top.getChild(in);

                    Mesh mesh = cube.getMesh();
                    IndexBuffer ib = mesh.getIndicesAsList();

                    Vector3f[] vertices = BufferUtils.getVector3Array(mesh.getFloatBuffer(VertexBuffer.Type.Position));
                    Vector3f[] normals = BufferUtils.getVector3Array(mesh.getFloatBuffer(VertexBuffer.Type.Normal));
                    FloatBuffer fl = mesh.getFloatBuffer(VertexBuffer.Type.TexCoord);

                    int[] indices = new int[ib.size()];

                    for (int i = 0; i < indices.length; i++) {

                        indices[i] = ib.get(i);

                    }

                    for (int g = 0; g < vertices.length; g++) {

                        float[] poop = null;
                        poop = cube.getWorldRotation().toAngles(poop);
                        Vector3f angle = new Vector3f(poop[0] * FastMath.RAD_TO_DEG, poop[1] * FastMath.RAD_TO_DEG, poop[2] * FastMath.RAD_TO_DEG);

                        float cx = (vertices[g].x) * cube.getWorldScale().x;
                        float cy = (vertices[g].y) * cube.getWorldScale().y;
                        float cz = (vertices[g].z) * cube.getWorldScale().z;

                        float cya = cy * cosDeg(angle.x) - cz * sinDeg(angle.x);
                        float cza = cy * sinDeg(angle.x) + cz * cosDeg(angle.x);

                        cy = cya;
                        cz = cza;

                        float cxb = cx * cosDeg(angle.y) + cz * sinDeg(angle.y);
                        float czb = cz * cosDeg(angle.y) - cx * sinDeg(angle.y);

                        cx = cxb;
                        cz = czb;

                        float cxc = cx * cosDeg(angle.z) - cy * sinDeg(angle.z);
                        float cyc = cx * sinDeg(angle.z) + cy * cosDeg(angle.z);

                        cx = cxc;
                        cy = cyc;

                        writer.write("v "
                                + (cx + cube.getWorldTranslation().x) + " "
                                + (cy + cube.getWorldTranslation().y) + " "
                                + (cz + cube.getWorldTranslation().z) + "\n");

                    }
                    try {
                    for (int i = 0; i < fl.capacity(); i += 2) {
                        writer.write("vt " + fl.get() + " " + fl.get() + "\n");
                    }
                    } catch(NullPointerException e) {
                        System.out.println("ERROR, DOES NOT HAVE VT. SKIPPING");
                    }
                    for (int p = 0; p < ib.size(); p += 3) {
                        writer.write("f " + (ib.get(p) + 1) + " " + (ib.get(p + 1) + 1) + " " + (ib.get(p + 2) + 1) + "\n");

                    }
                    globalnum++;
                    writer.flush();
                    writer.close();
                } else if(top.getChild(in) instanceof Node){
                    Node next = (Node) top.getChild(in);
                    genMesh(next);
                } else {
                    System.out.print("Not a geom or node, ignoring");
                }

            } catch (IOException ex) {
                System.out.println("ERRR WHAT THE FUCK ");
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);

            }

            // Writes the content to the file
        }
    }
   
   
}

  
