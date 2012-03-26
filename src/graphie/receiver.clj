(ns
  graphie.receiver
  (:require [clojure.string :as string]
            [graphie.udp :as udp]
            [graphie.stats :as stats]))


(defn- parse-int [v default]
  (try
    (Integer. v)
    (catch NumberFormatException e
      default)))

(defn decode-packet [packet]
  (let [parts (string/split packet #"[:\\|]")]
    (if (and
          (= 3 (count parts))
          (not (string/blank? (parts 0)))
          (not (nil? (parse-int (parts 1) nil))))
      {:name (parts 0), :value (parse-int (parts 1) nil), :type (parts 2)}
      nil)))

(defn- receive [stats-engine packet]
  (let [message (decode-packet packet)]
    (println (str "Got message '" message "', reporting to stats engine"))
    (stats-engine message)))

(defn start [port stats-engine]
  (udp/start-server port (partial receive stats-engine) (partial udp/packet-to-string "utf-8")))

(defn stop [server]
  (udp/stop-server server))
