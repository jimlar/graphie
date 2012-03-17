(ns
  graphie.receiver
  (:require [graphie.sockets :as sockets]))


(defn- receive-stream-data [in out]
  (let [reader (java.io.PushbackReader. (java.io.InputStreamReader. in "utf8"))]
    (let [name (read reader)
          value (read reader)
          time (read reader)]
      (println (str "Got sample for " name ", value: " value ", time: " time)))))

(defn- receive-udp [packet]
  (println (str "Got packet '" (String. (.getData packet) (.getOffset packet) (.getLength packet) "utf-8") "'")))

(defn start [port]
  (sockets/start-udp-server port receive-udp))


