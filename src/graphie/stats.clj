(ns
  graphie.stats
  (:require [graphie.time :as time]
            [clojure.contrib.math :as math]))

(defonce ^:private stats-agent (agent {}))

(defn- second-to-point [second]
  [(* (:second second) 1000) (long (math/round (/ (reduce + (:v second)) (count (:v second)))))])

(defn names []
  (keys @stats-agent))

(defn values [name]
  (map second-to-point (:secs (name @stats-agent))))

(defn- second-summary [values second]
  (let [sum (reduce + values)]
    {:second second
     :avg (long (math/round (/ sum (count values))))
     :sum sum
     :max (reduce max values)
     :min (reduce min values)
     :samples (count values)}))

(defn- start-second [stats value time]
  (merge
    (if (contains? stats :in)
      (assoc stats :secs (conj (get stats :secs []) (second-summary (:in stats) (:second stats))))
      {})
    {:in [value], :second (time/as-seconds time)}))

(defn- add-value [stats value]
  (assoc stats :in (conj (get stats :in []) value)))

(defn- same-second? [message stats]
  (= (:second stats) (time/as-seconds (:time message))))

(defn merge-message [data message]
  (let [key (keyword (:name message))
        stats (key data {})]
    (assoc data key
      (if (same-second? message stats)
        (add-value stats (:value message))
        (start-second stats (:value message) (:time message))))))

(defn record-stats [message]
  (if (= "v" (:type message))
    (send stats-agent merge-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))

