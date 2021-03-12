(ns total.borrower
  (:require
    [total.domain :as dom]
    [clojure.data.json :as json]
    [clojure.spec.alpha :as s]
    [orchestra.spec.test :as ostest]))

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

(defn borrower-json-string-to-borrower [borrower-string]
  (json/read-str borrower-string
                 :key-fn my-key-reader))
(s/fdef borrower-json-string-to-borrower
        :args (s/cat :borrower-string string?)
        :ret :unq/borrower)

;def borrowerJsonStringToBorrower (borrowerString: String) : Either [Error, Borrower] =
;decode [Borrower] (borrowerString)
;
;def borrowerToJsonString (br: Borrower) : JsonString =
;br.asJson.noSpaces
;
;(defn- my-value-reader
;  [key value]
;  (if (or (= key :givee)
;          (= key :giver))
;    (keyword value)
;    value))
;
;(defn gift-pair-json-string-to-Gift-Pair [gp-string]
;  (json/read-str gp-string
;                 :value-fn my-value-reader
;                 :key-fn keyword))
;(s/fdef gift-pair-json-string-to-Gift-Pair
;        :args (s/cat :gp-string string?)
;        :ret :unq/gift-pair)

(ostest/instrument)
