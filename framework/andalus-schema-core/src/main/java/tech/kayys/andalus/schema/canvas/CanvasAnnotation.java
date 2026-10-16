package tech.kayys.andalus.schema.canvas;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import tech.kayys.andalus.schema.common.Position;
import tech.kayys.andalus.schema.layout.Dimensions;
import tech.kayys.andalus.schema.data.AnnotationStyle;

/**
 * Canvas annotations/comments
 */
public class CanvasAnnotation {
    public String id;
    public String text;
    public Position position;
    public Dimensions dimensions;
    public String createdBy;
    public Instant createdAt;
    public List<String> attachedToNodeIds = new ArrayList<>();
    public AnnotationStyle style = new AnnotationStyle();
}
