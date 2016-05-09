package pt.ist.cmu.ubibike.httpserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class JSONSchemaValidation {

    public static final int TRAJECTORY = 1;
    public static final int POINTS_TRANSACTION = 2;
    public static final int REGISTER_USER = 3;
    public static final int AUTHENTICATE_USER = 4;
    public static final int BIKE_PICK_DROP = 5;

    private static final String BASE_PATH = "resource:/json_schemas/";
    private static final String TRAJECTORY_SCHEMA = "new_trajectory.json";
    private static final String POINTS_TRANSACTION_SCHEMA = "points.json";
    private static final String REGISTER_TRANSACTION_SCHEMA = "register.json";
    private static final String AUTHENTICATE_USER_SCHEMA = "authentication.json";
    private static final String BIKE_PICK_DROP_SCHEMA = "bike_pick_drop.json";

    private static String getFilePath(int code) {
        String schemaPath = BASE_PATH;

        if (code == TRAJECTORY) {
            schemaPath += TRAJECTORY_SCHEMA;
        } else if (code == POINTS_TRANSACTION) {
            schemaPath += POINTS_TRANSACTION_SCHEMA;
        } else if (code == REGISTER_USER) {
            schemaPath += REGISTER_TRANSACTION_SCHEMA;
        } else if (code == AUTHENTICATE_USER){
            schemaPath += AUTHENTICATE_USER_SCHEMA;
        } else if (code == BIKE_PICK_DROP){
            schemaPath += BIKE_PICK_DROP_SCHEMA;
        }
        else {
            schemaPath += TRAJECTORY_SCHEMA;
        }

        return schemaPath;
    }

    public static boolean validateSchema(String json, int schemaCode) {

        try {
            String schemaPath = getFilePath(schemaCode);
            JsonSchema schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaPath);
            JsonNode jsonNode = JsonLoader.fromString(json);

            return schema.validate(jsonNode).isSuccess();

        } catch (Exception e) {
            return false;
        }


    }


}
