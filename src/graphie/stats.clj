(ns
  graphie.stats
  (:require [graphie.time :as time]
            [graphie.storage :as storage]))

(defonce ^:private stats-agent (agent {}))

(defn- second-summary [values second]
  (let [sum (reduce + values)]
    {:second second
     :average (long (Math/round (double (/ sum (count values)))))
     :sum sum
     :max (reduce max values)
     :min (reduce min values)
     :samples (count values)}))

(defn- start-second [key stats value time]
  (if (contains? stats :in)
    (storage/store key (second-summary (:in stats) (:second stats))))
  {:in [value], :second (time/as-seconds time)})

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
        (start-second key stats (:value message) (:time message))))))

(defn record-stats [message]
  (if (= "v" (:type message))
    (send stats-agent merge-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))

