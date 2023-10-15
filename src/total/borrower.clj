(ns total.borrower
  (:require
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [orchestra.spec.test :as ostest]
   [total.domain :as dom]))

(defn get-name [borrower]
  (:name borrower))
(s/def get-name
  ::dom/extract-fn-br-name)

(defn set-name [borrower name]
  (assoc borrower :name name))
(s/fdef set-name
  :args (s/cat :borrower :unq/borrower
               :name ::dom/name)
  :ret :unq/borrower)

(defn get-max-books [borrower]
  (:max-books borrower))
(s/fdef get-max-books
  :args (s/cat :borrower :unq/borrower)
  :ret ::dom/max-books)

(defn set-max-books [borrower max-books]
  (assoc borrower :max-books max-books))
(s/fdef set-max-books
  :args (s/cat :borrower :unq/borrower
               :max-books ::dom/max-books)
  :ret :unq/borrower)

(defn to-string [borrower]
  (str (get-name borrower) " (" (get-max-books borrower) " books)"))
(s/fdef to-string
  :args (s/cat :borrower :unq/borrower)
  :ret string?)

(defn- my-key-reader
  [key]
  (cond
    (= key "maxBooks") :max-books
    :else (keyword key)))

(defn- my-key-writer
  [key]
  (cond
    (= key :max-books) "maxBooks"
    :else (name key)))

(defn borrower-json-string-to-borrower [borrower-string]
  (json/read-str borrower-string
                 :key-fn my-key-reader))
(s/fdef borrower-json-string-to-borrower
  :args (s/cat :borrower-string string?)
  :ret :unq/borrower)

(defn borrower-to-json-string [borrower]
  (json/write-str borrower
                  :key-fn my-key-writer))
(s/fdef borrower-to-json-string
  :args (s/cat :borrower :unq/borrower)
  :ret string?)

(ostest/instrument)
