/**
 * ------------------------------------------------------------------------- *
 *                     Copyright (c) by FeatherCore 2024                     *
 * ------------------------------------------------------------------------- *
 * @license https://github.com/TheAncientOwl/feather-core/blob/main/LICENSE
 *
 * @file AbstractDAO.java
 * @author Alexandru Delegeanu
 * @version 0.1
 * @description MongoDB morphia data accessor base class
 */

package mc.owls.valley.net.feathercore.modules.data.mongodb.api.accessors;

import java.util.List;
import java.util.UUID;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;

public abstract class AbstractDAO<T> {
    private final Datastore datastore;

    public AbstractDAO(final Datastore datastore) {
        this.datastore = datastore;
    }

    public T save(T entity) {
        entity = datastore.save(entity);
        return entity;
    }

    /**
     * @deprecated As of FeatherCore 0.2.3 use {{@link #get(UUID)}}
     */
    @Deprecated
    public T get(String id) {
        return datastore.find(getEntityClass()).filter(Filters.eq("_id", id)).first();
    }

    public T get(UUID uuid) {
        return datastore.find(getEntityClass()).filter(Filters.eq("_id", uuid)).first();

    }

    public List<T> findAll(String field, String value) {
        return datastore.find(getEntityClass()).filter(Filters.eq(field, value)).stream().toList();
    }

    public List<T> findLessThan(String field, Object value) {
        return datastore.find(getEntityClass()).filter(Filters.lt(field, value)).stream().toList();
    }

    public List<T> findGreaterThan(String field, Object value) {
        return datastore.find(getEntityClass()).filter(Filters.gt(field, value)).stream().toList();
    }

    public T findFirst(String field, String value) {
        return findAll(field, value).get(0);
    }

    public void delete(String id) {
        Query<T> query = datastore.find(getEntityClass()).filter(Filters.eq("_id", id));
        T first = query.first();

        if (first != null) {
            datastore.delete(first);
        }
    }

    public void delete(T ob) {
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
