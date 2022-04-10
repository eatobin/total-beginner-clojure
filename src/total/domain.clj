(ns total.domain
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::max-books (s/int-in 1 11))
(s/def :unq/borrower (s/keys :req-un [::name ::max-books]))

(s/def ::title string?)
(s/def ::author string?)
(s/def ::maybe-borrower (s/or :just :unq/borrower :nothing nil?))
(s/def :unq/book (s/keys :req-un [::title ::author ::maybe-borrower]))

(s/def :unq/brs (s/coll-of :unq/borrower :kind list?))
(s/def :unq/bks (s/coll-of :unq/book :kind list?))
(s/def ::extract-fn-br-name
  (s/fspec :args (s/cat :borrower :unq/borrower)
           :ret ::name))
(s/def ::extract-fn-bk-title
  (s/fspec :args (s/cat :book :unq/book)
           :ret ::title))
