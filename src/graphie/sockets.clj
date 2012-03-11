(ns
  graphie.sockets
  (:import (java.net InetAddress ServerSocket Socket SocketException)
           (java.util.concurrent Executors)))

(defonce executor (Executors/newCachedThreadPool))

(defrecord server [socket clients])

(defn- execute [^Runnable f]
  (.execute executor f))

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

(defn- create-server [f ^ServerSocket ss]
  (let [clients (ref #{})]
    (execute #(when-not (.isClosed ss)
                  (try
                    (accept (.accept ss) clients f)
                    (catch SocketException e))
                  (recur)))
    (server. ss clients)))

(defn start-server [port f]
    (create-server f (ServerSocket. port)))

(defn stop-server [server]
  (doseq [s @(:clients server)]
    (close s))
  (dosync (ref-set (:clients server) #{}))
  (.close ^ServerSocket (:socket server)))
