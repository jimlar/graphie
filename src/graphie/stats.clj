(ns
  graphie.stats
  (:require [graphie.time :as time]
            [clojure.contrib.math :as math]))

(defonce ^:private stats-agent (agent {}))

(defn names []
  (keys @stats-agent))

(defn- second-to-point [second]
  [(* (:second second) 1000) (long (math/round (/ (reduce + (:v second)) (count (:v second)))))])

(defn- include-values [second]
  (< (- (time/as-seconds (System/currentTimeMillis)) 60) (:second second))
  true)

(defn values [name]
  (map second-to-point (filter include-values (:secs (name @stats-agent)))))

(defn- finish-second
  [message key-stats]
    (merge
      (if (contains? key-stats :in)
        (assoc key-stats :secs (conj (get key-stats :secs []) {:v (:in key-stats) :second (:second key-stats)}))
        {})
      {:in [(:value message)], :second (time/as-seconds (:time message))}))

(defn- add-value [message key-stats]
  (assoc key-stats :in (conj (get key-stats :in []) (:value message))))

(defn- different-second? [message key-stats]
  (not (= (:second key-stats) (time/as-seconds (:time message)))))

(defn- merge-stats [message key-stats]
  (if (different-second? message key-stats)
    (finish-second message key-stats)
    (add-value message key-stats)))

(defn merge-time-message [data message]
  (let [key (keyword (:name message))
        key-stats (key data {})]
    (assoc data key (merge-stats message key-stats))))

(defn record-stats [message]
  (if (= "v" (:type message))
    (send stats-agent merge-time-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))
