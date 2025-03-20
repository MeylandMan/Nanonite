package net.Core.Rendering;


import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL43.*;

public class Shader {
    private int m_ID;
    public void Bind() {
        glUseProgram(m_ID);
    }

    public void UnBind() {
        glUseProgram(0);
    }

    public void Clear() {
        glDeleteProgram(m_ID);
    }

    public void CreateShader(String vertexFile, String fragmentFile) {

        int vertexShaderId = loadShader("assets/shaders/" + vertexFile, GL_VERTEX_SHADER);
        int fragmentShaderId = loadShader("assets/shaders/" + fragmentFile, GL_FRAGMENT_SHADER);

        m_ID = glCreateProgram();
        glAttachShader(m_ID, vertexShaderId);
        glAttachShader(m_ID, fragmentShaderId);
        glLinkProgram(m_ID);
        glValidateProgram(m_ID);

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    private int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Impossible de lire le fichier shader: " + file);
        }


        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);

        // Vérifier les erreurs de compilation
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shaderId, 1024);
            System.err.println("Error compiling the shader: " + infoLog);
            throw new RuntimeException("Error compiling the shader: " + file);
        }
        return shaderId;
    }
    public int getID() {
        return m_ID;
    }
    public int getUniform(String name) {
        return glGetUniformLocation(m_ID, name);
    }

    public void Uniform1f(String name, float x) {
        int location = getUniform(name);
        glUniform1f(location, x);
    }
    public void Uniform2f(String name, float x, float y) {
        int location = getUniform(name);
        glUniform2f(location, x, y);
    }
    public void Uniform2f(String name, Vector2f vector) {
        int location = getUniform(name);
        glUniform2f(location, vector.x, vector.y);
    }

    public void Uniform3f(String name, float x, float y, float z) {
        int location = getUniform(name);
        glUniform3f(location, x, y, z);
    }

    public void Uniform3d(String name, double x, double y, double z) {
        int location = getUniform(name);
        glUniform3d(location, x, y, z);
    }

    public void Uniform3f(String name, Vector3f vector) {
        int location = getUniform(name);
        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    public void Uniform4f(String name, float x, float y, float z, float w) {
        int location = getUniform(name);
        glUniform4f(location, x, y, z, w);
    }
    public void Uniform4f(String name, Vector4f vector) {
        int location = getUniform(name);
        glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    public void Uniform1i(String name, int x) {
        int location = getUniform(name);
        glUniform1i(location, x);
    }
    public void Uniform1iv(String name, int[] x) {
        int location = getUniform(name);
        glUniform1iv(location, x);
    }

    public void Uniform1fv(String name, FloatBuffer x) {
        int location = getUniform(name);
        glUniform1fv(location, x);
    }

    public void Uniform4fv(String name, FloatBuffer x) {
        int location = getUniform(name);
        glUniform4fv(location, x);
    }

    public void Uniform4fv(String name, float[] x) {
        int location = getUniform(name);
        glUniform4fv(location, x);
    }

    public void Uniform1iv(String name, IntBuffer x) {
        int location = getUniform(name);
        glUniform1iv(location, x);
        MemoryUtil.memFree(x);
    }

    public void Uniform2i(String name, int x, int y) {
        int location = getUniform(name);
        glUniform2i (location, x, y);
    }
    public void Uniform2i(String name, Vector2i vector) {
        int location = getUniform(name);
        glUniform2i(location, vector.x, vector.y);
    }

    public void Uniform3i(String name, int x, int y, int z) {
        int location = getUniform(name);
        glUniform3i(location, x, y, z);
    }
    public void Uniform3i(String name, Vector3i vector) {
        int location = getUniform(name);
        glUniform3i(location, vector.x, vector.y, vector.z);
    }

    public void Uniform4i(String name, int x, int y, int z, int w) {
        int location = getUniform(name);
        glUniform4i(location, x, y, z, w);
    }
    public void Uniform4i(String name, Vector4i vector) {
        int location = getUniform(name);
        glUniform4i(location, vector.x, vector.y, vector.z, vector.w);
    }

    public void UniformMatrix2x2(String name, Matrix2f matrix) {
        int location = getUniform(name);
        assert(location != -1);
        glUniformMatrix2fv(location, false, matrix.get(new float[4]));
    }
    public void UniformMatrix3x3(String name, Matrix3f matrix) {
        int location = getUniform(name);
        assert(location != -1);
        glUniformMatrix3fv(location, false, matrix.get(new float[9]));
    }
    public void UniformMatrix4x4(String name, Matrix4f matrix) {
        int location = getUniform(name);
        assert(location != -1);
        glUniformMatrix4fv(location, false, matrix.get(new float[16]));
    }

    public void UniformMatrix4x4d(String name, Matrix4d matrix) {
        int location = getUniform(name);
        assert(location != -1);
        glUniformMatrix4dv(location, false, matrix.get(new double[16]));
    }

}


