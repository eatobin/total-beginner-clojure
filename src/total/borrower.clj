(ns total.borrower
  (:require
   [clojure.spec.alpha :as s]
   [orchestra.spec.test :as ostest]))

(s/def ::name string?)
(s/def ::max-books (s/and int? #(>= % 0)))
(s/def ::borrower (s/keys :req [::name ::max-books]))

(defn make-borrower [name max-books]
  {::name name, ::max-books max-books})
(s/fdef make-borrower
  :args (s/cat :name ::name
               :max-books ::max-books)
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

(defn get-max-books [borrower]
  (borrower ::max-books))
(s/fdef get-max-books
  :args (s/cat :borrower ::borrower)
  :ret ::max-books)

(defn set-max-books [borrower max-books]
  (assoc borrower ::max-books max-books))
(s/fdef set-max-books
  :args (s/cat :borrower ::borrower
               :max-books ::max-books)
  :ret ::borrower)

(defn borrower-to-string [borrower]
  (str (get-name borrower) " (" (get-max-books borrower) " books)"))
(s/fdef borrower-to-string
  :args (s/cat :borrower ::borrower)
  :ret string?)

(ostest/instrument)
