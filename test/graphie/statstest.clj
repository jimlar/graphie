(ns graphie.statstest
  (:use midje.sweet)
  (:use graphie.stats))

(fact "First timing message gets inserted"
  (merge-value-message
    {}
    {:name "request_time" :value 320 :type "v", :time 456})
  => {:request_time {:in [320], :second 0}})

(fact "A second timing message gets added"
  (merge-value-message
    {:request_time {:in [320], :second 0}}
    {:name "request_time" :value 640 :type "v", :time 457})
  => {:request_time {:in [320 640], :second 0}})

(fact "A different metric gets a new key"
  (merge-value-message
    {:request_time {:in [320], :second 0}}
    {:name "response_time" :value 640 :type "v", :time 458})
  => {:request_time {:in [320], :second 0} :response_time {:in [640], :second 0}})

(fact "A new value for another second starts new value list and summarizes the previous second"
  (merge-value-message
    {:request_time {:in [320 456], :second 0}}
    {:name "request_time" :value 640 :type "v", :time 2000})
  => {:request_time {:in [640] :second 2 :secs [{:v [320 456], :second 0}]}})

(fact "A new value for a third  second starts adds to the seconds list"
  (merge-value-message
    {:request_time {:in [640] :second 2 :secs [{:v [320 456], :second 0}]}}
    {:name "request_time" :value 720 :type "v", :time 3000})
  => {:request_time {:in [720] :second 3 :secs [{:v [320 456], :second 0} {:v [640], :second 2}]}})
