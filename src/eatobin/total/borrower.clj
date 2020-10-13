(ns eatobin.total.borrower
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.spec.test :as ostest]))

(s/def ::name string?)
(s/def ::maxX-books (s/and int? #(>= % 0)))
(s/def ::borrower (s/keys :req [::name ::maxX-books]))

(defn make-borrower [name maxX-books]
  {::name name, ::maxX-books maxX-books})
(s/fdef make-borrower
  :args (s/cat :name ::name
               :maxX-books ::maxX-books)
  :ret ::borrower)

(defn get-name [borrower]
  (borrower ::name))
(s/fdef get-name
  :args (s/cat :borrower ::borrower)
  :ret ::name)

(defn set-name [borrower name]
  (assoc borrower ::name name))
(s/fdef set-name
  :args (s/cat :borrower ::borrower
               :name ::name)
  :ret ::borrower)

(defn get-maxX-books [borrower]
  (borrower ::maxX-books))
(s/fdef get-maxX-books
  :args (s/cat :borrower ::borrower)
  :ret ::maxX-books)

(defn set-maxX-books [borrower maxX-books]
  (assoc borrower ::maxX-books maxX-books))
(s/fdef set-maxX-books
  :args (s/cat :borrower ::borrower
               :maxX-books ::maxX-books)
  :ret ::borrower)

(defn borrower-to-string [borrower]
  (str (get-name borrower) " (" (get-maxX-books borrower) " books)"))
(s/fdef borrower-to-string
  :args (s/cat :borrower ::borrower)
  :ret string?)

(ostest/instrument)
