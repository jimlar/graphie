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

(defn- finish-second
  [message key-stats]
    (merge
      (if (contains? key-stats :in)
        (assoc key-stats :secs (conj (get key-stats :secs []) {:v (:in key-stats) :second (:second key-stats)}))
        {})
      {:in [(:value message)], :second (time/as-seconds (:time message))}))

(defn- add-value [message key-stats]
  (assoc key-stats :in (conj (get key-stats :in []) (:value message))))

(defn- same-second? [message key-stats]
  (= (:second key-stats) (time/as-seconds (:time message))))

(defn add-value-message [data message]
  (let [key (keyword (:name message))
        key-stats (key data {})]
    (assoc data key
      (if (same-second? message key-stats)
        (add-value message key-stats)
        (finish-second message key-stats)))))

(defn record-stats [message]
  (if (= "v" (:type message))
    (send stats-agent add-value-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))
