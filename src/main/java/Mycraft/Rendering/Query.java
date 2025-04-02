package Mycraft.Rendering;
import static org.lwjgl.opengl.GL46.*;

public class Query {
    private int m_ID;

    public Query() {
        m_ID = glGenQueries();
    }

    public void startQuery() {
        glBeginQuery(GL_PRIMITIVES_GENERATED, m_ID);
    }

    public void endQuery() {
        glEndQuery(GL_PRIMITIVES_GENERATED);
    }

    public int getPrimitivesGenerated() {
        return glGetQueryObjecti(m_ID, GL_QUERY_RESULT);
    }

    public void startTransformFeedbackQuery() {
        glBeginQuery(GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN, m_ID);
    }

    public void endTransformFeedbackQuery() {
        glEndQuery(GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN);
    }

    public int getTrianglesRendered() {
        return glGetQueryObjecti(m_ID, GL_QUERY_RESULT);
    }

    public void startVerticesQuery() {
        glBeginQuery(GL_VERTICES_SUBMITTED, m_ID);
    }

    public void endVerticesQuery() {
        glEndQuery(GL_VERTICES_SUBMITTED);
    }

    public int getVerticesRendered() {
        return glGetQueryObjecti(m_ID, GL_QUERY_RESULT);
    }
}
