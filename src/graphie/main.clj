(ns graphie.main
  (:require [noir.server :as noir]
            [graphie.receiver :as receiver]))

(noir/load-views "src/graphie/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (receiver/start 7890)
;    (noir/start port {:mode mode :ns 'graphie})
    (println "Started")))
