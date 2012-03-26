(ns
  graphie.sockets
  (:import (java.net InetAddress ServerSocket Socket SocketException DatagramSocket DatagramPacket)
           (java.io IOException)
           (java.util.concurrent Executors Executor)))

(defn- execute [^Executor executor ^Runnable f]
  (.execute executor f))

;
; TCP server parts
;
(defrecord tcp-server [executor socket clients])

(defn- close [^Socket s]
  (when-not (.isClosed s)
    (doto s
      (.shutdownInput)
      (.shutdownOutput)
      (.close))))

(defn- accept [^Executor executor ^Socket s clients f]
  (let [ins (.getInputStream s)
        outs (.getOutputStream s)]
    (execute executor #(do
                  (dosync (commute clients conj s))
                  (try
                    (f ins outs)
                    (catch SocketException e))
                  (close s)
                  (dosync (commute clients disj s))))))

(defn- create-tcp-server [f ^Executor executor ^ServerSocket ss]
  (let [clients (ref #{})]
    (execute executor #(when-not (.isClosed ss)
                  (try
                    (accept executor (.accept ss) clients f)
                    (catch SocketException e))
                  (recur)))
    (tcp-server. executor ss clients)))

(defn start-tcp-server [port f]
    (create-tcp-server f (Executors/newCachedThreadPool) (ServerSocket. port)))

(defn stop-tcp-server [server]
  (doseq [s @(:clients server)]
    (close s))
  (dosync (ref-set (:clients server) #{}))
  (.close ^ServerSocket (:socket server))
  (.shutdown (:executor server)))

;
; UDP parts
;

(defn udp-to-string [packet]
  (String. (.getData packet) (.getOffset packet) (.getLength packet) "utf-8"))

(defn start-udp-server [port f decode-packet]
  (let [ds (DatagramSocket. port)
        executor (Executors/newCachedThreadPool)]
    (execute executor #(when-not (.isClosed ds)
                (try
                  (let [dp (DatagramPacket. (byte-array 1024) 1024)]
                    (.receive ds dp)
                    (f (decode-packet dp)))
                    (catch IOException e))
                (recur)))
    (tcp-server. executor ds [])))
