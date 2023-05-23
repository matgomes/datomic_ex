(ns datomic-ex.types
  (:import
   [clojure.lang IPersistentMap IPersistentVector Keyword]
   (com.ericsson.otp.erlang
    OtpErlangAtom
    OtpErlangBinary
    OtpErlangList
    OtpErlangMap
    OtpErlangObject
    OtpErlangString
    OtpErlangTuple
    OtpErlangLong
    OtpErlangInt)))

(defprotocol ToClojure
  (elixir->clojure [this]))

(extend-protocol ToClojure

  ;; TODO add missing types
  ;; ...

  ;; TODO support date tuples
  OtpErlangTuple
  (elixir->clojure [this]
    (let [[fun & rest] (vec (.elements this))
          fun-symbol (-> fun elixir->clojure symbol)
          args (map elixir->clojure rest)]
      (apply (resolve fun-symbol) args)))

  OtpErlangList
  (elixir->clojure [this]
    (let [elements (vec (.elements this))]
      (mapv elixir->clojure elements)))

  OtpErlangMap
  (elixir->clojure [this]
    (-> (into {} (.entrySet this))
        (update-keys elixir->clojure)
        (update-vals elixir->clojure)))

  OtpErlangAtom
  (elixir->clojure [this]
    (let [atom (.atomValue this)]
      (case atom
        "true"  true
        "false" false
        (keyword atom))))

  OtpErlangString
  (elixir->clojure [this]
    (.stringValue this))

  OtpErlangBinary
  (elixir->clojure [this]
    (-> this .binaryValue (String. "UTF-8")))

  OtpErlangObject
  (elixir->clojure [this]
    this))

(defprotocol FromClojure
  (clojure->elixir [this]))

(defn otp-erlang-object-array
  [coll]
  (into-array OtpErlangObject (map clojure->elixir coll)))

(extend-protocol FromClojure

  ;; TODO add missing types
  ;; ...

  Keyword
  (clojure->elixir [this]
    (OtpErlangAtom. (subs (str this) 1)))

  IPersistentMap
  (clojure->elixir [this]
    (let [ks (or (keys this) [])
          vs (or (vals this) [])]
      (OtpErlangMap. (otp-erlang-object-array ks)
                     (otp-erlang-object-array vs))))

  IPersistentVector
  (clojure->elixir [this]
    (OtpErlangList. (otp-erlang-object-array this)))

  Long
  (clojure->elixir [this]
    (OtpErlangLong. this))

  String
  (clojure->elixir [this]
    (OtpErlangBinary. (.getBytes this))))
