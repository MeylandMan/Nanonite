package net.GameLayer;

import net.Core.*;
import net.Core.Rendering.Scene;
import net.Core.Rendering.Shader;
import net.Core.Rendering.VBO;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL43.*;
import static net.GameLayer.ChunkGen.*;


public class Chunk {
    boolean updateChunk = true;
    protected FloatBuffer buffer;

    public BlockType[][][] blocks;
    VBO Ssbo;
    int faceDrawn = 0;
    int blockDrawn = 0;

    public int positionX, positionY, positionZ;

    public byte dx;
    public byte dz;

    public Chunk() { }

    public Chunk(Vector2f position, byte dx, byte dz) {
        this.positionX = (int)position.x;
        this.positionY = Y_CHUNK;
        this.positionZ = (int)position.y;

        this.dx = dx;
        this.dz = dz;

    }

    public void Delete() {
        if(Ssbo != null)
            Ssbo.Delete();
        blocks = null;
        buffer = null;
    }



    public void updateChunk(int xx, int zz) {
        if(!updateChunk)
            return;

        buffer = World.getChunkData(xx, zz);
        faceDrawn = buffer.limit()/11;

        Ssbo.Bind();
        Ssbo.InitSSBO(buffer,0);
        Ssbo.UnBind();
        MemoryUtil.memFree(buffer);
        updateChunk = false;
    }

    public void DrawMesh() {

        Ssbo.BindBase(0);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 6* faceDrawn);
    }

    public void Init() {
        if (!GL.getCapabilities().OpenGL30) {
            Logger.log(Logger.Level.ERROR, "OpenGL 4.3 not supported");
        }

        Ssbo = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
    }

}
