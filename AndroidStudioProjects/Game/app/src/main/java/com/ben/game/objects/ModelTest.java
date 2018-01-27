package com.ben.game.objects;

import android.content.Context;
import android.opengl.Matrix;

import java.nio.FloatBuffer;

/**
 * Created by Ben on 5/5/2017.
 */

public class ModelTest {
    public static final int POS_DAT_SIZE = 3;
    public static final int NOR_DAT_SIZE = 3;
    public static final int TEX_DAT_SIZE = 2;
    public static final int COL_DAT_SIZE = 4;
    public static final int BYTES_PER_FLOAT = 4;

    private int texture;
    private int color;
    private int trisCount;

    private boolean isInterleaved;
    private boolean isVBO;

    private Model model;
    private ModelInterleaved modelInterleaved;
    private ModelVBO modelVBO;
    private ModelVBOI modelVBOI;

    private float[] modelMatrix;

    private ModelTest(ModelBuilder builder) {
        this.modelMatrix = new float[16];
        Matrix.setIdentityM(modelMatrix,0);
        this.texture = builder.texture;
        this.color = builder.color;
        this.isInterleaved = builder.isInterleaved;
        this.isVBO = builder.isVBO;

        Context context = builder.context;
        String resourceId = builder.fileName;

        if (isInterleaved && isVBO) {
            modelVBOI = (ModelVBOI) new ModelVBOI.Builder(context, resourceId)
                    .texture(texture)
                    .color(color).build();
            this.trisCount = modelVBOI.getTrisCount();
        } else if (!isInterleaved && isVBO) {
            modelVBO = (ModelVBO) new ModelVBO.Builder(context, resourceId).texture
                    (texture)
                    .color(color).build();
            this.trisCount = modelVBO.getTrisCount();
        } else if (isInterleaved && !isVBO) {
            modelInterleaved = (ModelInterleaved) new ModelInterleaved.Builder
                    (context, resourceId).texture(texture).color(color).build();
            this.trisCount = modelInterleaved.getTrisCount();
        } else {
            model = (Model) new Model.Builder(context, resourceId).texture(texture)
                    .color(color).build();
            this.trisCount = model.getTrisCount();
        }
    }

    public static class ModelBuilder {
        // required params
        private Context context;
        private String fileName;
        // semi-optional params must have either color and/or texture
        private int texture = -1;
        private int color = -1;
        // optional params
        private boolean isInterleaved = false;
        private boolean isVBO = false;

        public ModelBuilder(Context context, String fileName) {
            this.context = context;
            this.fileName = fileName;
        }

        public ModelBuilder texture(int texture) {
            this.texture = texture;
            return this;
        }

        public ModelBuilder color(int color) {
            this.color = color;
            return this;
        }

        public ModelBuilder interleaved(){
            this.isInterleaved = true;
            return this;
        }

        public ModelBuilder bufferedObject(){
            this.isVBO = true;
            return this;
        }

        public ModelTest build() {
            return new ModelTest(this);
        }
    }

    public boolean hasTexture() {
        return (texture == -1) ? false : true;
    }

    public boolean hasColor() {
        return (color == -1) ? false : true;
    }

    public boolean isInterleaved() {
        return isInterleaved;
    }

    public boolean isVBO() {
        return isVBO;
    }

    public FloatBuffer getPosBuf() {
        return model.getPosBuf();
    }

    public FloatBuffer getColBuff() {
        return model.getColBuf();
    }

    public FloatBuffer getNorBuff() {
        return model.getNorBuf();
    }

    public FloatBuffer getTexBuff() {
        return model.getTexBuf();
    }

    public FloatBuffer getInterleavedBuff() {
        return modelInterleaved.getInterleavedBuf();
    }

    public int getPosBufIndx() {
        return modelVBO.getPosBufIndx();
    }

    public int getColBufIndx() {
        return modelVBO.getColBufIndx();
    }

    public int getNorBufIndx() {
        return modelVBO.getNorBufIndx();
    }

    public int getTexBufIndx() {
        return modelVBO.getTexBufIndx();
    }

    public int getInterleavedBuffIndx() {
        return modelVBOI.getIntlvdBufIndx();
    }

    public int getTrisCount() {
        return trisCount;
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }
}

// M, MC, MT, MCT
// MI, MIC, MIT, MICT
// MV, MVC, MVT, MVCT
// MVI, MVIC, MVIT, MVICT
// 16 differences
