package com.seakernel.stardroid.model;

import android.opengl.GLES20;
import android.support.annotation.CallSuper;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Calvin on 2/27/16.
 */
public abstract class StardroidShape {
    // Vertex Shader keys
    public static final String POSITION = "position";
    public static final String MODEL_VIEW = "modelView";
    public static final String MVP_MATRIX = "uMVPMatrix";
    public static final String TEXTURE_UNIT = "textureUnit";
    public static final String TEXTURE_COORDINATE = "textureCoordinate";

    // Fragment Shader keys
    public static final String COLOR_VARYING = "vColor";
    public static final String POSITION_VARYING = "vPosition";
    public static final String TEXTURE_COORDINATE_VARYING = "textureCoordinateVarying";

    // This String contains the vertex shader source code required by GLES to draw
    protected static final String TEXTURE_VERTEX_SHADER_SOURCE =
            "precision mediump float;\n"
                    + "uniform mat4 " + MODEL_VIEW + ";\n"
                    + "attribute vec3 " + POSITION + ";\n"
                    + "attribute vec2 " + TEXTURE_COORDINATE + ";\n"

                    + "varying vec2 " + TEXTURE_COORDINATE_VARYING + ";\n"
                    + "\n"
                    + "void main()\n"
                    + "{\n"
                    + "   gl_Position = " + MODEL_VIEW + " * vec4(" + POSITION + ", 1.0);\n"
                    + "   " + TEXTURE_COORDINATE_VARYING + " = " + TEXTURE_COORDINATE + ";\n"
                    + "}";

    // This String contains the fragment shader source code required for GLES to draw
    protected static final String TEXTURE_FRAGMENT_SHADER_SOURCE =
            "precision mediump float;\n"
                    + "uniform sampler2D " + TEXTURE_UNIT + ";\n"
                    + "varying vec2 " + TEXTURE_COORDINATE_VARYING + ";\n"
                    + "\n"
                    + "void main()\n"
                    + "{\n"
                    + "   vec4 mColor = texture2D(" + TEXTURE_UNIT + ", " + TEXTURE_COORDINATE_VARYING + ");\n"
                    + "   gl_FragColor = mColor;\n"
                    + "}";

    protected static final String COLOR_VERTEX_SHADER_SOURCE =
            "attribute vec4 " + POSITION_VARYING + ";"
                    + "uniform mat4 " + MVP_MATRIX + ";"
                    + "void main() {"
                    + "  gl_Position = " + MVP_MATRIX + " * " + POSITION_VARYING + ";"
                    + "}";

    protected static final String COLOR_FRAGMENT_SHADER_SOURCE =
            "precision mediump float;"
                    + "uniform vec4 " + COLOR_VARYING + ";"
                    + "void main() {"
                    + "  gl_FragColor = " + COLOR_VARYING + ";"
                    + "}";

    // Shader handles
    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;

    // Vertex variables
    private static final int COORDS_PER_VERTEX = 3;
    protected static float[] COORDINATES;
    private FloatBuffer mVertexBuffer;
    private int VERTEX_COUNT;
    private int VERTEX_STRIDE;

    // Set mColor with red, green, blue and alpha (opacity) values
    protected float mColor[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    private final int PROGRAM;
    protected float[] mMvMatrix;

    protected abstract void initialize();

    public StardroidShape() {
        initialize();

        COORDINATES = getCoordinates();
        VERTEX_COUNT = COORDINATES.length / COORDS_PER_VERTEX;
        VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                COORDINATES.length * 4);
        bb.order(ByteOrder.nativeOrder());
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(COORDINATES);
        mVertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());

        // create empty OpenGL ES Program
        PROGRAM = GLES20.glCreateProgram();

        GLES20.glAttachShader(PROGRAM, vertexShader);
        GLES20.glAttachShader(PROGRAM, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(PROGRAM);

        onCreateProgramHandles(PROGRAM);
    }

    @CallSuper
    protected void onCreateProgramHandles(final int PROGRAM) {
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(PROGRAM, POSITION_VARYING);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(PROGRAM, COLOR_VARYING);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(PROGRAM, MVP_MATRIX);
    }

    protected float[] getCoordinates() {
        return new float[] {
                -0.5f, -0.5f, -1.0f, // bottom left
                0.5f, -0.5f, 0.0f,  // bottom right
                -0.5f,  0.5f, 1.0f, // top left
                0.5f,  0.5f, 0.0f,  // top right
        };
    }

    public void update(long dt) {} // Default implementation to do nothing

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(PROGRAM);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, VERTEX_STRIDE, mVertexBuffer);

        // Set mColor for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, mColor, 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_COUNT);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    /**
     * Used to get the required vertex shader at runtime
     * Default return value is the mColor shader.
     *
     * @return either the mColor shader or texture shader
     */
    protected String getVertexShader() {
        return COLOR_VERTEX_SHADER_SOURCE;
    }

    /**
     * Used to get the required fragment shader at runtime
     * Default return value is the mColor shader.
     *
     * @return either the mColor shader or texture shader
     */
    protected String getFragmentShader() {
        return COLOR_FRAGMENT_SHADER_SOURCE;
    }

    protected static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        String shaderLog = GLES20.glGetShaderInfoLog(shader);

        if (shaderLog.length() > 0) {
            Log.e("SHADER_LOG", "Shader type :: " + type + "\n" + shaderLog);
        }

        return shader;
    }
}
