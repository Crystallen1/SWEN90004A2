import model.World;

public class Main {
    public static void main(String[] args) {
        World world = new World();
        world.initialize(42);

        for (int i = 0; i < 500; i++) {
            world.step();
            if (i % 10 == 0) {
                world.printStats();
            }
        }
    }
}
