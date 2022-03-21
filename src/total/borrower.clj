(ns total.borrower
  (:require
    [clojure.data.json :as json]
    [malli.dev :as dev]
    [malli.dev.pretty :as pretty]))

(def =>name [:string {:min 1}])
(def =>max-books [:int {:min 1, :max 10}])
(def =>borrower [:map {:closed true} [:name =>name] [:max-books =>max-books]])

;; sample usage:
(def =>get-name [:=> [:cat =>borrower] =>name])

(defn get-name
  "get the name of a borrower"
  {:malli/schema =>get-name}
  [borrower]
  (:name borrower))

(defn set-name
  "set the name of a borrower"
  {:malli/schema [:=> [:cat =>borrower =>name] =>borrower]}
  [borrower name]
  (assoc borrower :name name))

(defn get-max-books
  "get the max-books of a borrower"
  {:malli/schema [:=> [:cat =>borrower] =>max-books]}
  [borrower]
  (:max-books borrower))

(defn set-max-books
  "set the max-books of a borrower"
  {:malli/schema [:=> [:cat =>borrower =>max-books] =>borrower]}
  [borrower max-books]
  (assoc borrower :max-books max-books))

(defn to-string
  "send a borrower to-string"
  {:malli/schema [:=> [:cat =>borrower] :string]}
  [borrower]
  (str (get-name borrower) " (" (get-max-books borrower) " books)"))

(defn my-key-reader
  "string -> keyword"
  {:malli/schema [:=> [:cat [:string {:min 1}]] :keyword]}
  [string-key]
  (cond
    (= string-key "maxBooks") :max-books
    :else (keyword string-key)))

(defn my-key-writer
  "keyword -> string"
  {:malli/schema [:=> [:cat :keyword] :string]}
  [keyword-key]
  (cond
    (= keyword-key :max-books) "maxBooks"
    :else (name keyword-key)))

(defn borrower-json-string-to-borrower
  "create a borrower from a JSON string"
  {:malli/schema [:=> [:cat :string] =>borrower]}
  [borrower-string]
  (json/read-str borrower-string
                 :key-fn my-key-reader))

(defn borrower-to-json-string
  "create a JSON string from a borrower"
  {:malli/schema [:=> [:cat =>borrower] :string]}
  [borrower]
  (json/write-str borrower
                  :key-fn my-key-writer))

(dev/start! {:report (pretty/reporter)})
