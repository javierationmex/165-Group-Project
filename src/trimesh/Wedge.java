package trimesh;

import sage.scene.TriMesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by arash on 2/26/2015.
 */
public class Wedge extends TriMesh {
    public Wedge() {
        setIsTransformSpaceParent(true);
        this.setName("Wedge");
        float[] vrts = new float[]{
                0, -1, 0, -0.5f, -1, -5, 0.5f, -1, -5,
                0, 1, 0, -0.5f, 1, -5, 0.5f, 1, -5
        };
        float[] cl = new float[]{
                0.6f, 0.6f, 0.6f, 1, 0.3f, 0.4f, 0.5f, 1, 0.7f, 0.3f, 0.5f, 1,
                0.6f, 0.6f, 0.6f, 1, 0.3f, 0.4f, 0.5f, 1, 0.7f, 0.3f, 0.5f, 1
        };
        int[] triangles = new int[]{
                0, 1, 2,
                1, 2, 4,
                2, 4, 5,
                3, 4, 5
        };
        FloatBuffer vertBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(vrts);
        FloatBuffer colorBuf = com.jogamp.common.nio.Buffers.newDirectFloatBuffer(cl);
        IntBuffer triangleBuf = com.jogamp.common.nio.Buffers.newDirectIntBuffer(triangles);
        this.setVertexBuffer(vertBuf);
        this.setColorBuffer(colorBuf);
        this.setIndexBuffer(triangleBuf);


    }
}
