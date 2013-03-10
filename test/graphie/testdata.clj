(ns graphie.testdata
  (:import (java.net InetAddress DatagramSocket DatagramPacket)))

(defn udp-send [host port bytes]
  (let [socket (DatagramSocket.)]
    (.send socket (DatagramPacket. bytes (count bytes) (InetAddress/getByName host) port))
    (.close socket)))

(defn generate-test-data []
  (let [rnd (java.util.Random.)]
    (for [i (range 100)]
      (do
        (Thread/sleep 300)
        (udp-send "localhost" 7890 (.getBytes (str "response_time:" (.nextInt rnd 100) "0|v") "utf8"))))))
