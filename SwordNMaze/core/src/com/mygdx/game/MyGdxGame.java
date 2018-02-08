package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
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
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.ai.SimpleAI;
import com.mygdx.maze.MazeCell;
import com.mygdx.maze.MazeGenerator;
import com.mygdx.model.GameModel;
import com.mygdx.model.MazeBuilder;
import com.mygdx.model.Sword;
import com.mygdx.util2.TreeNode;
import com.mygdx.view.CustomCameraController;
import com.mygdx.view.RenderData;
import java.util.ArrayList;
import java.util.List;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {

    //SpriteBatch batch;
    //Texture img;
    // camera
    public PerspectiveCamera cam;
    public CustomCameraController camController;
    public ModelBatch modelBatch;
    // models
    public Model simpleAI;
    public List<GameModel> instances;
    //lighting
    public Environment environment;

    // ai
    private SimpleAI ai;

    private MazeGenerator generator;
    private MazeBuilder builder;

    private RenderData data;

    // private MergedCubes cubes;
    private Sword sword;
    private GameModel swordI;

    @Override
    public void create() {
        //batch = new SpriteBatch();
        //img = new Texture("badlogic.jpg");
        initEnviroment();
        initCamera();

        generator = new MazeGenerator(15, 15);
        generator.generate();
        builder = new MazeBuilder();
        builder.loadMazeModels(generator);

        data = new RenderData();
        data.onCreate();

        modelBatch = new ModelBatch();
        instances = new ArrayList<GameModel>();
        instances.addAll(builder.getMazeParts());

        initAI();

        // cubes = new MergedCubes();
        //  instances.add(cubes.get());
        sword = new Sword();
        sword.initModel();
        swordI = sword.getModelInstance();
        instances.add(swordI);
    }

    private void initCamera() {
        // 67
        cam = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //cam.position.set(10f, Settings.MAZE_SCALE/4f, 10f);
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0, 0, 0);
        cam.up.set(0, 1, 0);
        cam.near = 0.01f;
        //  cam.far = 300f;
        cam.far = 50f;
        cam.update();

        camController = new CustomCameraController(cam);
        Gdx.input.setInputProcessor(camController);
    }

    private PointLight pLight;
    private DirectionalLight dLight;

    private void initEnviroment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        //dLight = new DirectionalLight().set(Color.BLACK, new Vector3(1,1,1));
        //environment.add(dLight);

        pLight = new PointLight();
        pLight.color.add(1, 1, 1, 1);
        pLight.position.set(0, 0, 0);
        pLight.intensity = 10;
        environment.add(pLight);
    }

    private GameModel aimodel;

    private void initAI() {
        ModelBuilder mb = new ModelBuilder();
        simpleAI = mb.createBox(0.25f, 2f, 0.25f,
                new Material(ColorAttribute.createDiffuse(Color.PURPLE)),
                Usage.Position | Usage.Normal);

        aimodel = new GameModel(simpleAI);
        instances.add(aimodel);
        TreeNode<MazeCell> tree = generator.getMazeAsTree();
        ai = new SimpleAI(tree, tree.getChildren().get(0));
    }

    float xRot,yRot,zRot,rot;
    @Override
    public void render() {
        //Gdx.gl.glClearColor(1, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //batch.begin();
        //batch.draw(img, 0, 0);
        //batch.end();
        ai.update();
        ModelInstance mi = instances.get(instances.indexOf(aimodel));
        mi.transform.setTranslation(ai.getPosition().y, 1, ai.getPosition().x);
        Vector3 t = new Vector3();
        mi.transform.getTranslation(t);
        pLight.setPosition(t);

        camController.update();
        cam.update();
        
        //sword.placeInFrontOfCamera(cam);
        //sword.advanceTestAnim();
        //sword.pointAtTest(Gdx.input.getX(), Gdx.input.getY());
        sword.pointAtTest(cam, Gdx.input.getX(), Gdx.input.getY());

        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl20.glCullFace(GL20.GL_BACK);

        modelBatch.begin(cam);
        for (GameModel i : instances) {
            if (isVisible(cam, i)) {
                modelBatch.render(i, environment);
                data.setVisibleCount(data.getVisibleCount() + 1);
            }
        }
        modelBatch.end();

        data.onDraw();
        data.setVisibleCount(0);
    }

    private Vector3 position = new Vector3();

    private boolean isVisible(Camera cam, GameModel instance) {
        instance.transform.getTranslation(position);
        position.add(instance.getCenter());
        if(instance.transform.hasRotationOrScaling()==false){
            return cam.frustum.boundsInFrustum(position, instance.getDimensions());
        }
        return cam.frustum.sphereInFrustum(position, instance.getRadius());
    }

    @Override
    public void dispose() {
        //batch.dispose();
        //img.dispose();
        modelBatch.dispose();

        builder.dispose();

        simpleAI.dispose();

        // cubes.dispose();
    }
}
