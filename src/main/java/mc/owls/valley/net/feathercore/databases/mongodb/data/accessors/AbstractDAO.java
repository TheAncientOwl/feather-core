package mc.owls.valley.net.feathercore.databases.mongodb.data.accessors;

import java.util.List;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;

public abstract class AbstractDAO<T> {
    private final Datastore datastore;

    public AbstractDAO(@NotNull Datastore datastore) {
        this.datastore = datastore;
    }

    public T save(T entity) {
        entity = datastore.save(entity);
        return entity;
    }

    public T get(@NotNull String id) {
        return datastore.find(getEntityClass()).filter(Filters.eq("_id", id)).first();
    }

    public T get(@NotNull UUID uuid) {
        return datastore.find(getEntityClass()).filter(Filters.eq("_id", uuid)).first();

    }

    public List<T> findAll(@NotNull String field, @NotNull String value) {
        return datastore.find(getEntityClass()).filter(Filters.eq(field, value)).stream().toList();
    }

    public List<T> findLessThan(@NotNull String field, @NotNull Object value) {
        return datastore.find(getEntityClass()).filter(Filters.lt(field, value)).stream().toList();
    }

    public List<T> findGreaterThan(@NotNull String field, @NotNull Object value) {
        return datastore.find(getEntityClass()).filter(Filters.gt(field, value)).stream().toList();
    }

    public T findFirst(@NotNull String field, @NotNull String value) {
        return findAll(field, value).get(0);
    }

    public void delete(@NotNull String id) {
        Query<T> query = datastore.find(getEntityClass()).filter(Filters.eq("_id", id));
        T first = query.first();

        if (first != null) {
            datastore.delete(first);
        }
    }

    public void delete(@NotNull T ob) {
        datastore.delete(ob);
    }

    public T findLast() {
        List<T> results = datastore.find(getEntityClass()).stream().toList();

        return results.get(results.size() - 1);
    }

    public List<T> findAll() {
        return datastore.find(getEntityClass()).stream().toList();
    }

    /**
     * Class we are trying to use for this DAO
     *
     * @return Class of this type
     */
    protected abstract Class<T> getEntityClass();

}
