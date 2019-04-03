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

; (defn get-name [borrower]
;   (borrower :name))
; (s/fdef get-name
;         :args (s/cat :borrower :unq/borrower)
;         :ret ::dom/name)

; (defn set-name [borrower name]
;   (assoc borrower :name name))
; (s/fdef set-name
;         :args (s/cat :borrower :unq/borrower
;                      :name ::dom/name)
;         :ret :unq/borrower)

; (defn get-max-books [borrower]
;   (borrower :max-books))
; (s/fdef get-max-books
;         :args (s/cat :borrower :unq/borrower)
;         :ret ::dom/max-books)

; (defn set-max-books [borrower max-books]
;   (assoc borrower :max-books max-books))
; (s/fdef set-max-books
;         :args (s/cat :borrower :unq/borrower
;                      :max-books ::dom/max-books)
;         :ret :unq/borrower)

; (defn borrower-to-string [borrower]
;   (str (get-name borrower) " (" (get-max-books borrower) " books)"))
; (s/fdef borrower-to-string
;         :args (s/cat :borrower :unq/borrower)
;         :ret string?)

(ostest/instrument)
