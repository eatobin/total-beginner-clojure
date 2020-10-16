(ns total.domain
  (:require [clojure.spec.alpha :as s]))

(s/def ::name string?)
(s/def ::max-books (s/and int? #(>= % 0)))
(s/def ::borrower (s/keys :req [::name ::max-books]))


(s/def ::title string?)
(s/def ::author string?)
(s/def ::maybe-borrower (s/or :just ::borrower :nothing nil?))
(s/def ::book (s/keys :req [::title ::author ::maybe-borrower]))
