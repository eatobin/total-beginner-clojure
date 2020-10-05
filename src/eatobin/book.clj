(ns eatobin.book
  (:require [eatobin.borrower :as br]
            [clojure.spec.alpha :as s]
            [orchestra.spec.test :as ostest]))

(s/def ::title string?)
(s/def ::author string?)
(s/def ::maybe-borrower (s/or :just ::br/borrower :nothing nil?))
(s/def ::book (s/keys :req [::title ::author ::maybe-borrower]))

(defn make-book
  ([title author] (make-book title author nil))
  ([title author m-borrower] {::title title, ::author author, ::maybe-borrower m-borrower}))
(s/fdef make-book
        :args (s/cat :title ::title
                     :author ::author
                     :borrower (s/? ::maybe-borrower))
        :ret ::book)

(defn get-title [book]
  (book ::title))
(s/fdef get-title
        :args (s/cat :book ::book)
        :ret ::title)

(defn get-author [book]
  (book ::author))
(s/fdef get-author
        :args (s/cat :book ::book)
        :ret ::author)

(defn get-borrower [book]
  (book ::maybe-borrower))
(s/fdef get-borrower
        :args (s/cat :book ::book)
        :ret ::maybe-borrower)

(defn set-borrower [book borrower]
  (assoc book ::maybe-borrower borrower))
(s/fdef set-borrower
        :args (s/cat :book ::book
                     :borrower ::maybe-borrower)
        :ret ::book)

(defn- available-string [book]
  (let [borrower (get-borrower book)]
    (if (nil? borrower)
      "Available"
      (str
        "Checked out to "
        (br/get-name borrower)))))
(s/fdef available-string
        :args (s/cat :book ::book)
        :ret string?)

(defn book-to-string [book]
  (str
    (get-title book)
    " by "
    (get-author book)
    "; "
    (available-string book)))
(s/fdef book-to-string
        :args (s/cat :book ::book)
        :ret string?)

(ostest/instrument)