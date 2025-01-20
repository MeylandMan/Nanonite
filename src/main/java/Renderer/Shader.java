package Renderer;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

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
        int vertexShaderId = loadShader(vertexFile, GL_VERTEX_SHADER);
        int fragmentShaderId = loadShader(fragmentFile, GL_FRAGMENT_SHADER);

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
}
