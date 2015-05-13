package housegradledemo;

public class House {
    private Foundation foundation;
    private Walls walls;
    private Roof roof;
    private Plumbing plumbing;
    
    public House(Foundation f, Walls w, Roof r, Plumbing p) {
        System.out.println("Working hard! (>_<)");
        this.foundation = f;
        this.walls = w;
        this.roof = r;
        this.plumbing = p;
        System.out.println("Success! The House was built!");
    }
}
