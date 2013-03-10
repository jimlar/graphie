(defproject graphie "0.1.0-SNAPSHOT"
            :description "Small statistics server"
            :dependencies [[org.clojure/clojure "1.5.0"]
                           [aleph "0.3.0-beta14"]
                           [compojure "1.1.5"]
                           [hiccup "1.0.2"]
                           [clj-json "0.5.3"]
                           [com.novemberain/monger "1.0.0-beta5"]]
            :plugins [[lein-ring "0.8.2"]]
            :ring {
              :init graphie.main/init
              :handler graphie.main/app
            }
            :profiles {
              :dev {
                :dependencies [[midje "1.5.0"]]
                :plugins [[lein-midje "3.0.0"]]
              }
            })
