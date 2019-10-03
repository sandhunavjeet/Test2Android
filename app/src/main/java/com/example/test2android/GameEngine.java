package com.example.test2android;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="DINO-RAINBOWS";

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;


    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;



    // -----------------------------------
    // GAME SPECIFIC VARIABLES
    // -----------------------------------
    // ----------------------------
    // ## SPRITES
    // Adding Player ---------------------------
    Player player;
    List<Item> items = new ArrayList<Item>();
    int itemImages[] = {R.drawable.candy64, R.drawable.poop64,R.drawable.rainbow64,  R.drawable.poop64};
    String Itemtype[] = {"candy","garbage","rainbow","garbage"};
    // represent the TOP LEFT CORNER OF THE GRAPHIC
    int vgap;
    int hgap;
    // ----------------------------
    // ## GAME STATS
    // ----------------------------
    int score = 0;
    int lives = 3;

    public GameEngine(Context context, int w, int h) {
        super(context);

        this.holder = this.getHolder();
        this.paintbrush = new Paint();

        this.screenWidth = w;
        this.screenHeight = h;

        vgap = this.screenHeight/5;
        hgap = this.screenWidth - 140 ;

        this.printScreenInfo();
        //setting up player
        player = new Player(this.getContext(), screenWidth-100, this.screenHeight-250);

        //Setting up items
        for (int i = 0; i < 4; i++) {
            Random r = new Random();

            Item b = new Item(getContext(),(r.nextInt(hgap)+1), ( vgap * (i+1) - 70) );

            b.setImage(BitmapFactory.decodeResource(context.getResources(),
                    itemImages[i]));
            b.setSpeed(r.nextInt(20) +1);
            b.setType(Itemtype[i]);
            items.add(b);
        }
    }



    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    private void spawnPlayer() {
        //@TODO: Start the player at the left side of screen
    }
    private void spawnEnemyShips() {
        Random random = new Random();

        //@TODO: Place the enemies in a random location

    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    public void updatePositions() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            item.setxPosition(item.getxPosition() + item.getSpeed());

            if(item.getxPosition() > screenWidth)
            {
                item.setxPosition(0);
            }
// collision model
            if (player.getHitbox().intersect(item.getHitbox())) {
                //increase score for candy and rainbow
                if(item.getType().equals("candy")||item.getType().equals("rainbow"))
                {
                    score = score + 1;
                }
                //reduce live for garbage
                if(item.getType().equals("garbage")) {
                    // reduce lives
                    lives--;
                }
                // reset object
                item.setxPosition(-500);
            }
        }

        if(lives==0)
        {
            gameIsRunning = false;
        }
    }

    public void redrawSprites() {
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            //----------------

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);

            // Draw Player

            canvas.drawBitmap(this.player.getImage(), this.player.getxPosition(),
                    this.player.getyPosition(), paintbrush);

            // DRAW THE PLAYER HITBOX

            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);
            Rect playerHitbox = player.getHitbox();
            canvas.drawRect(playerHitbox.left, playerHitbox.top, playerHitbox.right, playerHitbox.bottom, paintbrush);

            // Draw Items and item hitboxes
            for (int i = 0; i < items.size(); i++) {
                Item b = items.get(i);

                canvas.drawBitmap(b.getImage(), b.getxPosition(),
                        b.getyPosition(), paintbrush);

                Rect itemHitbox = b.getHitbox();
                canvas.drawRect(itemHitbox.left, itemHitbox.top, itemHitbox.right, itemHitbox.bottom, paintbrush);

            }


            // ------------------------
            // 1. change the paintbrush settings so we can see the hitbox
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            //Drawing bars on the screen

            canvas.drawRect(0, vgap, hgap, vgap + 10, paintbrush);
            canvas.drawRect(0, vgap*2, hgap, vgap*2 + 10, paintbrush);
            canvas.drawRect(0, vgap*3, hgap, vgap*3 + 10, paintbrush);
            canvas.drawRect(0, vgap*4, hgap, vgap*4 + 10, paintbrush);


            // draw game stats
            paintbrush.setTextSize(30);
            paintbrush.setStyle(Paint.Style.FILL);
            paintbrush.setStrokeWidth(0);
            canvas.drawText("Lives: " + lives, 0, 50, paintbrush);
            canvas.drawText("Score: " + score, screenWidth/2, 50, paintbrush);




            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    public void setFPS() {
        try {
            gameThread.sleep(120);
        }
        catch (Exception e) {

        }
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------


    String fingerAction = "";
    int playerdistance = 100;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {
            if (event.getY() < this.screenHeight / 2) {
                player.setyPosition(player.getyPosition() - vgap);
            }
            else {
                player.setyPosition(player.getyPosition() + vgap);
            }
        }
        else if (userAction == MotionEvent.ACTION_UP) {

        }

        return true;
    }
}