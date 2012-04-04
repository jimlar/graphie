(ns graphie.statstest
  (:use midje.sweet)
  (:use graphie.stats))


(fact "A timing message gets added to the stats agent state"
  (do
    (record-stats {:name "request_time" :value 320 :type "ms"})
    (await stats-agent)
    @stats-agent)
  => {:request_time 320})
