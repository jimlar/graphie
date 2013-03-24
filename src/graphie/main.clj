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
            [graphie.time :as time]
            [graphie.storage :as storage]))

(defhtml layout [theme & content]
  (let [theme (if (empty? theme) "spacelab" theme)]
    (html5
      [:head
       [:title "graphie"]
       (include-css "/css/reset.css")
       (include-css (str "/bootstrap/themes/" theme "/bootstrap.min.css"))
       (include-css "/css/graphie.css")]
      [:body
       [:div#wrapper content]
       (include-js "http://d3js.org/d3.v2.min.js?2.8.1")
       (include-js "/js/jquery.min.js")
       (include-js "/js/jquery.flot.min.js")
       (include-js "/bootstrap/js/bootstrap.js")
       (include-js "/js/graphie.js")
       ])))

(defn index [theme]
  (layout
    theme
    [:h1 "Welcome to graphie"]
    [:h2 "Current data"]))

(defn- point-for-key [key second]
  {:s (:second second) :v (key second)})

(defn- pretty [name]
  (string/capitalize (string/replace (string/replace (str name) ":" "" ) "_" " ")))

(defn- data-set [metric start-second end-second]
  (let [values (storage/values metric start-second end-second)
        pretty-name (pretty metric)]
    (apply
      merge
      (map
        (fn [key] { key { :label (name key) :data (map (partial point-for-key key) values)}})
        [:average :sum :max :min :samples]))))

(defn graph-data [seconds-back]
  {:headers {"Content-Type" "application/json"}
    :body
      (let [end-second (time/as-seconds (time/stamp))
            start-second (- end-second seconds-back)]
        (json/generate-string
          (apply
            merge
            (for [metric (storage/names)]
              {metric (assoc (data-set metric start-second end-second) :label (pretty metric))}))))})

(defroutes app-routes
  (GET "/" [theme] (index theme))
  (GET "/data.json" [] (graph-data 120))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn init []
  (receiver/start 7890 stats/record-stats)
  (println "Receiver started"))
