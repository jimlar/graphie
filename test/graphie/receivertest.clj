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

(defn dummy-stats-engine [message] nil)

(fact "Nil message is not passed to the stats-engine"
  (receive dummy-stats-engine "") => nil
  (provided
    (dummy-stats-engine nil) => nil :times 0))

(fact "Proper message is passed to the stats-engine"
  (receive dummy-stats-engine "glork:320|ms") => nil
  (provided
    (dummy-stats-engine {:name "glork", :value 320, :type "ms"}) => nil))
