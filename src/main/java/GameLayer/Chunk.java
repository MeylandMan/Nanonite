package GameLayer;

import Mycraft.Debug.Logger;
import Mycraft.Rendering.VBO;

import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL43.*;
import static GameLayer.ChunkGen.*;


public class Chunk {
    boolean hasMesh = false;
    boolean updateChunk = true;
    protected FloatBuffer buffer;

    public byte[] blocks;
    public Map<Integer, Integer[]> compressedBlocks;
    VBO StaticBlocks, LiquidBlocks;
    int[] faceDrawn = new int[2];
    int blockDrawn = 0;

    public long positionX, positionY, positionZ;

    public Chunk() { }

    public Chunk(long x, long y, long z) {
        this.compressedBlocks = new HashMap<>();
        this.positionX = x;
        this.positionY = y;
        this.positionZ = z;

    }

    public void Delete() {
        if(StaticBlocks != null)
            StaticBlocks.Delete();
        StaticBlocks = null;
        blocks = null;
        buffer = null;
        compressedBlocks = null;
    }

    public byte getBlock(int x, int y, int z) {
        return (blocks == null)? -1 : blocks[index(x, y ,z, CHUNK_SIZE)];
    }

    public void AddBlock(int x ,int y, int z, BlockType type) {
        if(blocks == null)
            CreateBlocksArray();

        blocks[index(x,y,z, CHUNK_SIZE)] = type.getID();
        blockDrawn++;
    }

    public void ReplaceBlock(int x ,int y, int z, BlockType type) {
        if(blocks == null)
            CreateBlocksArray();

        blocks[index(x,y,z, CHUNK_SIZE)] = type.getID();
    }

    public void CreateBlocksArray() {
        blocks = new byte[CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE];
        System.arraycopy(DefaultChunk, 0, blocks, 0, DefaultChunk.length);
    }

    public void updateChunk(long xx, long yy, long zz) {
        if(!updateChunk || blocks == null)
            return;

        updateChunk = false;

        // Updating Static blocks
        buffer = World.getChunkData(xx, yy, zz, 0);
        faceDrawn[0] = buffer.limit()/11;

        StaticBlocks.Bind();
        StaticBlocks.InitSSBO(buffer,0);
        StaticBlocks.UnBind();
        MemoryUtil.memFree(buffer);

        // Updating Liquid blocks
        buffer = World.getChunkData(xx, yy, zz, 1);
        faceDrawn[1] = buffer.limit()/11;

        LiquidBlocks.Bind();
        LiquidBlocks.InitSSBO(buffer,0);
        LiquidBlocks.UnBind();
        MemoryUtil.memFree(buffer);

    }

    public void DrawMesh() {

        StaticBlocks.BindBase(0);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 6* faceDrawn[0]);
    }

    public void DrawLiquidMesh() {

        LiquidBlocks.BindBase(0);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 6* faceDrawn[1]);
    }

    public void Init() {
        if (!GL.getCapabilities().OpenGL30) {
            Logger.log(Logger.Level.ERROR, "OpenGL 4.3 not supported");
        }

        StaticBlocks = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
        LiquidBlocks = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
    }

}
