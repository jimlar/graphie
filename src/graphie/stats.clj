(ns
  graphie.stats)

(defonce stats-agent (agent {}))

(defn process-timing-message [data message]
  (let [key (keyword (:name message))
        values (get data key [])]
    (assoc data (keyword (:name message)) (conj values (:value message)))))

(defn record-stats [message]
  (if (= "ms" (:type message))
    (send stats-agent process-timing-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))
