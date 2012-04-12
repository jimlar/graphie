(ns graphie.timetest
  (:use midje.sweet)
  (:use graphie.time))

(fact "123 is second 0"
  (as-seconds 123)
  => 0)

(fact "2000 is second 2"
  (as-seconds 2000)
  => 2)
