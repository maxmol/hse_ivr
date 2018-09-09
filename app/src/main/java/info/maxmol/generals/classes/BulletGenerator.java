package info.maxmol.generals.classes;

import java.util.Map;

import info.maxmol.generals.Drawing.Bullet;
import info.maxmol.generals.Drawing.Entity;
import info.maxmol.generals.Drawing.GameDraw;
import info.maxmol.generals.Drawing.Ship;

// Magic bullet generator class.
public class BulletGenerator {
    public int bulletsPerShoot = 1;
    public double angleSpread = 180;
    public double bulletSpeed = 10;
    public double inaccuracy = 0;
    public double bulletAcceleration = 0;
    public double bulletCurve = 0;
    public int bulletRate = 20;
    public int bulletDamage = 5;
    public Vec2D bulletOffset = new Vec2D();
    public boolean shootAtPlayer = false;

    public int arraysCount = 1;
    public double arraysSpread = 180;

    public double spinSpeed = 0;
    public double currentSpin = 0;
    public double spinAcceleration = 0;
    public double spinMaxSpeed = 25;

    public int roundBullets = 0;
    public double roundReload = 0;

    public int curRoundBullets = 0;
    public double roundCountDown = 0;

    protected Entity owner;
    protected int bulletCountDown;

    public BulletGenerator() {
        bulletCountDown = bulletRate;
    }

    public BulletGenerator(Entity e) {
        setOwner(e);
        bulletCountDown = bulletRate;
    }

    public BulletGenerator(Integer bulletsPerShoot, Double angleSpread, Double bulletSpeed, Double bulletAcceleration, Double bulletCurve, Integer bulletRate, Integer bulletDamage, Integer arraysCount, Double arraysSpread, Double spinSpeed, Double currentSpin, Double spinAcceleration, Double spinMaxSpeed, Integer roundBullets, Double roundReload) {
        if (bulletsPerShoot != null) this.bulletsPerShoot = bulletsPerShoot;
        if (angleSpread != null) this.angleSpread = angleSpread;
        if (bulletSpeed != null) this.bulletSpeed = bulletSpeed;
        if (bulletAcceleration != null) this.bulletAcceleration = bulletAcceleration;
        if (bulletCurve != null) this.bulletCurve = bulletCurve;
        if (bulletRate != null) this.bulletRate = bulletRate;
        if (bulletDamage != null) this.bulletDamage = bulletDamage;
        if (arraysCount != null) this.arraysCount = arraysCount;
        if (arraysSpread != null) this.arraysSpread = arraysSpread;
        if (spinSpeed != null) this.spinSpeed = spinSpeed;
        if (currentSpin != null) this.currentSpin = currentSpin;
        if (spinAcceleration != null) this.spinAcceleration = spinAcceleration;
        if (spinMaxSpeed != null) this.spinMaxSpeed = spinMaxSpeed;
        if (roundBullets != null) {
            this.roundBullets = roundBullets;
        }
        if (roundReload != null) {
            this.roundReload = roundReload;
            this.roundCountDown = roundReload;
        }

        bulletCountDown = this.bulletRate;
    }

    public void update() {
        if (roundBullets > 0) {
            if (curRoundBullets <= 0) {
                if (roundCountDown <= 0) {
                    curRoundBullets = roundBullets;
                    roundCountDown = roundReload;
                } else {
                    roundCountDown--;
                    return;
                }
            }
        }

        bulletCountDown--;
        if (bulletCountDown <= 0) {
            spawn();
            if (roundBullets > 0) curRoundBullets--;
            bulletCountDown = bulletRate;
        }
    }

    public void setOwner(Entity e) {
        owner = e;
    }

    public Bullet constructBullet(Vec2D bulletPos, Vec2D vel) {
        return new Bullet(bulletPos, vel, bulletAcceleration, bulletDamage, bulletCurve, owner);
    }

    public void spawn() {
        currentSpin += spinSpeed;
        spinSpeed += spinAcceleration;
        if (Math.abs(spinSpeed) > spinMaxSpeed) {
            spinAcceleration = -spinAcceleration;
        }

        Vec2D bulletPos;
        if (owner != null) {
            float ownerAng = owner.getAngle();
            if (ownerAng != 0f) // not sure if this helps but GetRotated is a quite heavy method
                bulletPos = owner.getPos().plus(bulletOffset.GetRotated(owner.getAngle()));
            else
                bulletPos = owner.getPos().plus(bulletOffset);
        }
        else
            bulletPos = bulletOffset;

        double toShipAng = 0.0;

        if (shootAtPlayer) {
            toShipAng = bulletPos.getRotationTo(GameDraw.context.ship.getPos());
        }

        for (int j = 0; j < arraysCount; j++) {
            double arrayAng = j == 0 ? 0 : arraysSpread/j;

            for (int i = 0; i < bulletsPerShoot; i++) {
                double ang = Math.toRadians((i == 0 ? 0 : (angleSpread/(bulletsPerShoot - 1)) * i) + arrayAng + currentSpin + 90 - (bulletsPerShoot > 1 ? angleSpread/2 : 0) + toShipAng + (Math.random() - 0.5) * inaccuracy * 360);
                Vec2D vel = new Vec2D(Math.cos(ang) * GameDraw.cp(bulletSpeed), Math.sin(ang) * GameDraw.cp(bulletSpeed));

                GameDraw.context.AddEntity(constructBullet(bulletPos, vel));
            }
        }
    }
}
