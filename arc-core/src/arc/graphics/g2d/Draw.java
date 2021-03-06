package arc.graphics.g2d;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.gl.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.ArcAnnotate.*;

import static arc.Core.*;

public class Draw{
    private static final Color[] carr = new Color[3];
    private static final float[] vertices = new float[SpriteBatch.SPRITE_SIZE];
    private static @Nullable Camera lastProj;
    private static Rect lastViewport = new Rect();

    public static float scl = 1f;

    public static void batch(Batch nextBatch){
        flush();
        Core.batch = nextBatch;
    }

    public static void batch(Batch nextBatch, Runnable run){
        Batch prev = Core.batch;
        prev.flush();

        Core.batch = nextBatch;

        run.run();

        nextBatch.flush();
        Core.batch = prev;
    }

    public static Shader getShader(){
        return Core.batch.getShader();
    }

    public static void shader(Shader shader){
        shader(shader, true);
    }

    public static void shader(Shader shader, boolean apply){
        Core.batch.setShader(shader, apply);
    }

    public static void shader(){
        Core.batch.setShader(null);
    }

    /** Note that sorting is disabled by default, even if it is supported. */
    public static void sort(boolean sort){
        batch.setSort(sort);
    }

    public static float z(){
        return Core.batch.z;
    }

    /** Note that this does nothing on most Batch implementations. */
    public static void z(float z){
        Core.batch.z(z);
    }

    public static Color getColor(){
        return Core.batch.getColor();
    }

    public static Color getMixColor(){
        return Core.batch.getMixColor();
    }

    public static void mixcol(Color color, float a){
        Core.batch.setMixColor(color.r, color.g, color.b, Mathf.clamp(a));
    }

    public static void mixcol(){
        Core.batch.setPackedMixColor(Color.clearFloatBits);
    }

    public static void tint(Color a, Color b, float s){
        Tmp.c1.set(a).lerp(b, s);
        Core.batch.setColor(Tmp.c1.r, Tmp.c1.g, Tmp.c1.b, Core.batch.getColor().a);
    }

    public static void tint(Color color){
        Core.batch.setColor(color.r, color.g, color.b, Core.batch.getColor().a);
    }

    public static void colorMul(Color color, float mul){
        color(color.r * mul, color.g * mul, color.b * mul, 1f);
    }

    public static void color(Color color){
        Core.batch.setColor(color);
    }

    public static void color(Color color, float alpha){
        Core.batch.setColor(color.r, color.g, color.b, alpha);
    }

    public static void color(int color){
        Core.batch.setColor(Tmp.c1.rgba8888(color));
    }

    public static void color(float color){
        Core.batch.setPackedColor(color);
    }

    public static void color(Color a, Color b, Color c, float progress){
        carr[0] = a;
        carr[1] = b;
        carr[2] = c;
        color(Tmp.c1.lerp(carr, progress));
    }

    /** Automatically mixes colors. */
    public static void color(Color a, Color b, float s){
        Core.batch.setColor(Tmp.c1.set(a).lerp(b, s));
    }

    public static void color(){
        Core.batch.setPackedColor(Color.whiteFloatBits);
    }

    public static void color(float r, float g, float b){
        Core.batch.setColor(r, g, b, 1f);
    }

    public static void color(float r, float g, float b, float a){
        Core.batch.setColor(r, g, b, a);
    }

    /** Lightness color. */
    public static void colorl(float l){
        color(l, l, l);
    }

    /** Lightness color, alpha. */
    public static void colorl(float l, float a){
        color(l, l, l, a);
    }

    public static void blend(Blending blending){
        Core.batch.setBlending(blending);
    }

    public static void blend(){
        blend(Blending.normal);
    }

    public static void reset(){
        color();
        mixcol();
        Lines.stroke(1f);
    }

    public static void alpha(float alpha){
        Core.batch.setColor(Core.batch.getColor().r, Core.batch.getColor().g, Core.batch.getColor().b, alpha);
    }

    public static void fbo(Texture texture, int worldWidth, int worldHeight, int tilesize){
        float ww = worldWidth * tilesize, wh = worldHeight * tilesize;
        float x = camera.position.x + tilesize / 2f, y = camera.position.y + tilesize / 2f;
        float u = (x - camera.width / 2f) / ww,
        v = (y - camera.height / 2f) / wh,
        u2 = (x + camera.width / 2f) / ww,
        v2 = (y + camera.height / 2f) / wh;

        Tmp.tr1.set(texture);
        Tmp.tr1.set(u, v2, u2, v);

        Draw.rect(Tmp.tr1, camera.position.x, camera.position.y, camera.width, camera.height);
    }

    /** On a sorting or queued batch implementation, this treats everything inside the runnable as one unit.
     * Thus, it can be used to set shaders and do other special state. */
    public static void draw(Runnable run){
        batch.draw(run);
    }

    public static void rect(FrameBuffer buffer){
        rect(wrap(buffer.getTexture()), camera.position.x, camera.position.y, camera.width, -camera.height);
    }

    public static void rect(String region, float x, float y, float w, float h){
        rect(Core.atlas.find(region), x, y, w, h);
    }

    public static void rect(TextureRegion region, float x, float y, float w, float h){
        Core.batch.draw(region, x - w /2f, y - h /2f, 0, 0, w, h, 0);
    }

    public static void rect(TextureRegion region, float x, float y){
        rect(region, x, y, region.getWidth() * scl, region.getHeight() * scl);
    }

    public static void rect(String region, float x, float y){
        rect(Core.atlas.find(region), x, y);
    }

    public static void rect(TextureRegion region, float x, float y, float w, float h, float originX, float originY, float rotation){
        Core.batch.draw(region, x - w /2f, y - h /2f, originX, originY, w, h, rotation);
    }

