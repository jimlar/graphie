(ns graphie.statstest
  (:use midje.sweet)
  (:use graphie.stats))

(fact "First timing message gets inserted"
  (process-timing-message {} {:name "request_time" :value 320 :type "ms"})
  => {:request_time [320]})

(fact "A second timing message gets added"
  (process-timing-message {:request_time [320]} {:name "request_time" :value 640 :type "ms"})
  => {:request_time [320 640]})

(fact "A different metric gets a new key"
  (process-timing-message {:request_time [320]} {:name "response_time" :value 640 :type "ms"})
  => {:request_time [320] :response_time [640]})

