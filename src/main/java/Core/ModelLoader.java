package Core;


import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

enum ModelType {
    BLOCKS,
    ENTITY
}

public class ModelLoader {
    private static final Gson gson = new Gson();
    private final Map<String, BlockModel> BlockModels = new HashMap<>();
    private final Map<String, EntityModel> EntityModels = new HashMap<>();

    public Model loadModel(String modelName, ModelType type) throws IOException {
        // Check if the model is already loaded
        if (BlockModels.containsKey(modelName)) {
            return BlockModels.get(modelName);
        }

        // Loading the Model's JSON file

        String modelFilePath = Client.assetsPath + "models/" + modelName + ".json";
        FileReader reader = new FileReader(modelFilePath);
        Model model = null;

        if(type == ModelType.BLOCKS)
            model = gson.fromJson(reader, BlockModel.class);
        else if(type == ModelType.ENTITY)
            model = gson.fromJson(reader, EntityModel.class);

        reader.close();

        // If the Model has a parent, solve the inheritance
        if (model.getParent() != null && !model.getParent().isEmpty()) {
            Model parentModel = loadModel(model.getParent(), type); // Load recursively the parent
            mergeModels(model, parentModel, type); // Merge the parent datas to the actual Model
        }

        Logger.log(Logger.Level.INFO, "Loading model " + modelName);
        AddModel(modelName, model, type);

        return model;
    }

    private void AddModel(String modelName, Model model, ModelType type) {
        switch(type) {
            case BLOCKS:
                BlockModel blockModel = new BlockModel();
                blockModel.setElements(model.getElements());
                blockModel.setTextures(model.getTextures());
                blockModel.setParent(model.getParent());
                blockModel.setType(model.getType());

                resolveTextures(blockModel);
                BlockModels.put(modelName, blockModel);

                break;
            case ENTITY:
                EntityModel entityModel = new EntityModel();
                entityModel.setElements(model.getElements());
                entityModel.setTexture(model.getTexture());
                entityModel.setParent(model.getParent());
                entityModel.setType(model.getType());

                EntityModels.put(modelName, entityModel);
                break;
        }
    }

    private void mergeModels(Model model, Model parentModel, ModelType type) {

        // Merge the textures (priority to those from the actual model)

        switch(type) {
            case BLOCKS:
                if (parentModel.getTextures() != null) {
                    if (model.getTextures() == null) {
                        model.setTextures(new HashMap<>(parentModel.getTextures()));
                    } else {
                        model.getTextures().putAll(parentModel.getTextures());
                    }
                }
                break;
            case ENTITY:
                if (parentModel.getTexture() != null) {
                    model.setTexture(parentModel.getTexture());
                }
                break;

        }

        // Merge elements (priority to those from the actual model)
        if (parentModel.getElements() != null) {
            if (model.getElements() == null) {
                model.setElements(new ArrayList<>(parentModel.getElements()));
            } else {
                model.getElements().addAll(parentModel.getElements());
            }

            // Merge types
            model.setType(parentModel.getType());
        }

    }

    private void resolveTextures(Model model) {
        if (model.getTextures() == null) {
            return;
        }

        Map<String, String> textures = model.getTextures();
        for (Map.Entry<String, String> entry : textures.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Check if the value is an # (start with #)
            if (value.startsWith("#")) {
                String alias = value.substring(1); // Remove the #
                if (textures.containsKey(alias)) {
                    textures.put(key, textures.get(alias)); // Replace the ref with the true texture
                }
            }
        }
    }

    public BlockModel getModel(String modelName) {
        return BlockModels.get(modelName);
    }

    public EntityModel getEntityModel(String modelName) {
        return EntityModels.get(modelName);
    }

    public Map<String, BlockModel> getBlockModels() {
        return BlockModels;
    }
}
