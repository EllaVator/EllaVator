package housegradledemo;

import materials.Bricks;
import materials.Concrete;
import materials.Pipes;
import materials.Steel;
import materials.Wood;

public class HouseGradleDemo {

    public static void main(String[] args) {
        System.out.println("Starting House construction!");
        
        //materials
        Concrete concrete = new Concrete();
        Steel steel = new Steel();
        Bricks bricks = new Bricks();
        Wood wood = new Wood();
        Pipes pipes = new Pipes();
        
        //parts
        Foundation fnd = new Foundation();
        Walls wl = new Walls();
        Roof rf = new Roof();
        Plumbing pl = new Plumbing();
        
        //bulding
        fnd.layFoundation(concrete, steel);
        wl.buildWalls(bricks);
        rf.coverRoof(wood);
        pl.layPipes(pipes);
        House house = new House(fnd, wl, rf, pl);
    }
}
