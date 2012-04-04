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
  (process-timing-message
    {}
    {:name "request_time" :value 320 :type "ms", :time 123456})
  => {:request_time {:in [320], :s 0}})

(fact "A second timing message gets added"
  (process-timing-message
    {:request_time {:in [320], :s 0}}
    {:name "request_time" :value 640 :type "ms", :time 123457})
  => {:request_time {:in [320 640], :s 0}})

(fact "A different metric gets a new key"
  (process-timing-message
    {:request_time {:in [320], :s 0}}
    {:name "response_time" :value 640 :type "ms", :time 123458})
  => {:request_time {:in [320], :s 0} :response_time {:in [640], :s 0}})

(fact "Sending a new value for another second starts new value list"
  (process-timing-message
    {:request_time {:in [320], :s 0}}
    {:name "request_time" :value 640 :type "ms", :time 2000000000})
  => {:request_time {:in [640] :s 2}})
