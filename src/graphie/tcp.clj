(ns
  graphie.tcp
  (:import (java.net InetAddress ServerSocket Socket SocketException)
           (java.io IOException)
           (java.util.concurrent Executors ExecutorService)))

(defn- execute [^ExecutorService executor ^Runnable f]
  (.execute executor f))

(defrecord tcp-server [executor socket clients])

(defn- close [^Socket s]
  (when-not (.isClosed s)
    (doto s
      (.shutdownInput)
      (.shutdownOutput)
      (.close))))

(defn- accept [^ExecutorService executor ^Socket s clients f]
  (let [ins (.getInputStream s)
        outs (.getOutputStream s)]
    (execute executor #(do
                  (dosync (commute clients conj s))
                  (try
                    (f ins outs)
                    (catch SocketException e))
                  (close s)
                  (dosync (commute clients disj s))))))

(defn- create-server [f ^ExecutorService executor ^ServerSocket ss]
  (let [clients (ref #{})]
    (execute executor #(when-not (.isClosed ss)
                  (try
                    (accept executor (.accept ss) clients f)
                    (catch SocketException e))
                  (recur)))
    (tcp-server. executor ss clients)))

(defn start-server [port f]
    (create-server f (Executors/newCachedThreadPool) (ServerSocket. port)))

(defn stop-server [server]
  (doseq [s @(:clients server)]
    (close s))
  (dosync (ref-set (:clients server) #{}))
  (.close ^ServerSocket (:socket server))
  (.shutdown (:executor server)))
