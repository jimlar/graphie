(ns
  graphie.receiver
  (:require [clojure.string :as string]
            [graphie.sockets :as sockets]))


(defn- receive-stream-data [in out]
  (let [reader (java.io.PushbackReader. (java.io.InputStreamReader. in "utf8"))]
    (let [name (read reader)
          value (read reader)
          time (read reader)]
      (println (str "Got sample for " name ", value: " value ", time: " time)))))

(defn- receive-udp [packet]
  (println (str "Got packet '" packet "'")))

(defn parse-int [v default]
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

(defn start [port]
  (sockets/start-udp-server port receive-udp sockets/udp-to-string))
