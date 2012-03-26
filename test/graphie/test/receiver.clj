(ns graphie.test.receiver
  (:use midje.sweet)
  (:use graphie.receiver))


(fact "A corect message should be properly parsed"
  (decode-packet "glork:320|ms") => {:name "glork", :value 320, :type "ms"})

(fact "A message with alpahs in the value should give nil value"
  (decode-packet "glork:A20|ms") => {:name "glork", :value nil, :type "ms"})

(fact "A message without colon should give nil return"
  (decode-packet "glork320|ms") => nil)

(fact "A message without pip should give nil return"
  (decode-packet "glork:320ms") => nil)

(fact "A message without name gives parsed message with an empty name"
  (decode-packet ":320|ms") => {:name "", :value 320, :type "ms"})