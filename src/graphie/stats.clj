(ns
  graphie.stats
  (:require [clojure.contrib.math :as math]))

(defonce stats-agent (agent {}))

(defn seconds-from-millis [millis]
  (long (Math/floor (/ millis 1000))))

(defn names []
  (keys @stats-agent))

(defn- second-to-point [second]
  [(* (:s second) 1000) (long (math/round (/ (reduce + (:v second)) (count (:v second)))))])

(defn- include-values [second]
  (< (- (seconds-from-millis (System/currentTimeMillis)) 60) (:s second))
  true)

(defn values [name]
  (map second-to-point (filter include-values (:secs (name @stats-agent)))))

(defn- start-new-second
  [message key-stats]
    (merge
      (if (contains? key-stats :in)
        (assoc key-stats :secs (conj (get key-stats :secs []) {:v (:in key-stats) :s (:s key-stats)}))
        {})
      {:in [(:value message)], :s (seconds-from-millis (:time message))}))

(defn- add-value [message key-stats]
  (assoc key-stats :in (conj (get key-stats :in []) (:value message))))

(defn- different-second? [message key-stats]
  (not (= (:s key-stats) (seconds-from-millis (:time message)))))

(defn- merge-stats [message key-stats]
  (if (different-second? message key-stats)
    (start-new-second message key-stats)
    (add-value message key-stats)))

(defn merge-time-message [data message]
  (let [key (keyword (:name message))
        key-stats (key data {})]
    (assoc data key (merge-stats message key-stats))))

(defn record-stats [message]
  (if (= "ms" (:type message))
    (send stats-agent merge-time-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))
