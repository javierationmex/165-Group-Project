package trimesh;


import graphicslib3D.Vector3D;
import sage.model.loader.OBJLoader;
import sage.scene.Group;
import sage.scene.TriMesh;
import sage.texture.TextureManager;
import java.io.File;
import sage.texture.Texture;
import java.io.Serializable;
/**
 * Created by Javier G on 4/21/2015.
 */
public class Ship extends Group implements Serializable{
    private static final long serialVersionUID = 5526552295622776147L;

    TriMesh child;

    public Ship() {
        OBJLoader loader = new OBJLoader();
        String shipDir = "." + File.separator + "materials" + File.separator;
        String shipFilename = "Ship2.obj";
        String shipFilePath = shipDir + shipFilename;
        TriMesh ship = loader.loadModel(shipFilePath);
        ship.updateLocalBound();

        String shipTextureFilename = "ship-texture5.png";
        String shipTextureFilePath = shipDir + shipTextureFilename;
        Texture shipTexture = TextureManager.loadTexture2D(shipTextureFilePath);
        ship.setTexture(shipTexture);

        ship.translate(0,1,0);
        this.addChild(ship);
        child = ship;
    }

    public TriMesh getChild() {
        return child;
    }
}
