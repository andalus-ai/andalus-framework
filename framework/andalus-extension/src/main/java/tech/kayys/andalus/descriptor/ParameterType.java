package tech.kayys.andalus.descriptor;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;




/**
 * Types of parameters.
 */
public enum ParameterType {
    STRING,
    NUMBER,
    INTEGER,
    BOOLEAN,
    ARRAY,
    OBJECT,
    FILE,
    DATE,
    DATETIME,
    ENUM,
    REFERENCE,
    ANY
}
