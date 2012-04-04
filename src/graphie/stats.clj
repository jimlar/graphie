(ns
  graphie.stats)

(defonce stats-agent (agent {}))

(defn process-timing-message [data message]
  (assoc data (keyword (:name message)) (:value message)))

(defn record-stats [message]
  (println "Recording " message)
  (if (= "ms" (:type message))
    (send stats-agent process-timing-message message)
    (throw (IllegalArgumentException. (str "Unsupported message type: " (:type message))))))
