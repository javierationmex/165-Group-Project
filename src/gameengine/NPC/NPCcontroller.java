package gameengine.NPC;

import graphicslib3D.Vector3D;
import networking.packets.ingame.NPCPacket;
import sage.scene.shape.Cube;

import java.util.ArrayList;

/**
 * Created by arash on 5/13/2015.
 */
public class NPCcontroller {

    private ArrayList<NPC> NPClist;

    public NPCcontroller() {
        this.NPClist = new ArrayList<NPC>();
    }

    public void updateNPCs() {
            for(NPC n : NPClist){
                n.updateLocation();
            }
    }

    public void createNPCs(){
        //Create all default NPCS here.
        /*Cube cube1 = new Cube();
        cube1.rotate(180, new Vector3D(0, 1, 0));
        cube1.translate(0, 5, -2000);
        cube1.updateGeometricState(0, true);
        NPClist.add(new NPC("Cube", 1, cube1.getLocalTranslation(), cube1.getLocalRotation(), cube1.getLocalScale()));*/
    }

    public void addNPC(NPC npc){
        NPClist.add(npc);
    }

    public void removeNPC(NPC npc){
        NPClist.remove(npc);
    }

    public NPCPacket getNPCInfoPacket(){
        return new NPCPacket(NPClist);
    }

}
