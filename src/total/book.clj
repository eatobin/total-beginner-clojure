(ns total.book
  (:require [total.domain :as dom]
            [total.borrower :as br]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]))

(defn make-book
  ([title author] (make-book title author nil))
  ([title author m-borrower] {:title title, :author author, :maybe-borrower m-borrower}))
(s/fdef make-book
        :args (s/cat :title ::dom/title
                     :author ::dom/author
                     :borrower (s/? ::dom/maybe-borrower))
        :ret :unq/book)

(defn get-title [book]
  (:title book))
(s/fdef get-title
        :args (s/cat :book :unq/book)
        :ret ::dom/title)

(defn get-author [book]
  (:author book))
(s/fdef get-author
        :args (s/cat :book :unq/book)
        :ret ::dom/author)

(defn get-borrower [book]
  (:maybe-borrower book))
(s/fdef get-borrower
        :args (s/cat :book :unq/book)
        :ret ::dom/maybe-borrower)

(defn set-borrower [book borrower]
  (assoc book :maybe-borrower borrower))
(s/fdef set-borrower
        :args (s/cat :book :unq/book
                     :borrower ::dom/maybe-borrower)
        :ret :unq/book)

(defn- available-string [book]
  (let [borrower (get-borrower book)]
    (if (nil? borrower)
      "Available"
      (str
        "Checked out to "
        (br/get-name borrower)))))
(s/fdef available-string
        :args (s/cat :book :unq/book)
        :ret string?)

(defn book-to-string [book]
  (str
    (get-title book)
    " by "
    (get-author book)
    "; "
    (available-string book)))
(s/fdef book-to-string
        :args (s/cat :book :unq/book)
        :ret string?)

(ostest/instrument)
