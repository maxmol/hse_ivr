package info.maxmol.generals.Drawing;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.view.SurfaceHolder;

import info.maxmol.generals.FightActivity;
import info.maxmol.generals.GameActivity;
import info.maxmol.generals.classes.Game;
import info.maxmol.generals.classes.MUtil;
import info.maxmol.generals.classes.Stages;

// This is the drawing and thinking machine. It calls and draws all entities in the game.
public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private boolean running = true;
    public static final int interval = 20;
    public long sysTime;
    private int dieCounter = 50; // Wait a bit before creating a dialog window

    private static float cp(float pixels) { // Converted Pixels
        return GameDraw.cp(pixels);
    }

    private static float cp(int pixels) {
        return GameDraw.cp(pixels);
    }

    private static double cp(double pixels) {
        return GameDraw.cp(pixels);
    }

    public DrawThread(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
    }

    public void stageSleep(int intervals) {
        GameDraw.context.stageSleeping += MUtil.Clamp(intervals, 0);
    }

    private void StageTick() {
        if (GameDraw.context.stageSleeping > 0) {
            GameDraw.context.stageSleeping--;
        }
    }

    @Override
    public void run() {
        Stages.start();

        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();

            if (canvas == null) {
                continue;
            }

            // --- TickStuff ---

            if (!GameDraw.context.paused) StageTick();

            sysTime = System.currentTimeMillis();


            // - Add enemies
            /*
            counterToEnemyNextSpawn++;

            if (Stages.COUNT < Game.getStage() - 1) {
                if (counterToEnemyNextSpawn >= 250) {
                    Enemy e = new Enemy();
                    e.setPos(new Vec2D(Math.random() * GameDraw.context.ScrW, cp(-20)));
                    e.setBulletGenerator(Stages.getBulletGenerator((int) (Math.random() * 7)));

                    GameDraw.context.AddEntity(e);

                    counterToEnemyNextSpawn = 0;
                }
            }*/

            // --- Core Stuff ---

            if (!GameDraw.context.paused) {
                // - Tick
                for (Entity ent : GameDraw.context.getEntities()) {
                    ent.Tick();
                }
            }

            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.rgb(0, 0, 64));
            canvas.drawPaint(p);

            if (GameDraw.context.shouldSort) {
                GameDraw.context.SortEntities();
            }

            for (Entity ent : GameDraw.context.getEntities()) {
                ent.Draw(canvas);
            }

            for (SuperVGUI v : GameDraw.context.getVGUIObjects()) {
                v.Draw(canvas);
            }

            p.setColor(Color.WHITE);
            p.setTextSize(cp(42));
            canvas.drawText(Game.formatMoney(Stages.getMoney()), cp(10), cp(40), p);
            p.setColor(Color.GREEN);

            try {
                canvas.drawRect(
                        0f,
                        (float) GameDraw.context.ScrH - (cp(16)),
                        (GameDraw.context.ScrW * ((float) Game.getHealth() / (float) Game.getMaxHealth())),
                        (float) GameDraw.context.ScrH, p);
            }
            catch (ArithmeticException e) {

            }

            // --- Pause ---

            if (GameDraw.context.paused) {
                p.setColor(Color.WHITE);
                p.setTextSize(cp(64));
                p.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Paused", GameDraw.context.ScrW / 2, cp(120), p);
            }

            long deltaTime = System.currentTimeMillis() - sysTime + 1;
            //canvas.drawText(1000/deltaTime + " FPS", GameDraw.context.ScrW/10, cp(120), p); // FPS counter

            surfaceHolder.unlockCanvasAndPost(canvas);

            if (deltaTime < interval) {
                try {
                    sleep(interval - deltaTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (GameDraw.context.ship == null || !GameDraw.context.ship.isValid()) {
                if (dieCounter > 0) {
                    dieCounter--;
                }
                else {
                    for (Entity ent : GameDraw.context.getEntities()) {
                        ent.Remove();
                    }

                    FightActivity.context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(FightActivity.context)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle(Game.getStage() == Stages.COUNT + 1 ? "Collected: " + Game.formatMoney(Stages.getMoney()) : "You lose!")
                                    .setMessage("Your ship is destroyed.")
                                    .setPositiveButton("Close", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            GameActivity.context.recreate(); // Weird bug fix
                                            FightActivity.context.finish();
                                        }

                                    })
                                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            GameActivity.context.recreate();
                                            FightActivity.context.finish();
                                        }
                                    })
                                    .show();
                        }
                    });

                    kill();
                }
            }
        }
    }

    public void kill() {
        running = false;
    }
}