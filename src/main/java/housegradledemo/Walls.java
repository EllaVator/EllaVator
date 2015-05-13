package housegradledemo;

import materials.Bricks;

public class Walls {
    private int bricks = 0;
        
    public boolean buildWalls(Bricks br) {
        bricks = br.getAmount();
        System.out.println("Delivered "+ br.getAmount() + " bricks.");
        return true;
    }
    
    public boolean isFoundation() {
        return bricks > 0;
    }
    
}
