package info.maxmol.generals.classes;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;

import java.util.Random;

import info.maxmol.generals.Drawing.Bullet;
import info.maxmol.generals.Drawing.DrawThread;
import info.maxmol.generals.Drawing.Enemy;
import info.maxmol.generals.Drawing.Entity;
import info.maxmol.generals.Drawing.ExplosionEffect;
import info.maxmol.generals.Drawing.GameDraw;
import info.maxmol.generals.Drawing.LaserBeam;
import info.maxmol.generals.Drawing.PathEnemy;
import info.maxmol.generals.FightActivity;
import info.maxmol.generals.GameActivity;
import info.maxmol.generals.R;

import static info.maxmol.generals.Drawing.GameDraw.cp;

// Stages logic. Making a stage is rather simple now.
public class Stages {
    private static int money;

    public static final int COUNT = 5;

    private static class Stage2BossBullet extends Bullet {
        @Override
        public void Draw(Canvas canvas) {
            Paint p = new Paint();
            p.setColor(Color.argb(128, 255, 64, 64));
            canvas.drawCircle((float) getPos().x, (float) getPos().y, cp(25f), p);
            canvas.drawCircle((float) getPos().x, (float) getPos().y, cp(20f), p);
        }

        public Stage2BossBullet(Vec2D vec2D, Vec2D vel, double accel, int damage, double curving, Entity owner) {
            super(vec2D, vel, accel, damage, curving, owner);
        }

        @Override
        public void Tick() {
            super.Tick();

            if (getPos().x > GameDraw.context.ScrW * 0.95 || getPos().x < GameDraw.context.ScrW * 0.05 || getPos().y < GameDraw.context.ScrH * 0.05 || getPos().y > GameDraw.context.ScrH * 0.95) {
                BulletGenerator bulletGenerator = new BulletGenerator(8, 360.0, 10.0, 0.0, null, 0, null, null, null, null, null, null, null, null, null);
                bulletGenerator.setOwner(this);
                bulletGenerator.update();
                Remove();
            }
        }
    }

    private static class Stage2BossBulletGenerator extends BulletGenerator {
        @Override
        public Bullet constructBullet(Vec2D bulletPos, Vec2D vel) {
            return new Stages.Stage2BossBullet(bulletPos, vel, bulletAcceleration, bulletDamage, bulletCurve, owner);
        }

        public Stage2BossBulletGenerator() {
            super();
        }
        public Stage2BossBulletGenerator(Integer bulletsPerShoot, Double angleSpread, Double bulletSpeed, Double bulletAcceleration, Double bulletCurve, Integer bulletRate, Integer bulletDamage, Integer arraysCount, Double arraysSpread, Double spinSpeed, Double currentSpin, Double spinAcceleration, Double spinMaxSpeed, Integer roundBullets, Double roundReload) {
            super(bulletsPerShoot, angleSpread, bulletSpeed, bulletAcceleration, bulletCurve, bulletRate, bulletDamage, arraysCount, arraysSpread, spinSpeed, currentSpin, spinAcceleration, spinMaxSpeed, roundBullets, roundReload);
        }
    }

    private static class FreeMode3BulletGenerator extends Stage2BossBulletGenerator {
        public FreeMode3BulletGenerator() {
            super(null, null, null, null, null, 50, 5, null, null, null, null, null, null, null, null);
            this.inaccuracy = 0.2;
        }
    }

    public static class StageScript extends Thread {
        private int stage;
        public boolean running = true;

        public StageScript(int stage) {
            this.stage = stage;

        }

