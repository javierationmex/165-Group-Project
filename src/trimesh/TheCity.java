package trimesh;

import graphicslib3D.Point3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingSphere;
import sage.texture.Texture;
import sage.texture.TextureManager;

import java.io.File;
import java.io.Serializable;

/**
 * Created by arash on 4/15/2015.
 */
public class TheCity extends Group implements Serializable {
    private static final long serialVersionUID = 7526552295622776147L;

    TriMesh child;

    public TheCity() {
        OBJLoader loader = new OBJLoader();
        String viperpieceDir = "." + File.separator + "materials" + File.separator;
        String viperpieceFilename = "TheCity.obj";
        String viperpieceFilePath = viperpieceDir + viperpieceFilename;
        TriMesh viperpiece = loader.loadModel(viperpieceFilePath);
        //viperpiece.scale(0.02f, 0.02f, 0.02f);
        this.setLocalBound(new BoundingSphere(new Point3D(0, 0, 0), 2));
        viperpiece.updateLocalBound();

        String viperpieceTextureFilename = "cty1.jpg";
        String viperpieceTextureFilePath = viperpieceDir + viperpieceTextureFilename;
        Texture viperpieceTexture = TextureManager.loadTexture2D(viperpieceTextureFilePath);
        viperpiece.setTexture(viperpieceTexture);

        this.addChild(viperpiece);
        child = viperpiece;


    }

    public TriMesh getChild() {
        return child;
    }


}
