(ns
  graphie.receiver
  (:require [clojure.string :as string]
            [aleph.udp :as aleph]
            [lamina.core :as lamina]
            [gloss.core :as gloss]
            [graphie.time :as time]
            [graphie.stats :as stats]))


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
      {:name (parts 0), :value (parse-int (parts 1) nil), :type (parts 2), :time (time/stamp)}
      nil)))

(defn receive [stats-engine packet]
  (let [message (decode-packet packet)]
    (if (not (nil? message))
      (stats-engine message))
    nil))

(defn start [port stats-engine]
  (let [server (aleph/udp-socket {:port port :frame (gloss/string :utf-8)})]
    (lamina/receive-all @server #(receive stats-engine (:message %1)))
    server))

(defn stop [server]
  (lamina/close server))
