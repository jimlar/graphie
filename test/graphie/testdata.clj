(ns graphie.testdata
  (:require [graphie.udp :as udp]))

(defn generate-test-data []
  (let [rnd (java.util.Random.)]
    (for [i (range 100)]
      (do
        (Thread/sleep 300)
        (udp/udp-send "localhost" 7890 (.getBytes (str "response_time:" (.nextInt rnd 100) "0|ms") "utf8"))))))