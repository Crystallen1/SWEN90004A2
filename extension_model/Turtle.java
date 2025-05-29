package extension_model;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Turtle {
    int x, y;
    int age;
    int wealth;
    int metabolism;
    int vision;
    int lifeExpectancy;
    private Direction currentDirection;

    private static final Random random = new Random();

    public Turtle(int metabolism, int vision, int lifeExpectancy) {
        this.metabolism = metabolism;
        this.vision = vision;
        this.lifeExpectancy = lifeExpectancy;
        this.age = 0;
        this.wealth = 0; // Initial wealth is set in World
    }

    /**
     * Decide movement direction following NetLogo's turn-towards-grain logic
     */
    public void decideDirection(World world) {
        Direction bestDirection = Direction.NORTH; // Default direction
        int bestAmount = getGrainAhead(world, Direction.NORTH);
        
        // Check all 4 directions
        for (Direction dir : Direction.values()) {
            int grainInDirection = getGrainAhead(world, dir);
            if (grainInDirection > bestAmount) {
                bestAmount = grainInDirection;
                bestDirection = dir;
            }
        }
        
        currentDirection = bestDirection;
    }

    /**
     * Calculate total grain ahead in specified direction following NetLogo's 
     * grain-ahead logic
     */
    private int getGrainAhead(World world, Direction direction) {
        int total = 0;
        for (int distance = 1; distance <= vision; distance++) {
            Patch patch = world.getPatchAhead(x, y, direction, distance);
            total += patch.getGrainHere();
        }
        return total;
    }

    /**
     * Move, eat grain, age, and possibly die following NetLogo's 
     * move-eat-age-die logic
     */
    public void moveEatAgeDie(World world) {
        // Move forward 1 step
        if (currentDirection != null) {
            x = (x + currentDirection.getDx() + world.width) % world.width;
            y = (y + currentDirection.getDy() + world.height) % world.height;
        }
        
        // Consume grain according to metabolism
        wealth -= metabolism;
        
        // Age
        age++;
        
        // Check death conditions: no grain or exceeded life expectancy
        if (wealth < 0 || age >= lifeExpectancy) {
            handleInheritance(world);
        }
    }

    /**
     * 处理财产继承逻辑
     */
    private void handleInheritance(World world) {
        int inheritedWealth;
        
        if (wealth < 0) {
            // 破产死亡：给予基本生存资金，模拟社会保障或家庭最低支持
            inheritedWealth = metabolism + random.nextInt(3); // metabolism + 0-2的随机支持
        } else {
            // 自然死亡：继承大部分财产，但有一定损失（税收、丧葬费等）
            inheritedWealth = (int) (wealth * (0.7 + random.nextDouble() * 0.2)); // 70%-90%继承率
            // 确保至少有基本生存资金
            inheritedWealth = Math.max(inheritedWealth, metabolism + 1);
        }
        
        // 重置为新个体
        resetAsNewIndividual(world, inheritedWealth);
    }

    /**
     * 重置为新个体，带有继承的财产
     */
    private void resetAsNewIndividual(World world, int inheritedWealth) {
        // 随机设置新属性
        this.lifeExpectancy = world.minLifeExpectancy + 
                             random.nextInt(world.maxLifeExpectancy 
                             - world.minLifeExpectancy + 1);
        this.metabolism = 1 + random.nextInt(world.maxMetabolism);
        this.vision = 1 + random.nextInt(world.maxVision);
        
        // 设置继承的财产
        this.wealth = inheritedWealth;
        
        // 设置随机年龄
        this.age = random.nextInt(this.lifeExpectancy);
        
        // 移动到随机位置
        int[] location = world.getRandomPatchLocation();
        this.x = location[0];
        this.y = location[1];
    }

    // Keep original methods for compatibility
    public void move(World world) {
        if (currentDirection != null) {
            x = (x + currentDirection.getDx() + world.width) % world.width;
            y = (y + currentDirection.getDy() + world.height) % world.height;
        }
    }

    public void ageAndConsume() {
        age++;
        wealth -= metabolism;
    }

    public boolean isDead() {
        return age >= lifeExpectancy || wealth <= 0;
    }

    public void rebirth(World world) {
        handleInheritance(world);
    }
}
