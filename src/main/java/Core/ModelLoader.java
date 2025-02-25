package Core;


import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelLoader {
    private static final Gson gson = new Gson();
    private final Map<String, BlockModel> models = new HashMap<>();

    public BlockModel loadModel(String modelName) throws IOException {
        // Check if the model is already loaded
        if (models.containsKey(modelName)) {
            return models.get(modelName);
        }

        // Loading the Model's JSON file

        String modelFilePath = "assets/models/" + modelName + ".json";
        FileReader reader = new FileReader(modelFilePath);
        BlockModel model = gson.fromJson(reader, BlockModel.class);
        reader.close();

        // If the Model has a parent, solve the inheritance
        if (model.getParent() != null && !model.getParent().isEmpty()) {
            BlockModel parentModel = loadModel(model.getParent()); // Load recursively the parent
            mergeModels(model, parentModel); // Merge the parent datas to the actual Model
        }

        Logger.log(Logger.Level.INFO, "Loading model " + modelName);
        models.put(modelName, model); // Put the Model in cache
        return model;
    }

    private void mergeModels(BlockModel model, BlockModel parentModel) {

        // Merge the textures (priority to those from the actual model)
        if (parentModel.getTextures() != null) {
            if (model.getTextures() == null) {
                model.setTextures(new HashMap<>(parentModel.getTextures()));
            } else {
                model.getTextures().putAll(parentModel.getTextures());
            }
        }

        // Merge elements (priority to those from the actual model)
        if (parentModel.getElements() != null) {
            if (model.getElements() == null) {
                model.setElements(new ArrayList<>(parentModel.getElements()));
            } else {
                model.getElements().addAll(parentModel.getElements());
            }
        }
    }

    public BlockModel getModel(String modelName) {
        return models.get(modelName);
    }
}
