package se.scalablesolutions.akka.api;

import se.scalablesolutions.akka.kernel.state.*;
import se.scalablesolutions.akka.annotation.transactional;
import se.scalablesolutions.akka.annotation.state;

public class PersistentStateful {
  private TransactionalState factory = new TransactionalState();
  private TransactionalMap mapState =       factory.newMap(new PersistentMapConfig(new CassandraStorageConfig()));
  private TransactionalVector vectorState = factory.newVector(new PersistentVectorConfig(new CassandraStorageConfig()));;
  private TransactionalRef refState =       factory.newRef(new PersistentRefConfig(new CassandraStorageConfig()));

  @transactional
  public String getMapState(String key) {
    return (String) mapState.get(key).get();
  }

  @transactional
  public String getVectorState(int index) {
    return (String) vectorState.get(index);
  }

  @transactional
  public String getRefState() {
    if (refState.isDefined()) {
      return (String) refState.get().get();
    } else throw new IllegalStateException("No such element");
  }

  @transactional
  public void setMapState(String key, String msg) {
    mapState.put(key, msg);
  }

  @transactional
  public void setVectorState(String msg) {
    vectorState.add(msg);
  }

  @transactional
  public void setRefState(String msg) {
    refState.swap(msg);
  }

  @transactional
  public void success(String key, String msg) {
    mapState.put(key, msg);
    vectorState.add(msg);
    refState.swap(msg);
  }

  @transactional
  public void failure(String key, String msg, PersistentFailer failer) {
    mapState.put(key, msg);
    vectorState.add(msg);
    refState.swap(msg);
    failer.fail();
  }

  @transactional
  public void thisMethodHangs(String key, String msg, PersistentFailer failer) {
    setMapState(key, msg);
  }
}

