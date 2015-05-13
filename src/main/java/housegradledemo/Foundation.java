package housegradledemo;

import materials.Concrete;
import materials.Steel;

public class Foundation {
    private int concrete = 0;
    private int steel = 0;
        
    public boolean layFoundation(Concrete cnr, Steel st) {
        concrete = cnr.getAmount();
        steel = st.getAmount();
        System.out.println("Delivered " + cnr.getAmount()+ " tonnes of concrete.");
        System.out.println("Delivered " + st.getAmount()+ " tonnes of steel.");
        return true;
    }
    
    public boolean isFoundation() {
        return (steel > 0) & (concrete > 0);
    }
}
