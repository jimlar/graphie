(ns graphie.statstest
  (:use midje.sweet)
  (:use graphie.stats)
  (:require [graphie.storage :as storage]))

(fact "First timing message gets inserted"
  (merge-message
    {}
    {:name "request_time" :value 320 :type "v", :time 456})
  => {:request_time {:in [320], :second 0}})

(fact "A second timing message gets added"
  (merge-message
    {:request_time {:in [320], :second 0}}
    {:name "request_time" :value 640 :type "v", :time 457})
  => {:request_time {:in [320 640], :second 0}})

(fact "A different metric gets a new key"
  (merge-message
    {:request_time {:in [320], :second 0}}
    {:name "response_time" :value 640 :type "v", :time 458})
  => {:request_time {:in [320], :second 0} :response_time {:in [640], :second 0}})

(fact "A new value for another second starts new value list and summarizes and stores the previous second"
  (merge-message
    {:request_time {:in [320 456], :second 0}}
    {:name "request_time" :value 640 :type "v", :time 2000})
  => {:request_time {:in [640] :second 2}}
  (provided
    (storage/store :request_time {:average 388, :sum 776, :max 456, :min 320, :samples 2, :second 0}) => nil))

(fact "A new value for a third  second starts adds to the seconds list"
  (merge-message
    {:request_time {:in [640] :second 2}}
    {:name "request_time" :value 720 :type "v", :time 3000})
  => {:request_time {:in [720] :second 3}}
  (provided
    (storage/store :request_time {:average 640, :sum 640, :max 640, :min 640, :samples 1, :second 2}) => nil))