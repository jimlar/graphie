(ns
  graphie.sockets
  (:import (java.net InetAddress ServerSocket Socket SocketException DatagramSocket DatagramPacket)
           (java.io IOException)
           (java.util.concurrent Executors)))

(defonce executor (Executors/newCachedThreadPool))

(defn- execute [^Runnable f]
  (.execute executor f))

;
; TCP server parts
;
(defrecord tcp-server [socket clients])

(defn- close [^Socket s]
  (when-not (.isClosed s)
    (doto s
      (.shutdownInput)
      (.shutdownOutput)
      (.close))))

(defn- accept [^Socket s clients f]
  (let [ins (.getInputStream s)
        outs (.getOutputStream s)]
    (execute #(do
                  (dosync (commute clients conj s))
                  (try
                    (f ins outs)
                    (catch SocketException e))
                  (close s)
                  (dosync (commute clients disj s))))))

(defn- create-tcp-server [f ^ServerSocket ss]
  (let [clients (ref #{})]
    (execute #(when-not (.isClosed ss)
                  (try
                    (accept (.accept ss) clients f)
                    (catch SocketException e))
                  (recur)))
    (tcp-server. ss clients)))

(defn start-tcp-server [port f]
    (create-tcp-server f (ServerSocket. port)))

(defn stop-tcp-server [server]
  (doseq [s @(:clients server)]
    (close s))
  (dosync (ref-set (:clients server) #{}))
  (.close ^ServerSocket (:socket server)))

;
; UDP parts
;

(defn start-udp-server [port f]
  (let [ds (DatagramSocket. port)]
    (execute #(when-not (.isClosed ds)
                (try
                  (let [dp (DatagramPacket. (byte-array 1024) 1024)]
                    (.receive ds dp)
                    (f dp))
                    (catch IOException e))
                (recur)))
    (tcp-server. ds [])))
