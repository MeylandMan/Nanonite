package Renderer;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL20;

import static Renderer.API_CONTEXT.*;

public class Shader {
    private int m_ID;
    API api;
    public void Bind() {
        if(api == API.OPENGL)
            GL20.glUseProgram(m_ID);
    }

    public void UnBind() {
        if(api == API.OPENGL)
            GL20.glUseProgram(0);
    }

    public void Clear() {
        if(api == API.OPENGL)
            GL20.glDeleteProgram(m_ID);
    }

    public void CreateShader(API api, String vertexFile, String fragmentFile) {
        this.api = api;
        int vertexShaderId = 0;
        int fragmentShaderId = 0;
        switch(api) {
            case API.OPENGL:
                vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
                fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);

                m_ID = GL20.glCreateProgram();
                GL20.glAttachShader(m_ID, vertexShaderId);
                GL20.glAttachShader(m_ID, fragmentShaderId);
                GL20.glLinkProgram(m_ID);
                GL20.glValidateProgram(m_ID);

                GL20.glDeleteShader(vertexShaderId);
                GL20.glDeleteShader(fragmentShaderId);
                break;
        }
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

        int shaderId = 0;
        if(api == API.OPENGL) {
            shaderId = GL20.glCreateShader(type);
            GL20.glShaderSource(shaderId, shaderSource);
            GL20.glCompileShader(shaderId);

            // Vérifier les erreurs de compilation
            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
                String infoLog = GL20.glGetShaderInfoLog(shaderId, 1024);
                System.err.println("Error compiling the shader: " + infoLog);
                throw new RuntimeException("Error compiling the shader: " + file);
            }
        }
        return shaderId;
    }

    public int getID() {
        return m_ID;
    }
}
