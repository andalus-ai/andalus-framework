package tech.kayys.andalus.input;
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
 * Input Types
 */
public enum InputType {
    TEXT,
    VOICE,
    EMAIL,
    WHATSAPP,
    SLACK,
    DISCORD,
    REST_API,
    KAFKA,
    MQTT,
    FILE,
    SENSOR,
    EVENT_BUS,
    WEBSOCKET,
    GRPC,
    WEBHOOK
}