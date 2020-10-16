(ns total.book
  (:require [total.domain :as dom]
            [total.borrower :as br]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]))

(defn make-book
  ([title author] (make-book title author nil))
  ([title author m-borrower] {::dom/title title, ::dom/author author, ::dom/maybe-borrower m-borrower}))
(s/fdef make-book
        :args (s/cat :title ::dom/title
                     :author ::dom/author
                     :borrower (s/? ::dom/maybe-borrower))
        :ret ::dom/book)

(defn get-title [book]
  (book ::dom/title))
(s/fdef get-title
        :args (s/cat :book ::dom/book)
        :ret ::dom/title)

(defn get-author [book]
  (book ::dom/author))
(s/fdef get-author
        :args (s/cat :book ::dom/book)
        :ret ::dom/author)

(defn get-borrower [book]
  (book ::dom/maybe-borrower))
(s/fdef get-borrower
        :args (s/cat :book ::dom/book)
        :ret ::dom/maybe-borrower)

(defn set-borrower [book borrower]
  (assoc book ::dom/maybe-borrower borrower))
(s/fdef set-borrower
        :args (s/cat :book ::dom/book
                     :borrower ::dom/maybe-borrower)
        :ret ::dom/book)

(defn- available-string [book]
  (let [borrower (get-borrower book)]
    (if (nil? borrower)
      "Available"
      (str
        "Checked out to "
        (br/get-name borrower)))))
(s/fdef available-string
        :args (s/cat :book ::dom/book)
        :ret string?)

(defn book-to-string [book]
  (str
    (get-title book)
    " by "
    (get-author book)
    "; "
    (available-string book)))
(s/fdef book-to-string
        :args (s/cat :book ::dom/book)
        :ret string?)

(ostest/instrument)
