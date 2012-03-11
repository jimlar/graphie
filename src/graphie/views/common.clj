(ns graphie.views.common
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
