(ns graphie.views.welcome
  (:require
    [graphie.stats :as stats])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpartial layout [& content]
  (html5
    [:head
     [:title "graphie"]
     (include-css "/css/reset.css")]
    [:body
     [:div#wrapper
      content]]))

(defpage "/" {}
         (layout
           [:h1 "Welcome to graphie"]
           [:h2 "Current data"]
           [:pre (str @stats/stats-agent)]))
