import model.World;

public class Main {
    public static void main(String[] args) {

        World world = new World(50, 50, 250, 5, 15, 
        1, 83, 10, 
        1, 4);
        world.initialize(60);

        for (int i = 0; i < 1000; i++) {
            world.step();
            if (i % 10 == 0) {
                world.printStats();
            }
        }
    }
}
