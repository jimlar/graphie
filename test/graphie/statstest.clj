(ns graphie.statstest
  (:use midje.sweet)
  (:use graphie.stats))

(fact "123456 is second 0"
  (seconds-from-nanos 123456)
  => 0)

(fact "2000000000 is second 2"
  (seconds-from-nanos 2000000000)
  => 2)

(fact "First timing message gets inserted"
  (merge-time-message
    {}
    {:name "request_time" :value 320 :type "ms", :time 123456})
  => {:request_time {:in [320], :s 0}})

(fact "A second timing message gets added"
  (merge-time-message
    {:request_time {:in [320], :s 0}}
    {:name "request_time" :value 640 :type "ms", :time 123457})
  => {:request_time {:in [320 640], :s 0}})

(fact "A different metric gets a new key"
  (merge-time-message
    {:request_time {:in [320], :s 0}}
    {:name "response_time" :value 640 :type "ms", :time 123458})
  => {:request_time {:in [320], :s 0} :response_time {:in [640], :s 0}})

(fact "A new value for another second starts new value list and summarizes the previous second"
  (merge-time-message
    {:request_time {:in [320 456], :s 0}}
    {:name "request_time" :value 640 :type "ms", :time 2000000000})
  => {:request_time {:in [640] :s 2 :secs [{:v [320 456], :s 0}]}})

(fact "A new value for a third  second starts adds to the seconds list"
  (merge-time-message
    {:request_time {:in [640] :s 2 :secs [{:v [320 456], :s 0}]}}
    {:name "request_time" :value 720 :type "ms", :time 3000000000})
  => {:request_time {:in [720] :s 3 :secs [{:v [320 456], :s 0} {:v [640], :s 2}]}})
