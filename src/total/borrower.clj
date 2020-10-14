(ns total.borrower
  (:require
    [total.domain :as dom]
    [clojure.spec.alpha :as s]
    [orchestra.spec.test :as ostest]))

(defn make-borrower [name max-books]
  {::dom/name name, ::dom/max-books max-books})
(s/fdef make-borrower
        :args (s/cat :name ::dom/name
                     :max-books ::dom/max-books)
        :ret ::dom/borrower)

(defn get-name [borrower]
  (borrower ::dom/name))
(s/fdef get-name
        :args (s/cat :borrower ::dom/borrower)
        :ret ::dom/name)

(defn set-name [borrower name]
  (assoc borrower ::dom/name name))
(s/fdef set-name
        :args (s/cat :borrower ::dom/borrower
                     :name ::dom/name)
        :ret ::dom/borrower)

(defn get-max-books [borrower]
  (borrower ::dom/max-books))
(s/fdef get-max-books
        :args (s/cat :borrower ::dom/borrower)
        :ret ::dom/max-books)

(defn set-max-books [borrower max-books]
  (assoc borrower ::dom/max-books max-books))
(s/fdef set-max-books
        :args (s/cat :borrower ::dom/borrower
                     :max-books ::dom/max-books)
        :ret ::dom/borrower)

(defn borrower-to-string [borrower]
  (str (get-name borrower) " (" (get-max-books borrower) " books)"))
(s/fdef borrower-to-string
        :args (s/cat :borrower ::dom/borrower)
        :ret string?)

(ostest/instrument)
