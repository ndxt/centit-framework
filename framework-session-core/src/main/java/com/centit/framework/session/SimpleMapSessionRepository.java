package com.centit.framework.session;

import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleMapSessionRepository implements FindByIndexNameSessionRepository<MapSession>, CentitSessionRepo {
    private Map<String, MapSession> sessionMap;
    public SimpleMapSessionRepository(){
        sessionMap = new ConcurrentHashMap<>(80);
    }
    /**
     * Find a {@link Map} of the session id to the {@link Session} of all sessions that
     * contain the specified index name index value.
     *
     * @param indexName  the name of the index (i.e.
     *                   {@link FindByIndexNameSessionRepository#PRINCIPAL_NAME_INDEX_NAME})
     * @param indexValue the value of the index to search for.
     * @return a {@code Map} (never {@code null}) of the session id to the {@code Session}
     * of all sessions that contain the specified index name and index value. If no
     * results are found, an empty {@code Map} is returned.
     */
    @Override
    public Map findByIndexNameAndIndexValue(String indexName, String indexValue) {
        return sessionMap;
    }

    /**
     * Creates a new {@link Session} that is capable of being persisted by this
     * {@link SessionRepository}.
     *
     * <p>
     * This allows optimizations and customizations in how the {@link Session} is
     * persisted. For example, the implementation returned might keep track of the changes
     * ensuring that only the delta needs to be persisted on a save.
     * </p>
     *
     * @return a new {@link Session} that is capable of being persisted by this
     * {@link SessionRepository}
     */
    @Override
    public MapSession createSession() {
        return new MapSession();
    }

    /**
     * Ensures the {@link Session} created by
     * {@link SessionRepository#createSession()} is saved.
     *
     * <p>
     * Some implementations may choose to save as the {@link Session} is updated by
     * returning a {@link Session} that immediately persists any changes. In this case,
     * this method may not actually do anything.
     * </p>
     *
     * @param session the {@link Session} to save
     */
    @Override
    public void save(MapSession session) {
        if (!session.getId().equals(session.getOriginalId())) {
            this.sessionMap.remove(session.getOriginalId());
        }
        this.sessionMap.put(session.getId(), new MapSession(session));
    }

    /**
     * Gets the {@link Session} by the {@link Session#getId()} or null if no
     * {@link Session} is found.
     *
     * @param id the {@link Session#getId()} to lookup
     * @return the {@link Session} by the {@link Session#getId()} or null if no
     * {@link Session} is found.
     */
    @Override
    public MapSession findById(String id) {
        Session saved = this.sessionMap.get(id);
        if (saved == null) {
            return null;
        }
        if (saved.isExpired()) {
            deleteById(saved.getId());
            return null;
        }
        return new MapSession(saved);
    }

    /**
     * Deletes the {@link Session} with the given {@link Session#getId()} or does nothing
     * if the {@link Session} is not found.
     *
     * @param id the {@link Session#getId()} to delete
     */
    @Override
    public void deleteById(String id) {
        sessionMap.remove(id);
    }

    @Override
    public void kickSessionByName(String loginName) {
        for(Map.Entry<String, MapSession> mapSessionEntry : sessionMap.entrySet()){
            if(loginName.equals(mapSessionEntry.getValue().getAttribute("username"))){
                sessionMap.remove(mapSessionEntry.getKey());
            }
        }
    }

    @Override
    public void kickSessionByPrincipal(String principalName) {
        for(Map.Entry<String, MapSession> mapSessionEntry : sessionMap.entrySet()){
            if(principalName.equals(mapSessionEntry.getValue().getAttribute("principal"))){
                sessionMap.remove(mapSessionEntry.getKey());
            }
        }
    }
}
