(ns
  graphie.time)

(defn stamp []
  (System/currentTimeMillis))

(defn as-seconds [millis]
  (long (Math/floor (/ millis 1000))))
