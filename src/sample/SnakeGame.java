package sample;
// programmed till:
/*
working fine!
 */
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends Application {
    // variables
    static int width =400;
    static int height =500;
    static int speed=1;
    static Point foodLocstion;
    static Direction direction = Direction.up;
    static Boolean gameEnded= true;
    static List<Point> snake = new ArrayList<Point>();
    static Boolean created = false;
    static int score = 0;
    static int topScore=0;
    static boolean playedBefore = false;
    static boolean foodEaten= true;


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Snake game");

        var canvas = new Canvas(width, height);
        var gc =canvas.getGraphicsContext2D();

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE); // to keep going as long as needed

        var scene = new Scene(new StackPane(canvas));

        // controls
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            // uf you press a key to make the snake move to the opposite direction nothing will happen
                    if (e.getCode() == KeyCode.W && direction!= Direction.down){
                        direction = Direction.up;
                    }
                    if (e.getCode() == KeyCode.A&& direction!= Direction.right) {
                        direction = Direction.left;
                    }
                    if (e.getCode() == KeyCode.S&& direction!= Direction.up) {
                        direction = Direction.down;
                    }
                    if (e.getCode() == KeyCode.D&& direction!= Direction.left) {
                        direction = Direction.right;
                    }
                });
        canvas.setOnMouseClicked(e ->  {gameEnded=false;}); // game starts when you click the mouse

        primaryStage.setScene(scene);
        primaryStage.show();
        tl.play();
    }

    private void run(GraphicsContext gc){
        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        if (gameEnded) {
            // before the game starts:
            if(playedBefore){
                gc.setFont(Font.font(46));
                gc.setFill(Color.WHITE);
                gc.fillText("GAME OVER!", width*0.5,height*0.42);
            }
            // check if there was a previous ended game
            gc.setFont(Font.font(16));
            gc.setFill(Color.WHITE);
            gc.fillText("Top score: "+ topScore,width*0.5,height*0.55);

            //printing message:
            gc.setFont(Font.font(28));
            gc.setStroke(Color.RED);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.strokeText("Click to start", width / 2, height / 2);

            //creating the snake starting body:
            if(!created) {
                snake.add(new Point(height / 2, width / 2)); // tail point
                for(int i=0; i<99; i++)
                    addSnakeNode(); // head points gets added in the end of the array
                created = true;
            }
            // setting food starting location
        }
        else {
            // print the score on the top left corner
            gc.setFont(Font.font(16));
            gc.setFill(Color.WHITE);
            gc.fillText("Score: "+ score, 30, 30);

            // draw the snake
            for(Point node: snake){
                gc.setFill(Color.PINK);
                gc.fillRect(node.x,node.y,6,6);
            }

            // draw the food
            if (foodEaten) {
                getNewFoodLocation(); // get new location for the next food
            }
            // draw food
            gc.fillOval(foodLocstion.x, foodLocstion.y, 6,6);

            moveForward();

            // did the snake eat the food?
            if (snakeAteFood()) {
                score++;
                foodEaten=true;
                for (int i=0; i<6; i++)
                addSnakeNode();
            }

            if (isCrashed()) {
                gameEnded=true;
                if(score> topScore)
                    topScore=score;
                score=0;
                speed = 1;
                snake = new ArrayList<Point>();
                created=false;
                direction=Direction.right;
                playedBefore = true;
                foodEaten= true;
            }
        }

    }

    public void addSnakeNode() {
        switch (direction) {
            case up -> {
                snake.add(new Point(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y-1));
                break;
            }
            case down -> {
                snake.add(new Point(snake.get(snake.size() - 1).x, snake.get(snake.size() - 1).y+1));
                break;
            }
            case left -> {
                snake.add(new Point(snake.get(snake.size() - 1).x-1, snake.get(snake.size() - 1).y));

                break;
            }
            case right -> {
                snake.add(new Point(snake.get(snake.size() - 1).x+1, snake.get(snake.size() - 1).y));
            break;
            }
        }
    }

    public void moveForward() {
        for (int i =0; i<= snake.size() - 2; i++) {
            snake.get(i).x= snake.get(i+1).x;
            snake.get(i).y= snake.get(i+1).y;
        }
        switch (direction) {
            case up -> {
                snake.get(snake.size()-1).y-=speed;
                break;
            }
            case down -> {
                snake.get(snake.size()-1).y+=speed;
                break;
            }
            case left -> {
                snake.get(snake.size()-1).x-=speed;
                break;
            }
            case right -> {
                snake.get(snake.size()-1).x+=speed;
                break;
            }
        }


    }

    public static Boolean isCrashed() {
        // if snake crashed in the borders
        // (0,0) is the top left corner and (width,height) is the bottom right
        // the - numbers are for visual purpose since each point in the snake is not represented by a one pixel square
        if (snake.get(snake.size() - 1).y < 1 || snake.get(snake.size() - 1).y >= height-3
                || snake.get(snake.size() - 1).x < 1 || snake.get(snake.size() - 1).x >= width-5)
            return true;

        // if snake crashed in its body
        for (int i =0; i< snake.size() - 2; i++){
            // the 2nd point is not checked since the head can't crash it anyways
            //but letting the program check it will make the program think that the snake crashed when the snake eats
            // food and gets extended from the front
        if (snake.get(snake.size()-1).x==snake.get(i).x && snake.get(snake.size()-1).y==snake.get(i).y)
            return true;
        }

        return false;
    }

    public static void getNewFoodLocation() {
        foodLocstion = new Point(new Random().nextInt(width-10)+5, new Random().nextInt(height-10)+5);
        // the generated food can't be under the snake's boody
        for (Point p : snake) {
            if (foodLocstion.equals(p))
                getNewFoodLocation();
        }
        foodEaten=false;
    }

    public static boolean snakeAteFood() {
        // foodlocation+3 >= snake head location >= foodlocation-3
        // 3 is a circumstance around the location that has the food since it is so somall (1 pixel) and the food is drawn
        // as a circle which has a radius of 6
        if (snake.get(snake.size()-1).x >= foodLocstion.x-6 && snake.get(snake.size()-1).x<= foodLocstion.x+6)
            if (snake.get(snake.size()-1).y >= foodLocstion.y-6 && snake.get(snake.size()-1).y<= foodLocstion.y+6)
                 return true;
        return false;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