        @Override
        public void run() {
            switch (stage) {
                case 1: {
                    // - STAGE I - //
                    FightActivity.playMusic(R.raw.stage_music_1);

                    if (wait(100)) return;
                    GameDraw.context.AddEntity(new Enemy(50, 20, getBulletGenerator(0)));

                    if (wait(250)) return;
                    GameDraw.context.AddEntity(new Enemy(20, 20, getBulletGenerator(0)));
                    GameDraw.context.AddEntity(new Enemy(80, 20, getBulletGenerator(0)));

                    if (wait(350)) return;
                    GameDraw.context.AddEntity(new Enemy(30, 20, getBulletGenerator(1), R.drawable.enemy));
                    GameDraw.context.AddEntity(new Enemy(50, 20, getBulletGenerator(1), R.drawable.enemy));
                    GameDraw.context.AddEntity(new Enemy(70, 20, getBulletGenerator(1), R.drawable.enemy));

                    if (wait(500)) return;

                    GameDraw.context.AddEntity(new PathEnemy(10, 999, getBulletGenerator(2), R.drawable.enemy, new Vec2D[] {new Vec2D(10, 25)}, true));
                    GameDraw.context.AddEntity(new PathEnemy(90, 999, getBulletGenerator(2), R.drawable.enemy, new Vec2D[] {new Vec2D(90, 25)}, true));

                    if (wait(100)) return;

                    GameDraw.context.AddEntity(new PathEnemy(20, 999, getBulletGenerator(2), R.drawable.enemy, new Vec2D[] {new Vec2D(20, 15)}, true));
                    GameDraw.context.AddEntity(new PathEnemy(80, 999, getBulletGenerator(2), R.drawable.enemy, new Vec2D[] {new Vec2D(80, 15)}, true));

                    if (wait(200)) return;

                    PathEnemy laserEnemy = new PathEnemy(50, 150, null, R.drawable.enemy3, new Vec2D[] {new Vec2D(50, 10)}, true);
                    laserEnemy.coinsCount = 4;
                    laserEnemy.explosion = 2;
                    GameDraw.context.AddEntity(laserEnemy);

                    if (wait(100)) return;

                    while (laserEnemy.getHealth() > 0) {
                        if (wait(100)) return;

                        laserEnemy.addPathPos(new Vec2D(75, 10), true);

                        LaserBeam laserBeam = new LaserBeam(100, 1, 20);
                        laserBeam.setOwner(laserEnemy);
                        GameDraw.context.AddEntity(laserBeam);

                        if (wait(100)) return;

                        laserEnemy.addPathPos(new Vec2D(25, 10), true);
                    }

                    for (Entity e: GameDraw.context.getEntities()) {
                        if (e instanceof Enemy) {
                            ((Enemy) e).kill();
                        }
                    }

                    if (wait(400)) return;

                    break;
                }
                case 2: {
                    // - STAGE II - //
                    FightActivity.playMusic(R.raw.stage_music_2);

                    PathEnemy pe;

                    if (wait(150)) return;
                    GameDraw.context.AddEntity(new Enemy(10, 35, getBulletGenerator(3)));
                    GameDraw.context.AddEntity(new Enemy(90, 35, getBulletGenerator(3)));

                    if (wait(500)) return;
                    GameDraw.context.AddEntity(new Enemy(25, 20, getBulletGenerator(5), R.drawable.enemy));
                    GameDraw.context.AddEntity(new Enemy(50, 20, getBulletGenerator(5), R.drawable.enemy));
                    GameDraw.context.AddEntity(new Enemy(75, 20, getBulletGenerator(5), R.drawable.enemy));

                    if (wait(600)) return;
                    pe = new PathEnemy(50, 80, getBulletGenerator(6), R.drawable.enemy, new Vec2D[]{new Vec2D(50, 25)}, true);
                    pe.coinsCount = 10;
                    pe.explosion = 2f;
                    GameDraw.context.AddEntity(pe);

                    Enemy laserEnemy = new Enemy(10, 60, null, R.drawable.enemy3);
                    GameDraw.context.AddEntity(laserEnemy);
                    laserEnemy.explosion = 1.5f;
                    Enemy laserEnemy2 = new Enemy(90, 60, null, R.drawable.enemy3);
                    GameDraw.context.AddEntity(laserEnemy2);
                    laserEnemy2.explosion = 1.5f;

                    if (wait(75)) return;

                    LaserBeam laserBeam = new LaserBeam(1000, 2, 20);
                    laserBeam.setOwner(laserEnemy);
                    GameDraw.context.AddEntity(laserBeam);

                    LaserBeam laserBeam2 = new LaserBeam(1000, 2, 20);
                    laserBeam2.setOwner(laserEnemy2);
                    GameDraw.context.AddEntity(laserBeam2);

                    if (wait(150)) return;

                    while (laserEnemy.getAngle() > 52) {
                        if (wait(2)) return;

                        laserBeam.angle -= 1;
                        laserBeam2.angle += 1;

                        laserEnemy.setAngle(laserEnemy.getAngle() - 1);
                        laserEnemy2.setAngle(laserEnemy2.getAngle() + 1);
                    }

                    while (pe.getHealth() > 0 ) {
                        if (wait(10)) return;
                    }

                    if (wait(100)) return;


                    for (int i = 0; i < 2; i++) {
                        Stage2BossBulletGenerator bulletGenerator = new Stage2BossBulletGenerator();
                        bulletGenerator.bulletSpeed = 10;
                        bulletGenerator.bulletAcceleration = -0.01;
                        bulletGenerator.bulletDamage = 5;
                        bulletGenerator.currentSpin = i == 0 ? -90 : 90;
                        bulletGenerator.bulletRate = 24;
                        bulletGenerator.bulletCountDown = i == 0 ? 12 : 0;

                        Enemy startboss = new Enemy(i == 0 ? 10 : 90, 10000, bulletGenerator, R.drawable.enemy4);
                        startboss.setAngle(i == 0 ? 0 : -180);
                        startboss.setSpeed(5);
                        GameDraw.context.AddEntity(startboss);
                    }

                    if (wait(600)) return;

                    break;
                }
                case 3: {
                    // - STAGE III - //
                    FightActivity.playMusic(R.raw.stage_music_2);

                    PathEnemy pe;

                    if (wait(150)) return;

                    PathEnemy[] twins = new PathEnemy[2];

                    for (int i = 0; i < 2; i++) {
                        Stage2BossBulletGenerator bulletGenerator = new Stage2BossBulletGenerator();
                        bulletGenerator.bulletSpeed = 10;
                        bulletGenerator.bulletDamage = 5;
                        bulletGenerator.currentSpin = i == 0 ? -90 : 90;
                        bulletGenerator.bulletRate = 30;
                        bulletGenerator.inaccuracy = 0.3;
                        bulletGenerator.bulletCountDown = i == 0 ? 12 : 0;

                        PathEnemy boss = new PathEnemy(0, 80, bulletGenerator, R.drawable.enemy4, new Vec2D[]{new Vec2D(i == 0 ? 10 : 90, 40)}, true);
                        boss.setPos(new Vec2D(GameDraw.context.ScrW * (i == 0 ? 0.1 : 0.9), GameDraw.context.ScrH + cp(5)));
                        boss.setAngle(i == 0 ? 0 : -180);
                        boss.setSpeed(5);
                        boss.coinsCount = 8;
                        GameDraw.context.AddEntity(boss);

                        twins[i] = boss;
                    }

                    while (true) {
                        if (wait(10)) return;

                        if (twins[0].getHealth() <= 0 && twins[1].getHealth() <= 0) break;
                    }

                    if (wait(150)) return;

                    Stage2BossBulletGenerator bulletGenerator = new Stage2BossBulletGenerator();
                    bulletGenerator.bulletSpeed = 10;
                    bulletGenerator.bulletDamage = 5;
                    bulletGenerator.bulletRate = 30;
                    bulletGenerator.bulletsPerShoot = 2;
                    bulletGenerator.angleSpread = 45;

                    PathEnemy e1 = new PathEnemy(50, 100, bulletGenerator, R.drawable.enemy4, new Vec2D[]{new Vec2D(50, 20)}, true);
                    e1.coinsCount = 6;
                    GameDraw.context.AddEntity(e1);

                    PathEnemy e2 = new PathEnemy(15, 50, null, R.drawable.enemy3, new Vec2D[]{new Vec2D(15, 80)}, true);
                    e2.setAngle(25);
                    e2.coinsCount = 3;
                    GameDraw.context.AddEntity(e2);

                    PathEnemy e3 = new PathEnemy(85, 50, null, R.drawable.enemy3, new Vec2D[]{new Vec2D(85, 80)}, true);
                    e3.setAngle(155);
                    e3.coinsCount = 3;
                    GameDraw.context.AddEntity(e3);

                    while (e1.isAlive() || e2.isAlive() || e3.isAlive()) {
                        if (e2.isAlive()) e2.addPathPos(new Vec2D(15, 25), true);
                        if (e3.isAlive()) e3.addPathPos(new Vec2D(85, 25), true);
                        if (wait(100)) return;

                        LaserBeam lb1 = new LaserBeam(150, 1, 20);
                        lb1.setOwner(e2);
                        lb1.angle = 25;
                        GameDraw.context.AddEntity(lb1);

                        LaserBeam lb2 = new LaserBeam(150, 1, 20);
                        lb2.setOwner(e3);
                        lb2.angle = 155;
                        GameDraw.context.AddEntity(lb2);

                        if (wait(100)) return;
                        if (e2.isAlive()) e2.addPathPos(new Vec2D(15, 95), true);
                        if (e3.isAlive()) e3.addPathPos(new Vec2D(85, 95), true);
                    }

                    if (wait(200)) return;
                    break;
                }
                case 4: {
                    // STAGE IV

                    FightActivity.playMusic(R.raw.stage_music_1);

                    Enemy e1 = new Enemy(30, 20, getBulletGenerator(7), R.drawable.enemy2);
                    e1.coinsCount = 4;
                    GameDraw.context.AddEntity(e1);
                    Enemy e2 = new Enemy(70, 20, getBulletGenerator(7), R.drawable.enemy2);
                    e2.coinsCount = 4;
                    GameDraw.context.AddEntity(e2);

                    if (wait(100)) return;
                    Enemy e3 = new Enemy(50, 30, getBulletGenerator(6), R.drawable.enemy2);
                    e3.coinsCount = 4;
                    GameDraw.context.AddEntity(e3);

                    while (true) {
                        if (wait(10)) return;

                        if (!e1.isValid() && !e2.isValid() && !e3.isValid()) break;
                    }

                    if (wait(100)) return;

                    for (int i = 1; i <= 5; i++) {
                        GameDraw.context.AddEntity(new Enemy(i * 10, 20, getBulletGenerator(0), R.drawable.enemy));
                        if (i != 5)
                            GameDraw.context.AddEntity(new Enemy(100 - i * 10, 10, getBulletGenerator(0), R.drawable.enemy));

                        if (wait(70)) return;
                    }

                    if (wait(500)) return;

                    Stage2BossBulletGenerator bulletGenerator = new Stage2BossBulletGenerator();
                    bulletGenerator.bulletSpeed = 10;
                    bulletGenerator.bulletDamage = 5;
                    bulletGenerator.bulletRate = 100;
                    bulletGenerator.bulletsPerShoot = 2;
                    bulletGenerator.inaccuracy = 0.025;
                    bulletGenerator.angleSpread = 60;
                    bulletGenerator.currentSpin = 0;

                    PathEnemy bigBalls = new PathEnemy(50, 120, bulletGenerator, R.drawable.enemy5, new Vec2D[]{new Vec2D(50, 15)}, true);
                    bigBalls.coinsCount = 8;
                    GameDraw.context.AddEntity(bigBalls);

                    LaserBeam laserBeam = new LaserBeam(10000, 10, 20);
                    laserBeam.setOwner(bigBalls);
                    GameDraw.context.AddEntity(laserBeam);

                    boolean left = true;
                    while (bigBalls.isAlive()) {
                        if (left) {
                            laserBeam.angle++;
                            bigBalls.setAngle(bigBalls.getAngle() + 1);
                        } else {
                            laserBeam.angle--;
                            bigBalls.setAngle(bigBalls.getAngle() - 1);
                        }

                        if (Math.abs(laserBeam.angle - 90) >= 20) {
                            left = !left;
                        }

                        if (wait(4)) return;
                    }

                    if (wait(200)) return;

                    break;
                }
                case 5: {
                    // STAGE V - FINAL
                    FightActivity.playMusic(R.raw.stage_music_2);

                    BulletGenerator bossGenerator = new BulletGenerator(4, 50.0, 10.0, -0.02, null, 5, 2, 2, 180.0, -21.0, null, 1.5, 30.0, null, null);

                    PathEnemy boss = new PathEnemy(50, 2000, bossGenerator, R.drawable.enemyboss, new Vec2D[]{new Vec2D(50, 30)}, true);
                    boss.coinsCount = 25;
                    GameDraw.context.AddEntity(boss);

                    while (boss.getHealth() > boss.getMaxHealth() * 0.66) {
                        if (wait(4)) return;
                    }

                    GameDraw.context.AddEntity(new ExplosionEffect(boss.getPos(), 2.0, 0));

                    bossGenerator.bulletRate = 4;

                    boss.setBulletGenerator(null);

                    if (wait(200)) return;

                    boss.setBulletGenerator(bossGenerator);

                    LaserBeam laserBeam = null;
                    LaserBeam laserBeam2 = null;

                    int ang1 = 90;
                    int ang2 = 90;
                    boolean reverse = false;

                    int laserInterval = 50;
                    while (boss.getHealth() > boss.getMaxHealth() * 0.33) {
                        if (laserInterval >= 20) {
                            laserBeam = new LaserBeam(60, 1, 20);
                            laserBeam.setOwner(boss);
                            GameDraw.context.AddEntity(laserBeam);

                            laserBeam2 = new LaserBeam(60, 1, 20);
                            laserBeam2.setOwner(boss);
                            GameDraw.context.AddEntity(laserBeam2);

                            laserInterval = 0;
                        }
                        else {
                            laserInterval++;
                        }

                        if (Math.abs(ang1 - 90) > 40) reverse = !reverse;

                        if (laserBeam != null) {
                            ang1 += reverse ? -1 : 1;
                            ang2 -= reverse ? -1 : 1;

                            laserBeam.angle = ang1;
                            laserBeam2.angle = ang2;
                        }

                        if (wait(4)) return;
                    }

                    GameDraw.context.AddEntity(new ExplosionEffect(boss.getPos(), 2.0, 0));

                    int spawnInterval = 90;

                    while (boss.isAlive()) {
                        if (laserInterval >= 20) {
                            laserBeam = new LaserBeam(60, 2, 20);
                            laserBeam.setOwner(boss);
                            GameDraw.context.AddEntity(laserBeam);

                            laserBeam2 = new LaserBeam(60, 2, 20);
                            laserBeam2.setOwner(boss);
                            GameDraw.context.AddEntity(laserBeam2);

                            laserInterval = 0;
                        }
                        else {
                            laserInterval++;
                        }

                        if (Math.abs(ang1 - 90) > 40) reverse = !reverse;

                        if (laserBeam != null) {
                            ang1 += reverse ? -1 : 1;
                            ang2 -= reverse ? -1 : 1;

                            laserBeam.angle = ang1;
                            laserBeam2.angle = ang2;
                        }

                        if (spawnInterval >= 100) {
                            Enemy enemy = new Enemy(20, 20, new Stage2BossBulletGenerator(null, null, null, null, null, 75, null, null, null, null, null, null, null, null, null), R.drawable.enemy4);
                            enemy.setSpeed(4.00);
                            GameDraw.context.AddEntity(enemy);

                            Enemy enemy2 = new Enemy(80, 20, new Stage2BossBulletGenerator(null, null, null, null, null, 75, null, null, null, null, null, null, null, null, null), R.drawable.enemy4);
                            enemy2.setSpeed(4.00);
                            GameDraw.context.AddEntity(enemy2);
                            spawnInterval = 0;
                        }
                        spawnInterval++;

                        if (wait(4)) return;
                    }

                    if (wait(200)) return;

                    break;
                }
                case COUNT + 1: {
                    // -= FREEMODE =- //

                    FightActivity.playMusic(R.raw.stage_music_1);

                    Random random = new Random();

                    int[] enemyTypes = new int[] {
                            R.drawable.enemy,
                            R.drawable.enemy2,
                            R.drawable.enemy4,
                    };

                    int counter = 0;
                    while (true) {
                        int spawnType = random.nextInt(3);
                        int primaryType = random.nextInt(3);
                        int secondaryType = random.nextInt(3);

                        int primaryTypeImg = enemyTypes[primaryType];
                        int secondaryTypeImg = enemyTypes[secondaryType];

                        int primaryBulletGenerator = 1;
                        if (primaryType == 0) {
                            primaryBulletGenerator = random.nextInt(3);
                        }
                        else if (primaryType == 1) {
                            primaryBulletGenerator = 3 + random.nextInt(5);
                        }
                        else if (primaryType == 2) {
                            primaryBulletGenerator = -1;
                        }

                        int secondaryBulletGenerator = 1;
                        if (secondaryType == 0) {
                            secondaryBulletGenerator = random.nextInt(3);
                        }
                        else if (secondaryType == 1) {
                            secondaryBulletGenerator = 3 + random.nextInt(5);
                        }
                        else if (secondaryType == 2) {
                            secondaryBulletGenerator = -1;
                        }

                        int health = 20 + counter * 5;
                        switch (spawnType) {
                            case 0:
                                GameDraw.context.AddEntity(new Enemy(10 + random.nextInt(80), health, primaryBulletGenerator == -1 ? new FreeMode3BulletGenerator() : getBulletGenerator(primaryBulletGenerator), primaryTypeImg));
                                break;
                            case 1:
                                GameDraw.context.AddEntity(new Enemy(15, health, primaryBulletGenerator == -1 ? new FreeMode3BulletGenerator() : getBulletGenerator(primaryBulletGenerator), primaryTypeImg));
                                GameDraw.context.AddEntity(new Enemy(85, health, primaryBulletGenerator == -1 ? new FreeMode3BulletGenerator() : getBulletGenerator(primaryBulletGenerator), primaryTypeImg));
                                break;
                            case 2:
                                GameDraw.context.AddEntity(new Enemy(50, 20 + counter * 5, secondaryBulletGenerator == -1 ? new FreeMode3BulletGenerator() : getBulletGenerator(secondaryBulletGenerator), secondaryTypeImg));

                                GameDraw.context.AddEntity(new Enemy(15, health, primaryBulletGenerator == -1 ? new FreeMode3BulletGenerator() : getBulletGenerator(primaryBulletGenerator), primaryTypeImg));
                                GameDraw.context.AddEntity(new Enemy(85, health, primaryBulletGenerator == -1 ? new FreeMode3BulletGenerator() : getBulletGenerator(primaryBulletGenerator), primaryTypeImg));
                                break;
                        }

                        if (wait(500 - MUtil.Clamp(counter * 10, 0, 300))) return;
                        counter++;
                    }
                }
            }

            Game.addMoney(Stages.getMoney());
            
            if (Game.getStep() == Game.getStage()) {
                Game.nextStep();
                Game.setStage(Game.getStep());
            }

            FightActivity.context.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    GameDraw.context.drawThread.kill();

                    new AlertDialog.Builder(FightActivity.context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Congrats!")
                            .setMessage("You have completed this stage.")
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    GameActivity.context.recreate();
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
        }

        private boolean wait(int intervals) { // this is weird... couldn't do any better tho
            GameDraw.context.drawThread.stageSleep(intervals);

            while (true) {
                if (!running) return true;

                if (GameDraw.context.stageSleeping > 0) {
                    try {
                        this.sleep(DrawThread.interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                break;
            }

            return false;
        }
    }

    public static void start() {
        money = 0;
    }

    public static void collectMoney(int m) {
        money += m;
    }

    public static int getMoney() {
        return money;
    }

    public static BulletGenerator getBulletGenerator(int i) {
        switch (i) {
            case 0:
                BulletGenerator bg = new BulletGenerator(1, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
                bg.shootAtPlayer = true;
                bg.inaccuracy = 0.02;
                return bg;
            case 1:
                return new BulletGenerator(2, 15.0, null, null, null, null, null, null, null, null, null, null, null, null, null);
            case 2:
                return new BulletGenerator(3, 10.0, null, null, null, null, null, null, null, null, null, null, null, null, null);
            case 3:
                return new BulletGenerator(1, null, null, 0.01, null, 2, null, null, null, 10.0, -10.0, null, null, 40, 120.0);
            case 4:
                return new BulletGenerator(2, 180.0, null, 0.01, null, 10, null, null, null, 15.0, null, null, null, null, null);
            case 5:
                return new BulletGenerator(2, 30.0, null, 0.0, null, 20, null, 2, 180.0, 15.0, null, null, null, null, null);
            case 6:
                return new BulletGenerator(16, 360.0, null, 0.0, null, 10, null, null, null, 4.0, null, null, null, 8, 25.0);
            case 7:
                return new BulletGenerator(2, 180.0, null, 0.01, null, 10, null, null, null, 0.0, null, 2.0, 20.0, null, null);
        }

        return new BulletGenerator();
    }
}