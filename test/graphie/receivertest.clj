(ns graphie.receivertest
  (:use midje.sweet)
  (:use graphie.receiver)
  (:require [graphie.time :as time]))


(fact "A correct message should be properly parsed"
  (decode-packet "request_time:320|v") => {:name "request_time", :value 320, :type "v", :time 123456}
  (provided
    (time/stamp) => 123456))

(fact "A message with a float value should give a nil message"
  (decode-packet "request_time:320.9|v")
  => nil)

(fact "A message with alpahs in the value should give a nil message"
  (decode-packet "request_time:A20|v")
  => nil)

(fact "A message without colon should give nil return"
  (decode-packet "request_time320|v")
  => nil)

(fact "A message without pip should give nil return"
  (decode-packet "request_time:320v")
  => nil)

(fact "A message without name gives nil message"
  (decode-packet ":320|v")
  => nil)

(fact "A message without type gives nil message"
  (decode-packet "request_time:320|")
  => nil)

(fact "A garbage message gives nil message"
  (decode-packet "garbage")
  => nil)

(fact "An empty message gives nil message"
  (decode-packet "")
  => nil)

(defn dummy-stats-engine [message] nil)

(fact "Nil message is not passed to the stats-engine"
  (receive dummy-stats-engine "") => nil
  (provided
    (dummy-stats-engine nil) => nil :times 0))

(fact "Proper message is passed to the stats-engine"
  (receive dummy-stats-engine "request_time:320|v")
  => nil
  (provided
    (time/stamp) => 123456
    (dummy-stats-engine {:name "request_time", :value 320, :type "v", :time 123456}) => nil))

