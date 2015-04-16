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
public class Mushroom extends Group implements Serializable {

    private static final long serialVersionUID = 7526472295522776147L;

    public Mushroom() {

        OBJLoader loader = new OBJLoader();
        String mushroomDir = "." + File.separator + "materials" + File.separator;
        String mushroomFilename = "mushroom.obj";
        String mushroomFilePath = mushroomDir + mushroomFilename;
        TriMesh mushroom = loader.loadModel(mushroomFilePath);
        mushroom.updateLocalBound();
        mushroom.scale(6, 6, 6);
        mushroom.translate(-20, 3, 0);

        String mushroomTextureFilename = "texture-mushroom-2.jpg";
        String mushroomTextureFilePath = mushroomDir + mushroomTextureFilename;
        Texture mushroomTexture = TextureManager.loadTexture2D(mushroomTextureFilePath);
        mushroom.setTexture(mushroomTexture);
        this.addChild(mushroom);
    }
}
