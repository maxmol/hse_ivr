package maxmol.igp.Drawing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.ArrayList;

import maxmol.igp.R;
import maxmol.igp.classes.MUtil;
import maxmol.igp.classes.Vec2D;

import static maxmol.igp.Drawing.GameDraw.cp;

// This lasers are cool.
public class LaserBeam extends Entity {
    public int dieTime = 250;
    public int damage = 5;
    public float wide;
    private Entity owner;
    private float length;
    public float angle = 90;
    public int color = Color.argb(128, 255, 255, 255);
    private SoundPool soundPool;
    private int laserSound;

    public LaserBeam(int dieTime, int damage, float wide) {
        this.dieTime = dieTime;
        this.damage = damage;
        this.wide = cp(wide);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        laserSound = soundPool.load(GameDraw.context.getContext(), R.raw.electric_loop, 1);

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                soundPool.play(laserSound, 1f, 1f, 0, 1, 1.2f);
            }
        });
    }

    public Entity getOwner() {
        return owner;
    }

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public boolean isDead() {
        return dieTime <= 0;
    }

    @Override
    public void Tick() {
        if (getOwner() != null) {
            if (getOwner() instanceof Enemy && ((Enemy) getOwner()).getHealth() <= 0) {
                Remove();
            }

            setPos(getOwner().getPos());
        }

        length = (int) Math.sqrt(GameDraw.context.ScrH * GameDraw.context.ScrH + GameDraw.context.ScrW * GameDraw.context.ScrW); // Диагональ экрана - максимальная возможная длина лазера.

        if (--dieTime <= 0) {
            Remove();
        }

        Vec2D directional = new Vec2D(0, length).GetRotated(angle - 90);
        Vec2D right = new Vec2D(wide, 0).GetRotated(angle - 90);

        setPointsMesh(new Vec2D[]{
                getPos().plus(right),
                getPos().minus(right),
                getPos().plus(directional).minus(right),
                getPos().plus(directional).plus(right),
        });

        length = GameDraw.context.ScrH;

        if (getOwner() instanceof Enemy) {
            entityHit(GameDraw.context.ship);
        }
        else {
            for (Entity e : GameDraw.context.getEntities()) {
                if (!e.isPhysicsObject()) continue;
                if (this.getOwner() == e) continue;

                if (entityHit(e)) {
                    break;
                }
            }
        }
    }

    private boolean entityHit(Entity e) {
        if (getRegion().contains((int)e.getPos().x, (int)e.getPos().y)) {
            float len = (float) Math.abs(e.getPos().Distance(getPos()));
            if (len < length) length = len;
            e.takeDamage(1);
            GameDraw.context.AddEntity(new SparksEffect(e.getPos(), (int) (Math.random() * 3) + 1, 1, 0, 5, 1, Color.rgb(255, 196, 64)));
            return true;
        }

        return false;
    }

    @Override
    public void Draw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(color);
        p.setAntiAlias(true);

        float x = (float) getPos().x, y = (float) getPos().y;

        canvas.save();
        canvas.rotate(angle - 90, x, y);
        for (int i = 0; i < 4; i++) {
            canvas.drawRect(x - wide * i/4, y, x + wide * i/4, y + length, p);
        }
        canvas.restore();
    }

    @Override
    public boolean isPhysicsObject() {
        return false;
    }

    @Override
    public int getZPos() {
        return 0;
    }

    @Override
    public void Remove() {
        soundPool.stop(laserSound);
        System.out.println("test sound stop");

        super.Remove();
    }
}
