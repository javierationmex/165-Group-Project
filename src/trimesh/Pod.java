package trimesh;

import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Javier G on 4/21/2015.
 */
public class Pod extends Group implements Serializable{
    private static final long serialVersionUID = 5526552295622776147L;

    TriMesh child;

    public Pod() {
        OBJLoader loader = new OBJLoader();
        String podDir = "." + File.separator + "materials" + File.separator;
        String podFilename = "Pod2.obj";
        String podFilePath = podDir + podFilename;
        TriMesh pod = loader.loadModel(podFilePath);
        pod.updateLocalBound();

        String podTextureFilename = "pod-texture-done2.jpg";
        String podTextureFilePath = podDir + podTextureFilename;
        Texture podTexture = TextureManager.loadTexture2D(podTextureFilePath);
        pod.setTexture(podTexture);

        pod.translate(0,1,0);
        this.addChild(pod);
        child = pod;
    }

    public TriMesh getChild() {
        return child;
    }
}
