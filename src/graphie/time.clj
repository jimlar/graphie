(ns
  graphie.time
  (:require [clojure.contrib.math :as math]))

(defn stamp []
  (System/currentTimeMillis))

(defn as-seconds [millis]
  (long (Math/floor (/ millis 1000))))
