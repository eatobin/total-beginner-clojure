(ns total.domain
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::max-books (s/and int? #(>= % 0)))
(s/def ::borrower (s/keys :req [::name ::max-books]))


(s/def ::title string?)
(s/def ::author string?)
(s/def ::maybe-borrower (s/or :just ::borrower :nothing nil?))
(s/def ::book (s/keys :req [::title ::author ::maybe-borrower]))

(s/def ::brs (s/coll-of ::borrower :kind list?))
(s/def ::bks (s/coll-of ::book :kind list?))
(s/def ::extract-fn-br-name
  (s/fspec :args (s/cat :borrower ::borrower)
           :ret ::name))
(s/def ::extract-fn-bk-title
  (s/fspec :args (s/cat :book ::book)
           :ret ::title))
