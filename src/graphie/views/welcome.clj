(ns graphie.views.welcome
  (:require
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
    [:div#placeholder {:style "width:600px;height:300px;"}]
    [:pre (str @stats/stats-agent)]))

(defn data-set [name]
  {:label (str name) :data (stats/values name)})

(defpage "/data.json" {}
  (response/json (map data-set (stats/names))))
