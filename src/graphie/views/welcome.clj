(ns graphie.views.welcome
  (:require [graphie.views.common :as common]
            [noir.content.pages :as pages])
  (:use noir.core
        hiccup.core
        hiccup.page-helpers))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to graphie"]))
