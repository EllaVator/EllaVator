package housegradledemo;

import materials.Wood;

public class Roof {
    private int wood = 0;
        
    public boolean coverRoof(Wood woo) {
        wood = woo.getAmount();
        System.out.println("Delivered "+ woo.getAmount() + " tonnes of wood.");
        return true;
    }
    
    public boolean isFoundation() {
        return wood > 0;
    }
    
}
