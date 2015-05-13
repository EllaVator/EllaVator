package housegradledemo;

import materials.Pipes;

public class Plumbing {
    private int pipes = 0;
    
    public boolean layPipes(Pipes pps) {
        pipes = pps.getAmount();
        System.out.println("Delivered "+ pps.getAmount() + " pipes.");
        return true;
    }
    
    public boolean isFoundation() {
        return pipes > 0;
    }
    
}
