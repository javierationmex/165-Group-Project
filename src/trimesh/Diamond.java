package trimesh;

import graphicslib3D.Point3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingSphere;

import java.io.File;
import java.io.Serializable;

/**
 * Created by arash on 4/15/2015.
 */
public class Diamond extends Group implements Serializable {
    private static final long serialVersionUID = 7526552295622776147L;

    TriMesh child;

    public Diamond(String name) {
        this.setName(name);
        OBJLoader loader = new OBJLoader();
        String diamondpieceDir = "." + File.separator + "materials" + File.separator;
        String diamondpieceFilename = "diamond.obj";
        String diamondpieceFilePath = diamondpieceDir + diamondpieceFilename;
        TriMesh diamondpiece = loader.loadModel(diamondpieceFilePath);
        //diamondpiece.scale(0.005f, .005f, .005f);
        diamondpiece.setLocalBound(new BoundingSphere(new Point3D(0, 0, 0), 3));
        diamondpiece.updateLocalBound();

//        String diamondpieceTextureFilename = "diamond_blinn1.png";
//        String diamondpieceTextureFilePath = diamondpieceDir + diamondpieceTextureFilename;
//        Texture diamondpieceTexture = TextureManager.loadTexture2D(diamondpieceTextureFilePath);
//        diamondpiece.setTexture(diamondpieceTexture);

        this.addChild(diamondpiece);
        child = diamondpiece;


    }

    public TriMesh getChild() {
        return child;
    }
}


