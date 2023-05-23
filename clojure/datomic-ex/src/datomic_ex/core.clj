(ns datomic-ex.core
  (:require
   [datomic-ex.types :as types]
   [datomic.api :as d])
  (:import
   (com.ericsson.otp.erlang OtpMbox OtpNode)
   [java.util.concurrent ExecutorService Executors])
  (:gen-class))

(defmulti datomic-function
  (fn [_ {:datomic/keys [function]}]
    function))

(defmethod datomic-function :q
  [{:datomic/keys [conn]}
   {query :data}]
  (d/q query (d/db conn)))

(defmethod datomic-function :transact
  [{:datomic/keys [conn]}
   {tx-data :data}]
  @(d/transact conn tx-data)
  :success)

(defn start-loop
  [conn mbox service]
  (while true
    (let [{:source/keys [from] :as message} (-> mbox .receive types/elixir->clojure)]
      (.submit ^ExecutorService service
               ^Callable (fn []
                           (->> (datomic-function {:datomic/conn conn} message)
                                types/clojure->elixir
                                (.send mbox from)))))))

(defn -main
  [& args]
  (System/setProperty "OtpConnection.trace" "4")
  (let [node (OtpNode. "cljnode" "mycookie")
        mbox ^OtpMbox (.createMbox ^OtpNode node "datomic_mailbox")
        uri "datomic:mem://foo"
        _ (d/create-database uri)
        conn (d/connect uri)]
    (start-loop conn mbox (Executors/newFixedThreadPool 20))))
