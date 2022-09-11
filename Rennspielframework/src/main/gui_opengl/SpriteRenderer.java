package gui_opengl;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class SpriteRenderer {

    //Render State
    private Shader shader;
    private int quadVAO;

    //Constructor( inits shaders/shapes)
    public SpriteRenderer(Shader shader){
        this.shader = shader;
        initRenderData();
    }

    //Render a definied quad textured with given sprite
    public void DrawSprite(Texture texture, Vector2f position, Vector2f size, Vector3f color){
        this.shader.use();
        Matrix4f model = new Matrix4f();
        model = model.identity();
        model.translate(new Vector3f(position.x, position.y, 0.0f)); //first translate(tranformationare: scale happens first, than rotation, and than final translation happens, reversed order

        model.scale(new Vector3f(size.x, size.y, 1.0f)); //scale

        this.shader.uploadMat4f("model", model);
        //render textured quad
        this.shader.uploadVec3f("spriteColor", color);

        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glBindVertexArray(quadVAO);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);

    }

    private void initRenderData(){
        int VBO;
        /*float vertices[] = {
// p            //pos      // tex
                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f,

                0.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 0.0f
        };*/
        float vertices[] = {
// p            //pos      // tex
                0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

                0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f
        };

        quadVAO = glGenVertexArrays();
        VBO = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindVertexArray(quadVAO);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0,4, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }
}
