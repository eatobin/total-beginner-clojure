(ns total.borrower
  (:require
    [clojure.data.json :as json]
    [malli.instrument :as mi]))

(def =>bn [:string {:min 1}])
(def =>mb [:and :int [:> 0]])
(def =>br [:map {:closed true} [:name =>bn] [:max-books =>mb]])

;; sample usage:
(def =>get-name [:=> [:cat =>br] =>bn])

(defn get-name
  "get the name of a borrower"
  {:malli/schema =>get-name}
  [borrower]
  (:name borrower))

(defn set-name
  "set the name of a borrower"
  {:malli/schema [:=> [:cat =>br =>bn] =>br]}
  [borrower name]
  (assoc borrower :name name))

(defn get-max-books
  "get the max-books of a borrower"
  {:malli/schema [:=> [:cat =>br] =>mb]}
  [borrower]
  (:max-books borrower))

(defn set-max-books
  "set the max-books of a borrower"
  {:malli/schema [:=> [:cat =>br =>mb] =>br]}
  [borrower max-books]
  (assoc borrower :max-books max-books))

(defn to-string
  "send a borrower to-string"
  {:malli/schema [:=> [:cat =>br] :string]}
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
  {:malli/schema [:=> [:cat :string] =>br]}
  [borrower-string]
  (json/read-str borrower-string
                 :key-fn my-key-reader))

(defn borrower-to-json-string
  "create a JSON string from a borrower"
  {:malli/schema [:=> [:cat =>br] :string]}
  [borrower]
  (json/write-str borrower
                  :key-fn my-key-writer))

(mi/collect!)
(mi/instrument!)
