(ns
  graphie.receiver
  (:require [graphie.sockets :as sockets]))


(defn- receive-data [in out]
  (let [reader (java.io.PushbackReader. (java.io.InputStreamReader. in "utf8"))]
    (let [name (read reader)
          value (read reader)
          time (read reader)]
      (println (str "Got sample for " name ", value: " value ", time: " time)))))

(defn start [port]
  (sockets/start-server port receive-data))


