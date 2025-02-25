package Core;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader {
    private static final Gson gson = new Gson();
    private final Map<String, BlockModel> models = new HashMap<>(); //Model cache

    public BlockModel loadModel(String modelName) throws IOException {
        // Check if the model is already loaded
        if (models.containsKey(modelName)) {
            return models.get(modelName);
        }

        // Loading the Model's JSON file
        String modelFilePath = Paths.get("assets", "models", modelName + ".json").toString();
        FileReader reader = new FileReader(modelFilePath);
        BlockModel model = gson.fromJson(reader, BlockModel.class);
        reader.close();

        // If the Model has a parent, solve the inheritance
        if (model.getParent() != null && !model.getParent().isEmpty()) {
            BlockModel parentModel = loadModel(model.getParent()); // Load recursively the parent
            mergeModels(model, parentModel); // Merge the parent datas to the actual Model
        }

        models.put(modelName, model); // Put the Model in cache
        return model;
    }

    private void mergeModels(BlockModel model, BlockModel parentModel) {
        // Merge the textures (priority to those from the actual model)
        if (model.getTextures() == null) {
            model.setTextures(parentModel.getTextures());
        } else {
            model.getTextures().putAll(parentModel.getTextures());
        }

        // Fusionner les éléments (priorité à ceux du modèle actuel)
        if (model.getElements() == null) {
            model.setElements(parentModel.getElements());
        } else {
            model.getElements().putAll(parentModel.getElements());
        }
    }
}
