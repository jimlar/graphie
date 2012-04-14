(ns graphie.views.welcome
  (:require
    [clojure.string :as string]
    [noir.response :as response]
    [graphie.stats :as stats])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial layout [& content]
  (html5
    [:head [:title "graphie"]
       (include-css "/css/reset.css")
       (include-js "/js/jquery.min.js")
       (include-js "/js/jquery.flot.min.js")
       (include-js "/js/graphie.js")
     ]
    [:body [:div#wrapper content]]))

(defpage "/" {}
  (layout
    [:h1 "Welcome to graphie"]
    [:h2 "Current data"]
    [:div#placeholder {:style "width:600px;height:300px;"}]))

(defn- point-for-key [key second]
  [(* (:second second) 1000) (key second)])

(defn- pretty [name]
  (string/capitalize (string/replace (string/replace (str name) ":" "" ) "_" " ")))

(defn- data-set [name]
  (let [values (stats/seconds name)
        pretty-name (pretty name)]
    [
      {:label (str pretty-name " average") :data (map (partial point-for-key :avg) values)}
      {:label (str pretty-name " sum") :data (map (partial point-for-key :sum) values)}
      {:label (str pretty-name " max") :data (map (partial point-for-key :max) values)}
      {:label (str pretty-name " min") :data (map (partial point-for-key :min) values)}
      {:label (str pretty-name " #samples") :data (map (partial point-for-key :samples) values)}
    ]))

(defpage "/data.json" {}
  (response/json (reduce concat (map data-set (stats/names)))))
