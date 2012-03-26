(ns
  graphie.udp
  (:import (java.net InetAddress DatagramSocket DatagramPacket)
           (java.io IOException)
           (java.util.concurrent Executors ExecutorService)))

(defrecord udp-server [executor socket])

(defn- execute [^ExecutorService executor ^Runnable f]
  (.execute executor f))

(defn packet-to-string [encoding packet]
  (String. (.getData packet) (.getOffset packet) (.getLength packet) encoding))

(defn start-server [port f decode-f]
  (let [ds (DatagramSocket. port)
        executor (Executors/newCachedThreadPool)]
    (execute executor #(when-not (.isClosed ds)
                (try
                  (let [dp (DatagramPacket. (byte-array 1024) 1024)]
                    (.receive ds dp)
                    (f (decode-f dp)))
                    (catch IOException e))
                (recur)))
    (udp-server. executor ds)))

(defn stop-server [^udp-server server]
  (.close (:socket server))
  (.shutdown (:executor server)))
