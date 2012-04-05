(ns
  graphie.stats)

(defonce stats-agent (agent {}))

(defn seconds-from-nanos [nanos]
  (long (Math/floor (/ nanos 1000000000))))

(defn- start-new-second
  [message key-stats]
    (merge
      (if (contains? key-stats :in)
        (assoc key-stats :secs (conj (get key-stats :secs []) {:v (:in key-stats) :s (:s key-stats)}))
        {})
      {:in [(:value message)], :s (seconds-from-nanos (:time message))}))

(defn- add-value [message key-stats]
  (assoc key-stats :in (conj (get key-stats :in []) (:value message))))

(defn- merge-stats [message key-stats]
  (if (= (:s key-stats) (seconds-from-nanos (:time message)))
    (add-value message key-stats)
    (start-new-second message key-stats)))

(defn merge-time-message [data message]
  (let [key (keyword (:name message))
        key-stats (key data {})]
    (assoc data key (merge-stats message key-stats))))

(defn record-stats [message]
  (if (= "ms" (:type message))
    (send stats-agent merge-time-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))

