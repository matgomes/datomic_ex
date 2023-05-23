(defproject datomic-ex "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.datomic/peer "1.0.6726"]
                 [org.erlang.otp/jinterface "1.13.1"]]
  :main ^:skip-aot datomic-ex.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
