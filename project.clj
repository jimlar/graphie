(defproject graphie "0.1.0-SNAPSHOT"
            :description "Small statistics server"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [noir "1.2.2"]]
            :dev-dependencies [[midje "1.3.2-SNAPSHOT"]
                               [lein-midje "[1.0.8,)"]
                               [com.stuartsierra/lazytest "1.2.3"]]
            :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"}
            :main graphie.main)