    public static void rect(String region, float x, float y, float w, float h, float originX, float originY, float rotation){
        Core.batch.draw(Core.atlas.find(region), x - w /2f, y - h /2f, originX, originY, w, h, rotation);
    }

    public static void rect(TextureRegion region, float x, float y, float w, float h, float rotation){
        rect(region, x, y, w, h, w/2f, h/2f, rotation);
    }

    public static void rect(String region, float x, float y, float w, float h, float rotation){
        rect(Core.atlas.find(region), x, y, w, h, w/2f, h/2f, rotation);
    }

    public static void rect(TextureRegion region, Position pos, float w, float h){
        rect(region, pos.getX(), pos.getY(), w, h);
    }

    public static void rect(TextureRegion region, Position pos, float w, float h, float rotation){
        rect(region, pos.getX(), pos.getY(), w, h, rotation);
    }

    public static void rect(TextureRegion region, Position pos, float rotation){
        rect(region, pos.getX(), pos.getY(), rotation);
    }

    public static void rect(TextureRegion region, float x, float y, float rotation){
        rect(region, x, y, region.getWidth() * scl, region.getHeight() * scl, rotation);
    }

    public static void rect(String region, float x, float y, float rotation){
        rect(Core.atlas.find(region), x, y, rotation);
    }

    public static void vert(Texture texture, float[] vertices, int offset, int length){
        Core.batch.draw(texture, vertices, offset, length);
    }

    public static void vert(float[] vertices){
        vert(Core.atlas.texture(), vertices, 0, vertices.length);
    }

    public static void flush(){
        Core.batch.flush();
    }

    public static Rect lastViewport(){
        return lastViewport;
    }

    public static void proj(Camera proj){
        proj(proj.mat);
    }

    public static void proj(Mat proj){
        lastProj = (Core.camera != null && camera.mat == proj ? camera : null);
        if(lastProj != null){
            lastProj.bounds(lastViewport);
        }
        Core.batch.setProjection(proj);
    }

    public static Mat proj(){
        lastProj = null;
        return Core.batch.getProjection();
    }

    public static void trans(Mat trans){
        Core.batch.setTransform(trans);
    }

    public static Mat trans(){
        return Core.batch.getTransform();
    }

    /** @return whether the batch's projection is currently the camera. */
    public static boolean isCamera(){
        return lastProj == camera;
    }

    public static TextureRegion wrap(Texture texture){
        Tmp.tr2.set(texture);
        return Tmp.tr2;
    }

    public static void rectv(TextureRegion region, float x, float y, float width, float height, Cons<Vec2> tweaker){
        rectv(region, x, y, width, height, 0, tweaker);
    }

    public static void rectv(TextureRegion region, float x, float y, float width, float height, float rotation, Cons<Vec2> tweaker){
        rectv(region, x, y, width, height, width/2, height/2, rotation, tweaker);
    }

    public static void rectv(TextureRegion region, float x, float y, float width, float height, float originX, float originY, float rotation, Cons<Vec2> tweaker){
        x -= width/2f;
        y -= height/2f;

        //bottom left and top right corner points relative to origin
        final float worldOriginX = x + originX;
        final float worldOriginY = y + originY;
        float fx = -originX;
        float fy = -originY;
        float fx2 = width - originX;
        float fy2 = height - originY;

        float x1;
        float y1;
        float x2;
        float y2;
        float x3;
        float y3;
        float x4;
        float y4;

        // rotate
        final float cos = Mathf.cosDeg(rotation);
        final float sin = Mathf.sinDeg(rotation);

        x1 = cos * fx - sin * fy;
        y1 = sin * fx + cos * fy;

        x2 = cos * fx - sin * fy2;
        y2 = sin * fx + cos * fy2;

        x3 = cos * fx2 - sin * fy2;
        y3 = sin * fx2 + cos * fy2;

        x4 = x1 + (x3 - x2);
        y4 = y3 - (y2 - y1);

        x1 += worldOriginX;
        y1 += worldOriginY;
        x2 += worldOriginX;
        y2 += worldOriginY;
        x3 += worldOriginX;
        y3 += worldOriginY;
        x4 += worldOriginX;
        y4 += worldOriginY;

        tweaker.get(Tmp.v1.set(x1, y1));
        x1 = Tmp.v1.x;
        y1 = Tmp.v1.y;

        tweaker.get(Tmp.v1.set(x2, y2));
        x2 = Tmp.v1.x;
        y2 = Tmp.v1.y;

        tweaker.get(Tmp.v1.set(x3, y3));
        x3 = Tmp.v1.x;
        y3 = Tmp.v1.y;

        tweaker.get(Tmp.v1.set(x4, y4));
        x4 = Tmp.v1.x;
        y4 = Tmp.v1.y;

        final float u = region.u;
        final float v = region.v2;
        final float u2 = region.u2;
        final float v2 = region.v;

        final float color = batch.getPackedColor();
        final float mixColor = batch.getPackedMixColor();
        vertices[0] = x1;
        vertices[1] = y1;
        vertices[2] = color;
        vertices[3] = u;
        vertices[4] = v;
        vertices[5] = mixColor;

        vertices[6] = x2;
        vertices[7] = y2;
        vertices[8] = color;
        vertices[9] = u;
        vertices[10] = v2;
        vertices[11] = mixColor;

        vertices[12] = x3;
        vertices[13] = y3;
        vertices[14] = color;
        vertices[15] = u2;
        vertices[16] = v2;
        vertices[17] = mixColor;

        vertices[18] = x4;
        vertices[19] = y4;
        vertices[20] = color;
        vertices[21] = u2;
        vertices[22] = v;
        vertices[23] = mixColor;

        Draw.vert(vertices);
    }
}
