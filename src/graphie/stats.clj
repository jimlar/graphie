(ns
  graphie.stats)

(defonce stats-agent (agent {}))

(defn seconds-from-nanos [nanos]
  (long (Math/floor (/ nanos 1000000000))))

(defn- create-key-stats [message]
  {:in [(:value message)], :s (seconds-from-nanos (:time message))})

(defn- add-to-in [message key-stats]
  (if (= (:s key-stats) (seconds-from-nanos (:time message)))
    (assoc key-stats :in (conj (get key-stats :in []) (:value message)))
    (merge
      key-stats
      (create-key-stats message))))

(defn process-timing-message [data message]
  (let [key (keyword (:name message))
        key-stats (key data)]
    (if (nil? key-stats)
      (assoc data key (create-key-stats message))
      (assoc data key (add-to-in message key-stats)))))

(defn record-stats [message]
  (if (= "ms" (:type message))
    (send stats-agent process-timing-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))
