package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.mygdx.maze.MazeCell;
import com.mygdx.maze.MazeGenerator;
import java.util.ArrayList;
import java.util.List;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {

    //SpriteBatch batch;
    //Texture img;
    // camera
    public PerspectiveCamera cam;
    public CameraInputController camController;
    public ModelBatch modelBatch;
    // models
    public Model model;
    public ModelInstance instance;
    public Model bottomWall;
    public Model rightWall;
    public Model cornerWall;
    public List<ModelInstance> instances;
    //lighting
    public Environment environment;

    @Override
    public void create() {
        //batch = new SpriteBatch();
        //img = new Texture("badlogic.jpg");

        initEnviroment();
        initCamera();
        initModels();
        initMaze();
    }

    private void initCamera() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
    }

    private void initEnviroment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initModels() {
        modelBatch = new ModelBatch();

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(0.1f, 0.1f, 0.1f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                Usage.Position | Usage.Normal);
        instance = new ModelInstance(model);
    }

    private void initMaze() {
        ModelBuilder mb = new ModelBuilder();
        instances = new ArrayList<ModelInstance>();
        rightWall = mb.createBox(1f, 1f, 0.1f,
                new Material(ColorAttribute.createDiffuse(Color.RED)),
                Usage.Position | Usage.Normal);
        bottomWall = mb.createBox(0.1f, 1f, 1f,
                new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                Usage.Position | Usage.Normal);
        cornerWall = mb.createBox(0.1f, 1f, 0.1f,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                Usage.Position | Usage.Normal);
        MazeGenerator generator = new MazeGenerator(25, 25);
        generator.generate();
        MazeCell[][] grid = generator.getMazeAsGrid();
        // maze if flipped over "z" axis relative to test code in maze generator
        for (int i = 0; i < generator.getRows(); i++) {
            for (int j = 0; j < generator.getColumns(); j++) {
                MazeCell c = grid[i][j];
                if (c.hasBottomWall()) {
                    ModelInstance bw = new ModelInstance(bottomWall);
                    bw.transform.setTranslation(i + 0.45f, 0.5f, j);
                    instances.add(bw);
                }
                if (c.hasRightWall()) {
                    ModelInstance bw = new ModelInstance(rightWall);
                    bw.transform.setTranslation(i, 0.5f, j + 0.45f);
                    instances.add(bw);
                }

                if (c.hasBottomWall() == false && c.hasRightWall() == false) {
                    ModelInstance cw = new ModelInstance(cornerWall);
                    cw.transform.setTranslation(i + 0.45f, 0.5f, j + 0.45f);
                    instances.add(cw);
                }
            }
        }
    }

    // might casuse some catch misses

    private final Quaternion rotQCamHelp = new Quaternion();
    private void handleInputs() {
        float x = 0;
        float z = 0;
        float rotY = 0;
        float rotY2 = cam.view.getRotation(rotQCamHelp).getAngleAround(0, 1, 0);
        float rad = (float)Math.toRadians(rotY2);
        float sin = (float)Math.sin(rad)*0.1f;
        float cos = (float)Math.cos(rad)*0.1f;
        if (Gdx.input.isKeyPressed(Keys.W)) {
            x += sin;
            z -= cos;
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            x -= sin;
            z += cos;
        }
        if (Gdx.input.isKeyPressed(Keys.A)) {
            rotY = 1;
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            rotY = -1;
        }
        // uses degrees
        cam.rotate(rotY, 0, 1, 0);
        cam.translate(x, 0, z);
    }

    @Override
    public void render() {
        //Gdx.gl.glClearColor(1, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //batch.begin();
        //batch.draw(img, 0, 0);
        //batch.end();
        handleInputs();
        cam.update();
        //camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        modelBatch.begin(cam);
        modelBatch.render(instance, environment);
        for (ModelInstance i : instances) {
            modelBatch.render(i, environment);
        }
        modelBatch.end();
    }

    @Override
    public void dispose() {
        //batch.dispose();
        //img.dispose();
        modelBatch.dispose();
        model.dispose();

        bottomWall.dispose();
        rightWall.dispose();
        cornerWall.dispose();
    }
}
