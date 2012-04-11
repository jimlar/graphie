(defproject graphie "0.1.0-SNAPSHOT"
            :description "Small statistics server"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [org.clojure/clojure-contrib "1.2.0"]
                           [noir "1.2.2"]]
            :dev-dependencies [[midje "1.3.1"]
                               [lein-midje "[1.0.9]"]
                               [com.stuartsierra/lazytest "1.2.3"]]
            :repositories {"stuartsierra-releases" "http://stuartsierra.com/maven2"}
	    :plugins [[lein-swank "1.4.4"]]
            :main graphie.main)
