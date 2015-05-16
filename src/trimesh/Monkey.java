package trimesh;

import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;

import java.io.File;
import java.io.Serializable;

/**
 * Created by arash on 4/15/2015.
 */
public class Monkey extends Group implements Serializable {
    private static final long serialVersionUID = 7526552295624476147L;

    public Monkey() {
        OBJLoader loader = new OBJLoader();
        String chesspieceDir = "." + File.separator + "materials" + File.separator;
        String chesspieceFilename = "TheMonkey.obj";
        String chesspieceFilePath = chesspieceDir + chesspieceFilename;
        TriMesh chesspiece = loader.loadModel(chesspieceFilePath);
        chesspiece.updateLocalBound();

//        String chesspieceTextureFilename = "chess-texture.jpg";
//        String chesspieceTextureFilePath = chesspieceDir + chesspieceTextureFilename;
//        Texture chesspieceTexture = TextureManager.loadTexture2D(chesspieceTextureFilePath);
//        chesspiece.setTexture(chesspieceTexture);

        this.addChild(chesspiece);
    }
}
