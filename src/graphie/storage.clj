(ns
  graphie.storage
  (:require [monger.core :as mg]
            [monger.db :as db]
            [monger.collection :as mc]))

(mg/connect-via-uri! "mongodb://127.0.0.1/graphie")

(defn system-coll? [name]
  (or
    (.startsWith name "system.")
    (.startsWith name "fs.")))

(defn coll-name [metric]
  (subs (str metric) 1))

(defn metric-name [collection]
  (keyword collection))

(defn store [metric second-data]
  (mc/insert (coll-name metric) second-data))

(defn names []
  (map metric-name (filter #(not (system-coll? %)) (db/get-collection-names))))

(defn- second-indexed-hash [values]
  (reduce
    #(assoc %1 (:second %2) %2)
    {}
    values))

(defn- fill-with-blanks [start end values]
  (let [seconds (second-indexed-hash values)]
    (map
      #(get seconds % {:second % :average 0 :sum 0 :max 0 :min 0 :samples 0})
      (range start end))))

(defn values [name start-second end-second]
  (fill-with-blanks start-second end-second (mc/find-maps (coll-name name) {:second {"$gte" start-second "$lt" end-second}})))
