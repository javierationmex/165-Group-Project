package trimesh;

import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.texture.Texture;
import sage.texture.TextureManager;

import java.io.File;
import java.io.Serializable;

/**
 * Created by arash on 4/15/2015.
 */
public class Arc170 extends Group implements Serializable {
    private static final long serialVersionUID = 7526552295622776147L;

    TriMesh child;

    public Arc170() {
        OBJLoader loader = new OBJLoader();
        String arc170pieceDir = "." + File.separator + "materials" + File.separator;
        String arc170pieceFilename = "Arc170.obj";
        String arc170pieceFilePath = arc170pieceDir + arc170pieceFilename;
        TriMesh arc170piece = loader.loadModel(arc170pieceFilePath);
        arc170piece.scale(0.005f, .005f, .005f);
        //arc170piece.setLocalBound(new BoundingSphere(new Point3D(0,,), 1));
        arc170piece.updateLocalBound();

        String arc170pieceTextureFilename = "Arc170_blinn1.png";
        String arc170pieceTextureFilePath = arc170pieceDir + arc170pieceTextureFilename;
        Texture arc170pieceTexture = TextureManager.loadTexture2D(arc170pieceTextureFilePath);
        arc170piece.setTexture(arc170pieceTexture);

        this.addChild(arc170piece);
        child = arc170piece;


    }

    public TriMesh getChild() {
        return child;
    }
}


