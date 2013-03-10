(ns graphie.main
  (:use compojure.core
        [hiccup.def :only [defhtml]]
        [hiccup.page :only [include-css include-js html5]])
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-json.core :as json]
            [clojure.string :as string]
            [graphie.receiver :as receiver]
            [graphie.stats :as stats]
            [graphie.storage :as storage]))

(defhtml layout [theme & content]
  (let [theme (if (empty? theme) "spacelab" theme)]
    (html5
      [:head
       [:title "graphie"]
       (include-css "/css/reset.css")
       (include-css (str "/bootstrap/themes/" theme "/bootstrap.min.css"))]
      [:body
       [:div#wrapper content]
       (include-js "/js/jquery.min.js")
       (include-js "/js/jquery.flot.min.js")
       (include-js "/bootstrap/js/bootstrap.js")
       (include-js "/js/graphie.js")
       ])))

(defn index [theme]
  (layout
    theme
    [:h1 "Welcome to graphie"]
    [:h2 "Current data"]
    [:div#placeholder {:style "width:1024px;height:600px;"}]))

(defn- point-for-key [key second]
  [(* (:second second) 1000) (key second)])

(defn- pretty [name]
  (string/capitalize (string/replace (string/replace (str name) ":" "" ) "_" " ")))

(defn- data-set [name]
  (let [values (storage/seconds name)
        pretty-name (pretty name)]
    [
      {:label (str pretty-name " average") :data (map (partial point-for-key :average) values)}
      {:label (str pretty-name " sum") :data (map (partial point-for-key :sum) values)}
      {:label (str pretty-name " max") :data (map (partial point-for-key :max) values)}
      {:label (str pretty-name " min") :data (map (partial point-for-key :min) values)}
      {:label (str pretty-name " #samples") :data (map (partial point-for-key :samples) values)}
      ]))

(defn graph-data []
  {:headers {"Content-Type" "application/json"}
    :body (json/generate-string (reduce concat (map data-set (storage/names))))})

(defroutes app-routes
  (GET "/" [theme] (index theme))
  (GET "/data.json" [] (graph-data))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn init []
  (receiver/start 7890 stats/record-stats)
  (println "Receiver started"))
