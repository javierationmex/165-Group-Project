package gameengine.shapes;

import sage.scene.TriMesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by Javier G on 2/24/2015.
 */
public class Trapesoid extends TriMesh {
    private static float[] vrts = new float[] {2,-1,1,2,-1,-1,1,1,-1,1,1,1,-1,1,1,-1,1,-1,-2,-1,1,-2,-1,-1};
    private static float[] cl = new float[] {1,0,1,0,0,1,1,1,0,0,0,1,0,1,0,1,1,0,1,1,1,0,1,1,0,1,0,1,0,1,1,0};
    private static int[] triangles = new int[] {0,1,2,0,2,3,0,3,4,0,4,6,2,3,5,3,4,5,1,2,5,1,5,7,0,1,6,1,6,7,4,5,6,5,6,7};
    public Trapesoid()
    {
        FloatBuffer vertBuf =
                com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
        FloatBuffer colorBuf =
                com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
        IntBuffer triangleBuf =
                com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
        this.setVertexBuffer(vertBuf);
        this.setColorBuffer(colorBuf);
        this.setIndexBuffer(triangleBuf); }

}
