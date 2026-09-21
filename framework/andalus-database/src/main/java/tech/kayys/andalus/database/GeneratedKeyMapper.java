package tech.kayys.andalus.database;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Generated Key Mapper
 */
@FunctionalInterface
public interface GeneratedKeyMapper<T> {
    T mapGeneratedKeys(ResultSet rs) throws SQLException;
}
