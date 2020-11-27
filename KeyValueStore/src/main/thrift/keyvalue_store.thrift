exception SystemException {
  1: optional string message
}

struct ValueMetadata {
  1: i64 timestamp;
}

struct Value {
  1: optional ValueMetadata meta;
  2: optional string value;
}

struct Node {
  1: string ip;
  2: i32 port;
}

enum ConsistencyLevel {
    ONE,
    QUORUM
}

service KeyValueStore {

  string get(1: i32 key, 2: ConsistencyLevel level)
    throws (1: SystemException systemException),

  void put(1: i32 key, 2: string value, 3: ConsistencyLevel level)
    throws (1: SystemException systemException),

  Value read(1: i32 key)
    throws (1: SystemException systemException),

  void write(1: i32 key, 2: Value value)
    throws (1: SystemException systemException),

  map<i32, Value> getMissedWrites(1: Node node),

}