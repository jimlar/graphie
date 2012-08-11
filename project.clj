(defproject graphie "0.1.0-SNAPSHOT"
            :description "Small statistics server"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [org.clojure/clojure-contrib "1.2.0"]
                           [noir "1.2.2"]
                           [com.novemberain/monger "1.0.0-beta5"]]
            :repositories {"stuart" "http://stuartsierra.com/maven2"}
            :main graphie.main
            :profiles {
              :dev {
                :plugins [[lein-midje "2.0.0-SNAPSHOT"]]
              }
              :test {
                :dependencies [
                  [midje "1.4.0"]
                  [com.stuartsierra/lazytest "1.2.3"]
                ]
              }
            })