(ns graphie.storagetest
  (:use midje.sweet)
  (:use graphie.storage))

(fact "Correct collection name from metric keyword"
  (coll-name :response_time) => "response_time")

(fact "Correct metric from collection name"
  (metric-name "response_time") => :response_time)

(fact "Collection starting with system. is considered a system collection"
  (system-coll? "system.indexes") => true)

(fact "Collection starting with fs. is considered a system collection"
  (system-coll? "fs.chunks") => true
  (system-coll? "fs.files") => true)

