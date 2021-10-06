package application;
import java.util.ArrayList;

public class CharacterAI extends Character {
    public final static int Brain_Size = 300;
    public final static int startX = 100;
    public final static int startY = 165;
    
    public ArrayList<Integer> brain = new ArrayList<>();
    
    boolean brainDead, up = false;
    int step = 0;
    
    public void initializeBot(){
        this.setTranslateX(startX);
        this.setTranslateY(startY);
        for (int i = 0; i <=CharacterAI.Brain_Size; i++) {
            this.brain.add((int) (Math.random() * 4));
        }
    }
    
    public void updateBot(double fitness, ArrayList<Integer> BestBrain) {
        this.setTranslateX(100);
        this.setTranslateY(165);
        this.brain = new ArrayList<>(BestBrain);
        for (int j = 0; j <= CharacterAI.Brain_Size; j++) {
            double x;
            if (j <= 100)
                x = (Math.random() * 16.25) - (300 - j) / 180;
            else
                x = (Math.random() * 16.25) - 0.75;
            if (x > fitness) {
                this.brain.set(j, (int) (Math.random() * 3));
            }
        }
    }

    public void applyGravity(){
        if ((this.playerVelocity.getY() < 10) && (!this.brainDead)) {
            this.playerVelocity = this.playerVelocity.add(0, 2.4);
        }
    }
    
    public void move(int levelWidth){
        if (!this.brainDead) {
            this.step++;
            if (this.brain.get(this.step) == 0 && this.getTranslateY() >= 5) {
                this.animation.setOffsetX(75);
                this.jumpPlayer();
            }
            if (this.brain.get(this.step) == 1 && this.getTranslateX() + 40 <= levelWidth - 5) {
                this.animation.setOffsetX(75);
                this.setScaleX(1);
                this.animation.play();
                this.moveX(10);
            }
            if (this.brain.get(this.step) == 2) {
                this.animation.setOffsetX(0);
                this.animation.setCycleCount(1);
                this.animation.stop();
            }

            if (this.brain.get(this.step) == 3) {
                this.animation.setOffsetX(75);
                this.setScaleX(-1);
                this.animation.play();
                this.moveX(-5);
            }
        }
    }
    
    public double calculateFitness(){
        double distance = Math.hypot(this.getTranslateX() - 1150, this.getTranslateY() - 190);
        double fitness = (1150 - distance) / (distance + 1150) * 10;

        if (this.Vlad) {
            fitness += 1;
        }
        //if bot reaches end , fitness += 3
        fitness += this.getTranslateX()*(3.0/1000);

        if ((this.getTranslateY() > 200)) {
            fitness -= 1;
        }

        if (this.up) {
            fitness += 1.25;
        }
        return fitness;
    }

    CharacterAI() {
        super();
    }
}
