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

def borrowerJsonStringToBorrower(borrowerString: String): Either[Error, Borrower] =
decode[Borrower](borrowerString)

def borrowerToJsonString(br: Borrower): JsonString =
br.asJson.noSpaces

(ostest/instrument)
