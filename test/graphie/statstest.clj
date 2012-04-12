(ns graphie.statstest
  (:use midje.sweet)
  (:use graphie.stats))

(fact "123 is second 0"
  (seconds-from-millis 123)
  => 0)

(fact "2000 is second 2"
  (seconds-from-millis 2000)
  => 2)

(fact "First timing message gets inserted"
  (merge-time-message
    {}
    {:name "request_time" :value 320 :type "ms", :time 456})
  => {:request_time {:in [320], :second 0}})

(fact "A second timing message gets added"
  (merge-time-message
    {:request_time {:in [320], :second 0}}
    {:name "request_time" :value 640 :type "ms", :time 457})
  => {:request_time {:in [320 640], :second 0}})

(fact "A different metric gets a new key"
  (merge-time-message
    {:request_time {:in [320], :second 0}}
    {:name "response_time" :value 640 :type "ms", :time 458})
  => {:request_time {:in [320], :second 0} :response_time {:in [640], :second 0}})

(fact "A new value for another second starts new value list and summarizes the previous second"
  (merge-time-message
    {:request_time {:in [320 456], :second 0}}
    {:name "request_time" :value 640 :type "ms", :time 2000})
  => {:request_time {:in [640] :second 2 :secs [{:v [320 456], :second 0}]}})

(fact "A new value for a third  second starts adds to the seconds list"
  (merge-time-message
    {:request_time {:in [640] :second 2 :secs [{:v [320 456], :second 0}]}}
    {:name "request_time" :value 720 :type "ms", :time 3000})
  => {:request_time {:in [720] :second 3 :secs [{:v [320 456], :second 0} {:v [640], :second 2}]}})
