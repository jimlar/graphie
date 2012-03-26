(ns graphie.receivertest
  (:use midje.sweet)
  (:use graphie.receiver))


(fact "A correct message should be properly parsed"
  (decode-packet "glork:320|ms") => {:name "glork", :value 320, :type "ms"})

(fact "A message with a float value should give a nil message"
  (decode-packet "glork:320.9|ms") => nil)

(fact "A message with alpahs in the value should give a nil message"
  (decode-packet "glork:A20|ms") => nil)

(fact "A message without colon should give nil return"
  (decode-packet "glork320|ms") => nil)

(fact "A message without pip should give nil return"
  (decode-packet "glork:320ms") => nil)

(fact "A message without name gives nil message"
  (decode-packet ":320|ms") => nil)

(fact "A message without type gives nil message"
  (decode-packet "glork:320|") => nil)

(fact "An empty message gives nil message"
  (decode-packet "") => nil)


