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

(defn seconds [metric]
  (mc/find-maps (coll-name metric)))

